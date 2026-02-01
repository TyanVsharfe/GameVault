package com.gamevault.service.filter;

import com.gamevault.db.model.QGame;
import com.gamevault.db.model.QUserGame;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.List;
import java.util.Objects;

public class SpecificationComposer {

    public static BooleanExpression compose(QUserGame ug, QGame game, List<UserGameFilterSpecification> specs) {
        return specs.stream()
                .map(spec -> spec.toExpression(ug, game))
                .filter(Objects::nonNull)
                .reduce(BooleanExpression::and)
                .orElse(null);
    }
}
