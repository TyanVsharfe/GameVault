package com.gamevault.db.repository;

import com.gamevault.dto.input.update.UpdateOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserGameListItemRepository {

    private final JdbcTemplate jdbcTemplate;

    public void updateOrderBatch(UUID listId, List<UpdateOrderDto> order) {
        log.info("Updating order for listId: {}, items: {}", listId, order);

        jdbcTemplate.batchUpdate("""
            UPDATE USER_GAME_LIST_ITEMS
            SET ITEM_ORDER = ?
            WHERE GAME_ID = ? AND USER_GAME_LIST_ID = ?
        """,
                order,
                order.size(),
                (ps, dto) -> {
                    ps.setInt(1, dto.order());
                    ps.setObject(2, dto.igdbId());
                    ps.setObject(3, listId);
                });
    }
}

