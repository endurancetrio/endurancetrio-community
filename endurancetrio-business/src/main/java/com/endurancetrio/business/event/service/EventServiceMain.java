/*
 * Copyright (c) 2011-2026 Ricardo do Canto
 *
 * This file is part of the EnduranceTrio project.
 *
 * Licensed under the Functional Software License (FSL), Version 1.1, ALv2 Future License
 * (the "License");
 *
 * You may not use this file except in compliance with the License. You may obtain a copy
 * of the License at https://fsl.software/
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND WITHOUT WARRANTIES OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF FITNESS FOR A PARTICULAR
 * PURPOSE, MERCHANTABILITY, TITLE OR NON-INFRINGEMENT.
 *
 * IN NO EVENT WILL WE HAVE ANY LIABILITY TO YOU ARISING OUT OF OR RELATED TO THE
 * SOFTWARE, INCLUDING INDIRECT, SPECIAL, INCIDENTAL OR CONSEQUENTIAL DAMAGES,
 * EVEN IF WE HAVE BEEN INFORMED OF THEIR POSSIBILITY IN ADVANCE.
 */

package com.endurancetrio.business.event.service;

import com.endurancetrio.business.common.dto.ErrorDTO;
import com.endurancetrio.business.common.dto.PaginationDTO;
import com.endurancetrio.business.common.exception.EnduranceTrioError;
import com.endurancetrio.business.common.exception.EnduranceTrioException;
import com.endurancetrio.business.event.dto.EventDTO;
import com.endurancetrio.business.event.dto.EventOverviewDTO;
import com.endurancetrio.business.event.dto.EventsPageDTO;
import com.endurancetrio.business.event.mapper.EventMapper;
import com.endurancetrio.data.event.model.entity.Event;
import com.endurancetrio.data.event.repository.EventRepository;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventServiceMain implements EventService {

  private static final Logger LOG = LoggerFactory.getLogger(EventServiceMain.class);

  private final EventRepository eventRepository;
  private final EventMapper eventMapper;

  @Autowired
  public EventServiceMain(EventRepository eventRepository, EventMapper eventMapper) {
    this.eventRepository = eventRepository;
    this.eventMapper = eventMapper;
  }

  @Override
  @Transactional(readOnly = true)
  public List<Integer> getEventYears() {
    return eventRepository.findDistinctYears();
  }

  @Override
  @Transactional(readOnly = true)
  public EventsPageDTO getEventsByYear(int year, Pageable pageable) {
    Page<EventDTO> eventPage = eventRepository.findByEventYear(year, pageable)
        .map(eventMapper::mapToEventDTO);

    return new EventsPageDTO(eventPage.getContent(), PaginationDTO.from(eventPage));
  }

  @Override
  @Cacheable(value = "eventOverview", key = "#id + '-' + #year")
  @Transactional(readOnly = true)
  public EventOverviewDTO getEventOverview(Long id, int year) {
    Event event = eventRepository.findByIdAndYearWithGraph(id, year).orElseThrow(() -> {
      String errorMsg = String.format("No event found with ID %d for year %d", id, year);
      LOG.warn(errorMsg);
      return new EnduranceTrioException(new ErrorDTO(EnduranceTrioError.NOT_FOUND, errorMsg));
    });

    return eventMapper.mapToEventOverviewDTO(event);
  }

  @Override
  @Transactional(readOnly = true)
  public List<EventDTO> getEventsByIds(List<Long> ids) {
    var entities = eventRepository.findEventsByIdInWithCourses(ids);

    return entities.stream()
        .map(eventMapper::mapToEventDTO)
        .filter(Objects::nonNull)
        .sorted(Comparator.comparing(EventDTO::startDate).reversed())
        .toList();
  }

  @Override
  @Cacheable(
      value = "mostRecentAddedEvents",
      key = "#pageable.pageNumber + '-' + #pageable.pageSize"
  )
  @Transactional(readOnly = true)
  public EventsPageDTO getMostRecentAddedEvents(Pageable pageable) {
    Page<Long> eventIds = eventRepository.findMostRecentAddedEventIds(pageable);

    if (eventIds.isEmpty()) {
      return new EventsPageDTO(List.of(), PaginationDTO.from(eventIds));
    }

    List<Long> orderedEventIds = eventIds.getContent();
    List<Event> eventEntities = eventRepository.findEventsByIdInWithCourses(orderedEventIds);

    Map<Long, Event> eventEntitiesMap = HashMap.newHashMap(eventEntities.size());
    for (Event event : eventEntities) {
      eventEntitiesMap.put(event.getId(), event);
    }

    List<EventDTO> events = orderedEventIds.stream()
        .map(eventEntitiesMap::get)
        .filter(Objects::nonNull)
        .map(eventMapper::mapToEventDTO)
        .toList();

    return new EventsPageDTO(events, PaginationDTO.from(eventIds));
  }
}
