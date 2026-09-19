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

import com.endurancetrio.business.event.dto.EventDTO;
import com.endurancetrio.business.event.dto.EventOverviewDTO;
import com.endurancetrio.business.event.dto.EventsPageDTO;
import com.endurancetrio.business.event.dto.YearsWithEventsDTO;
import java.util.List;
import org.springframework.data.domain.Pageable;

/**
 * The {@link EventService} defines the business operations for managing event data.
 */
public interface EventService {

  /**
   * Returns a list of all distinct years extracted from the events' start dates, ordered
   * descending. This is used by {@link YearsWithEventsDTO} to drive the year-browser pagination on the
   * events landing page.
   *
   * @return a list of distinct event years in descending order
   */
  List<Integer> getEventYears();

  /**
   * Returns an {@link EventsPageDTO} containing the
   * {@link EventDTO events} whose start date falls within the
   * given {@code year}, ordered by start date descending and paginated according to the given
   * {@link Pageable}.
   *
   * @param year     the year to filter events by
   * @param pageable the pagination information (page number, page size, etc.)
   * @return an {@link EventsPageDTO} with the events for the given year and pagination metadata
   */
  EventsPageDTO getEventsByYear(int year, Pageable pageable);

  /**
   * Returns an {@link EventOverviewDTO} containing the summary details of the event identified by
   * the given {@code id} and belonging to the given {@code year}, including its organizers and
   * races.
   *
   * @param id   the unique identifier of the event
   * @param year the year the event belongs to
   * @return an {@link EventOverviewDTO} with the event overview
   * @throws com.endurancetrio.business.common.exception.EnduranceTrioException if no event with the
   *         given {@code id} and {@code year} is found
   */
  EventOverviewDTO getEventOverview(Long id, int year);

  /**
   * Returns a list of {@link EventDTO events} for the given IDs, ordered by their start date
   * descending, from the most recent to the oldest. Events that are not found are silently
   * excluded. Courses are eagerly fetched to derive sport codes without additional queries.
   *
   * @param ids the list of event IDs to fetch
   * @return a list of {@link EventDTO events} matching the given IDs, ordered by start date
   *         descending
   */
  List<EventDTO> getEventsByIds(List<Long> ids);

  /**
   * Returns an {@link EventsPageDTO} containing the most recently added {@link EventDTO events},
   * ordered by their creation timestamp descending. Courses are eagerly fetched to derive sport
   * codes without additional queries.
   *
   * @param pageable the pagination information; use
   *                 {@link org.springframework.data.domain.PageRequest#of(int, int)
   *                 PageRequest.of(0, limit)} to retrieve the most recent N events
   * @return an {@link EventsPageDTO} with the most recently added events and pagination metadata
   */
  EventsPageDTO getMostRecentAddedEvents(Pageable pageable);
}
