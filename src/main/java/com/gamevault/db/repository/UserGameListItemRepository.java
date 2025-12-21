package com.gamevault.db.repository;

import com.gamevault.db.model.UserGameListItem;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserGameListItemRepository extends CrudRepository<UserGameListItem, UUID> {
}
