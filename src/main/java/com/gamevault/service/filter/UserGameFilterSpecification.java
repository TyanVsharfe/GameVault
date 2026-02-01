package com.gamevault.service.filter;

import com.gamevault.db.model.QGame;
import com.gamevault.db.model.QUserGame;
import com.querydsl.core.types.dsl.BooleanExpression;

@FunctionalInterface
public interface UserGameFilterSpecification {
    BooleanExpression toExpression(QUserGame userGame, QGame game);
}
