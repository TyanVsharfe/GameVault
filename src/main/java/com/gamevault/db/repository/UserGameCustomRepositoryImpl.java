package com.gamevault.db.repository;

import com.gamevault.db.model.*;
import com.gamevault.dto.input.UserGamesFilterParams;
import com.gamevault.dto.output.db.UserGameBaseData;
import com.gamevault.dto.output.db.UserGameBatchData;
import com.gamevault.dto.output.enriched.UserModeDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

@Repository
public class UserGameCustomRepositoryImpl implements UserGameCustomRepository {
    private final JPAQueryFactory queryFactory;

    public UserGameCustomRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<UserGame> findGamesWithFilters(UserGamesFilterParams params, String username, Pageable pageable) {
        QUserGame ug = QUserGame.userGame;
        QGame game = QGame.game;

        BooleanBuilder where = new BooleanBuilder();

        if (username != null) {
            where.and(ug.user.username.eq(username));
        }

        if (params.getStatus() != null) {
            where.and(ug.status.eq(params.getStatus()));
        }

        if (params.getMinRating() != null) {
            where.and(ug.userRating.goe(params.getMinRating()));
        }

        if (params.getMaxRating() != null) {
            where.and(ug.userRating.loe(params.getMaxRating()));
        }

        if (params.getHasReview() != null) {
            if (params.getHasReview()) {
                where.and(ug.review.isNotNull());
            } else {
                where.and(ug.review.isNull());
            }
        }

        if (params.getIsFullyCompleted() != null) {
            where.and(ug.isFullyCompleted.eq(params.getIsFullyCompleted()));
        }

        if (params.getGameType() != null) {
            where.and(ug.game.category.eq(params.getGameType()));
        }

        if (params.getTitle() != null) {
            where.and(game.title.containsIgnoreCase(params.getTitle()));
        }

        if (params.getCreatedAfter() != null) {
            where.and(ug.createdAt.goe(Instant.from(params.getCreatedAfter().atStartOfDay(ZoneId.systemDefault()))));
        }

        if (params.getCreatedBefore() != null) {
            where.and(ug.createdAt.loe(Instant.from(params.getCreatedBefore().atStartOfDay(ZoneId.systemDefault()))));
        }

        long total = queryFactory
                .selectFrom(ug)
                .leftJoin(ug.game, game)
                .where(where)
                .stream().count();

        List<UserGame> content = queryFactory
                .selectFrom(ug)
                .leftJoin(ug.game, game).fetchJoin()
                .where(where)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(buildOrderSpecifier(pageable.getSort(), ug))
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Optional<UserGameBaseData> getUserGameBaseData(Long igdbId, String username) {
        QUserGame ug = QUserGame.userGame;
        QNote note = QNote.note;

        UserGameBaseData result = queryFactory
                .select(Projections.constructor(
                        UserGameBaseData.class,
                        ug.id,
                        ug.status,
                        ug.userRating,
                        ug.review,
                        ug.isFullyCompleted,
                        ug.isOverallRating,
                        ug.isOverallStatus,
                        ug.userCoverUrl,
                        ug.createdAt,
                        ug.updatedAt,
                        note.countDistinct()
                ))
                .from(ug)
                .leftJoin(ug.notes, note)
                .where(
                        ug.user.username.eq(username),
                        ug.game.igdbId.eq(igdbId)
                )
                .groupBy(ug.id)
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<UserModeDto> findUserModes(Long userGameId) {
        QUserGameMode mode = QUserGameMode.userGameMode;

        return queryFactory
                .select(Projections.constructor(
                        UserModeDto.class,
                        mode.id,
                        mode.mode,
                        mode.status,
                        mode.userRating
                ))
                .from(mode)
                .where(mode.userGame.id.eq(userGameId))
                .fetch();
    }

    @Override
    public List<UserGameBatchData> getUserGamesBaseDataBatch(String username, Set<Long> igdbIds) {
        QUserGame ug = QUserGame.userGame;
        QGame game = QGame.game;
        QNote note = QNote.note;

        return queryFactory
                .select(Projections.constructor(
                        UserGameBatchData.class,
                        ug.id,
                        game.igdbId,
                        ug.status,
                        ug.userRating,
                        ug.review,
                        ug.isFullyCompleted,
                        ug.isOverallRating,
                        ug.isOverallStatus,
                        ug.userCoverUrl,
                        ug.createdAt,
                        ug.updatedAt,
                        note.countDistinct()
                ))
                .from(ug)
                .join(ug.game, game)
                .leftJoin(ug.notes, note)
                .where(
                        ug.user.username.eq(username),
                        game.igdbId.in(igdbIds)
                )
                .groupBy(ug.id, game.igdbId)
                .fetch();
    }

    @Override
    public Double calculateAverageRating(String username) {
        QUserGame ug = QUserGame.userGame;

        return queryFactory
                .select(ug.userRating.avg())
                .from(ug)
                .where(
                        ug.user.username.eq(username),
                        ug.userRating.isNotNull()
                )
                .fetchOne();
    }

    private OrderSpecifier<?>[] buildOrderSpecifier(Sort sort, QUserGame ug) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        for (Sort.Order order : sort) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            OrderSpecifier<?> orderSpecifier = switch (order.getProperty()) {
                case "createdAt" -> new OrderSpecifier<>(direction, ug.createdAt);
                case "updatedAt" -> new OrderSpecifier<>(direction, ug.updatedAt);
                case "userRating" -> new OrderSpecifier<>(direction, ug.userRating);
                case "status" -> new OrderSpecifier<>(direction, ug.status);
                case "title" -> new OrderSpecifier<>(direction, ug.game.title);
                default -> new OrderSpecifier<>(direction, ug.createdAt);
            };

            orders.add(orderSpecifier);
        }

        return orders.toArray(new OrderSpecifier[0]);
    }
}

