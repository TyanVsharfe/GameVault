package com.gamevault.db.repository;

import com.gamevault.db.model.*;
import com.gamevault.dto.input.UserGamesFilterParams;
import com.gamevault.dto.output.db.UserGameBaseData;
import com.gamevault.dto.output.db.UserGameBatchData;
import com.gamevault.dto.output.enriched.UserModeDto;
import com.gamevault.service.filter.SpecificationComposer;
import com.gamevault.service.filter.UserGameFilterFactory;
import com.gamevault.service.filter.UserGameFilterSpecification;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserGameCustomRepositoryImpl implements UserGameCustomRepository {
    private final JPAQueryFactory queryFactory;
    private final UserGameFilterFactory filterFactory;

    public UserGameCustomRepositoryImpl(JPAQueryFactory queryFactory, UserGameFilterFactory filterFactory) {
        this.queryFactory = queryFactory;
        this.filterFactory = filterFactory;
    }

    @Override
    public Page<UserGame> findGamesWithFilters(UserGamesFilterParams params, String username, Pageable pageable) {
        QUserGame ug = QUserGame.userGame;
        QGame game = QGame.game;

        List<UserGameFilterSpecification> specs = filterFactory.createFilters(params, username);

        BooleanExpression expression = SpecificationComposer.compose(ug, game, specs);

        long total = queryFactory
                .selectFrom(ug)
                .leftJoin(ug.game, game)
                .where(expression)
                .stream().count();

        List<UserGame> content = queryFactory
                .selectFrom(ug)
                .leftJoin(ug.game, game).fetchJoin()
                .where(expression)
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

