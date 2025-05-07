package com.nhnacademy.eventlevel.repository.impl;

import com.nhnacademy.eventlevel.domain.EventLevel;
import com.nhnacademy.eventlevel.domain.QEventLevel;
import com.nhnacademy.eventlevel.dto.EventLevelResponse;
import com.nhnacademy.eventlevel.repository.CustomEventLevelRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Optional;

public class CustomEventLevelRepositoryImpl extends QuerydslRepositorySupport implements CustomEventLevelRepository {
    public CustomEventLevelRepositoryImpl() {
        super(EventLevel.class);
    }

    @Override
    public Optional<List<EventLevelResponse>> findAllEventLevel() {
        JPAQuery<EventLevelResponse> query = new JPAQuery<>(getEntityManager());
        QEventLevel qEventLevel = QEventLevel.eventLevel;

        return Optional.of(query
                .select(Projections.constructor(
                        EventLevelResponse.class,
                        qEventLevel.eventLevelName,
                        qEventLevel.eventLevelDetails,
                        qEventLevel.priority
                ))
                .from(qEventLevel)
                .fetch());
    }

    @Override
    public Optional<EventLevelResponse> findEventLevelByLevelName(String levelName) {
        JPAQuery<EventLevelResponse> query = new JPAQuery<>(getEntityManager());
        QEventLevel qEventLevel = QEventLevel.eventLevel;

        return Optional.ofNullable(query
                .select(Projections.constructor(
                        EventLevelResponse.class,
                        qEventLevel.eventLevelName,
                        qEventLevel.eventLevelDetails,
                        qEventLevel.priority
                ))
                .from(qEventLevel)
                .where(qEventLevel.eventLevelName.eq(levelName))
                .fetchOne());
    }
}
