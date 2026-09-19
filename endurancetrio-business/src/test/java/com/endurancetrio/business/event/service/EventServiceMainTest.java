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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.endurancetrio.business.common.exception.EnduranceTrioError;
import com.endurancetrio.business.common.exception.EnduranceTrioException;
import com.endurancetrio.business.event.dto.EventDTO;
import com.endurancetrio.business.event.dto.EventOverviewDTO;
import com.endurancetrio.business.event.dto.EventsPageDTO;
import com.endurancetrio.business.event.mapper.EventMapper;
import com.endurancetrio.data.event.model.entity.Course;
import com.endurancetrio.data.event.model.entity.Event;
import com.endurancetrio.data.event.model.enumerator.Sport;
import com.endurancetrio.data.event.repository.EventRepository;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class EventServiceMainTest {

  private static final Long EVENT_ID = 1L;
  private static final List<Integer> ALL_YEARS = List.of(1989, 1988, 1987, 1986, 1985, 1984);
  private static final int YEAR = 1984;
  private static final Pageable PAGEABLE = PageRequest.of(0, 10);

  @Mock
  EventRepository eventRepository;

  @Mock
  EventMapper eventMapper;

  @InjectMocks
  EventServiceMain underTest;

  @Test
  void getEventYearsShouldReturnAllYears() {
    when(eventRepository.findDistinctYears()).thenReturn(ALL_YEARS);

    List<Integer> result = underTest.getEventYears();

    assertNotNull(result);
    assertEquals(ALL_YEARS, result);
  }

  @Test
  void getEventYearsShouldReturnEmptyWhenNoYears() {
    when(eventRepository.findDistinctYears()).thenReturn(List.of());

    List<Integer> result = underTest.getEventYears();

    assertNotNull(result);
    assertEquals(List.of(), result);
  }

  @Test
  void getEventsByYearShouldReturnEventsWithSportCodes() {
    Event event1 = createEvent(1L, "Event 1", LocalDate.of(1984, Month.AUGUST, 15),
        LocalDate.of(1984, Month.AUGUST, 15),
        "City1", "County1", "District1",
        Set.of(createCourse(Sport.TRIATHLON), createCourse(Sport.ROAD_RUNNING))
    );
    Event event2 = createEvent(2L, "Event 2", LocalDate.of(1984, Month.AUGUST, 1),
        LocalDate.of(1984, Month.AUGUST, 1),
        "City2", "County2", "District2", Set.of(createCourse(Sport.DUATHLON))
    );

    EventDTO expectedDTO1 = new EventDTO(1L, "Event 1", LocalDate.of(1984, Month.AUGUST, 15),
        LocalDate.of(1984, Month.AUGUST, 15), "City1", "County1", "District1",
        List.of("ROAD_RUNNING", "TRIATHLON")
    );
    EventDTO expectedDTO2 = new EventDTO(2L, "Event 2", LocalDate.of(1984, Month.AUGUST, 1),
        LocalDate.of(1984, Month.AUGUST, 1), "City2", "County2", "District2",
        List.of("DUATHLON")
    );

    Page<Event> eventPage = new PageImpl<>(List.of(event1, event2), PAGEABLE, 2L);
    when(eventRepository.findByEventYear(YEAR, PAGEABLE)).thenReturn(eventPage);
    when(eventMapper.mapToEventDTO(event1)).thenReturn(expectedDTO1);
    when(eventMapper.mapToEventDTO(event2)).thenReturn(expectedDTO2);

    EventsPageDTO result = underTest.getEventsByYear(YEAR, PAGEABLE);

    assertNotNull(result);
    assertEquals(2, result.events().size());
    assertEquals(expectedDTO1, result.events().get(0));
    assertEquals(expectedDTO2, result.events().get(1));
    assertEquals(0, result.pagination().pageNumber());
    assertEquals(1, result.pagination().totalPages());
    assertEquals(2L, result.pagination().totalItems());
  }

  @Test
  void getEventsByYearShouldReturnDistinctSortedSportCodes() {
    Event event = createEvent(1L, "Event", LocalDate.of(1984, Month.JUNE, 1),
        LocalDate.of(1984, Month.AUGUST, 15),
        "City", "County", "District",
        Set.of(createCourse(Sport.TRIATHLON), createCourse(Sport.TRIATHLON),
            createCourse(Sport.ROAD_RUNNING)
        )
    );

    EventDTO expectedDTO = new EventDTO(1L, "Event", LocalDate.of(1984, Month.JUNE, 1),
        LocalDate.of(1984, Month.AUGUST, 15), "City", "County", "District",
        List.of("ROAD_RUNNING", "TRIATHLON")
    );

    Page<Event> eventPage = new PageImpl<>(List.of(event), PAGEABLE, 1L);
    when(eventRepository.findByEventYear(YEAR, PAGEABLE)).thenReturn(eventPage);
    when(eventMapper.mapToEventDTO(event)).thenReturn(expectedDTO);

    EventsPageDTO result = underTest.getEventsByYear(YEAR, PAGEABLE);

    assertNotNull(result);
    assertEquals(1, result.events().size());
    assertEquals(expectedDTO, result.events().getFirst());
  }

  @Test
  void getEventsByYearShouldReturnEmptyPage() {
    Page<Event> eventPage = new PageImpl<>(List.of(), PAGEABLE, 0L);
    when(eventRepository.findByEventYear(YEAR, PAGEABLE)).thenReturn(eventPage);

    EventsPageDTO result = underTest.getEventsByYear(YEAR, PAGEABLE);

    assertNotNull(result);
    assertEquals(0, result.events().size());
    assertEquals(0, result.pagination().totalItems());
  }

  @Test
  void getEventsByYearShouldReturnEmptySportCodes() {
    Event event = createEvent(1L, "Event", LocalDate.of(1984, Month.JUNE, 1),
        LocalDate.of(1984, Month.AUGUST, 15),
        "City", "County", "District", Set.of()
    );

    EventDTO expectedDTO = new EventDTO(1L, "Event", LocalDate.of(1984, Month.JUNE, 1),
        LocalDate.of(1984, Month.AUGUST, 15), "City", "County", "District",
        List.of()
    );

    Page<Event> eventPage = new PageImpl<>(List.of(event), PAGEABLE, 1L);
    when(eventRepository.findByEventYear(YEAR, PAGEABLE)).thenReturn(eventPage);
    when(eventMapper.mapToEventDTO(event)).thenReturn(expectedDTO);

    EventsPageDTO result = underTest.getEventsByYear(YEAR, PAGEABLE);

    assertNotNull(result);
    assertEquals(1, result.events().size());
    assertEquals(expectedDTO, result.events().getFirst());
  }

  @Test
  void getEventOverviewShouldReturnEventOverview() {
    Event event = createEvent(EVENT_ID, "Test Event", LocalDate.of(1984, Month.AUGUST, 15),
        LocalDate.of(1984, Month.AUGUST, 15), "City", "County", "District", Set.of()
    );
    EventOverviewDTO expected = mock(EventOverviewDTO.class);

    when(eventRepository.findByIdAndYearWithGraph(EVENT_ID, YEAR)).thenReturn(Optional.of(event));
    when(eventMapper.mapToEventOverviewDTO(event)).thenReturn(expected);

    EventOverviewDTO result = underTest.getEventOverview(EVENT_ID, YEAR);

    assertNotNull(result);
    assertEquals(expected, result);
  }

  @Test
  void getEventOverviewShouldThrowWhenEventNotFound() {
    when(eventRepository.findByIdAndYearWithGraph(EVENT_ID, YEAR)).thenReturn(Optional.empty());

    EnduranceTrioException exception = assertThrows(EnduranceTrioException.class,
        () -> underTest.getEventOverview(EVENT_ID, YEAR)
    );

    assertEquals(EnduranceTrioError.NOT_FOUND.getCode(), exception.getCode());
  }

  @Test
  void getEventsByIdsShouldReturnEventsOrderedByStartDateDescending() {
    Event event1 = createEvent(1L, "Event 1", LocalDate.of(1984, Month.AUGUST, 1),
        LocalDate.of(1984, Month.AUGUST, 1),
        "City1", "County1", "District1", Set.of(createCourse(Sport.TRIATHLON))
    );
    Event event2 = createEvent(2L, "Event 2", LocalDate.of(1984, Month.AUGUST, 15),
        LocalDate.of(1984, Month.AUGUST, 15),
        "City2", "County2", "District2", Set.of(createCourse(Sport.DUATHLON))
    );

    EventDTO expectedDTO1 = new EventDTO(1L, "Event 1", LocalDate.of(1984, Month.AUGUST, 1),
        LocalDate.of(1984, Month.AUGUST, 1), "City1", "County1", "District1",
        List.of("TRIATHLON")
    );
    EventDTO expectedDTO2 = new EventDTO(2L, "Event 2", LocalDate.of(1984, Month.AUGUST, 15),
        LocalDate.of(1984, Month.AUGUST, 15), "City2", "County2", "District2",
        List.of("DUATHLON")
    );

    when(eventRepository.findEventsByIdInWithCourses(List.of(1L, 2L))).thenReturn(
        List.of(event1, event2));
    when(eventMapper.mapToEventDTO(event1)).thenReturn(expectedDTO1);
    when(eventMapper.mapToEventDTO(event2)).thenReturn(expectedDTO2);

    List<EventDTO> result = underTest.getEventsByIds(List.of(1L, 2L));

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(expectedDTO2, result.get(0));
    assertEquals(expectedDTO1, result.get(1));
  }

  @Test
  void getEventsByIdsShouldReturnEmptyList() {
    when(eventRepository.findEventsByIdInWithCourses(List.of(1L))).thenReturn(List.of());

    List<EventDTO> result = underTest.getEventsByIds(List.of(1L));

    assertNotNull(result);
    assertEquals(List.of(), result);
  }

  @Test
  void getEventsByIdsShouldExcludeMissingEvents() {
    Event event1 = createEvent(1L, "Event 1", LocalDate.of(1984, Month.AUGUST, 15),
        LocalDate.of(1984, Month.AUGUST, 15),
        "City1", "County1", "District1", Set.of(createCourse(Sport.TRIATHLON))
    );

    EventDTO expectedDTO1 = new EventDTO(1L, "Event 1", LocalDate.of(1984, Month.AUGUST, 15),
        LocalDate.of(1984, Month.AUGUST, 15), "City1", "County1", "District1",
        List.of("TRIATHLON")
    );

    when(eventRepository.findEventsByIdInWithCourses(List.of(1L, 2L))).thenReturn(List.of(event1));
    when(eventMapper.mapToEventDTO(event1)).thenReturn(expectedDTO1);

    List<EventDTO> result = underTest.getEventsByIds(List.of(1L, 2L));

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(expectedDTO1, result.getFirst());
  }

  @Test
  void getMostRecentAddedEventsShouldReturnEventsWithSportCodes() {
    Event event1 = createEvent(1L, "Event 1", LocalDate.of(2026, Month.JULY, 10),
        LocalDate.of(2026, Month.JULY, 10), "City1", "County1", "District1",
        Set.of(createCourse(Sport.TRIATHLON), createCourse(Sport.ROAD_RUNNING))
    );
    Event event2 = createEvent(2L, "Event 2", LocalDate.of(2026, Month.JULY, 5),
        LocalDate.of(2026, Month.JULY, 5), "City2", "County2", "District2",
        Set.of(createCourse(Sport.DUATHLON))
    );

    EventDTO expectedDTO1 = new EventDTO(1L, "Event 1", LocalDate.of(2026, Month.JULY, 10),
        LocalDate.of(2026, Month.JULY, 10), "City1", "County1", "District1",
        List.of("ROAD_RUNNING", "TRIATHLON")
    );
    EventDTO expectedDTO2 = new EventDTO(2L, "Event 2", LocalDate.of(2026, Month.JULY, 5),
        LocalDate.of(2026, Month.JULY, 5), "City2", "County2", "District2", List.of("DUATHLON")
    );

    Pageable pageable = PageRequest.of(0, 5);
    Page<Long> idPage = new PageImpl<>(List.of(1L, 2L), pageable, 2L);
    when(eventRepository.findMostRecentAddedEventIds(pageable)).thenReturn(idPage);
    when(eventRepository.findEventsByIdInWithCourses(List.of(1L, 2L))).thenReturn(
        List.of(event1, event2));
    when(eventMapper.mapToEventDTO(event1)).thenReturn(expectedDTO1);
    when(eventMapper.mapToEventDTO(event2)).thenReturn(expectedDTO2);

    EventsPageDTO result = underTest.getMostRecentAddedEvents(pageable);

    assertNotNull(result);
    assertEquals(2, result.events().size());
    assertEquals(expectedDTO1, result.events().get(0));
    assertEquals(expectedDTO2, result.events().get(1));
    assertEquals(0, result.pagination().pageNumber());
    assertEquals(1, result.pagination().totalPages());
    assertEquals(2L, result.pagination().totalItems());
  }

  @Test
  void getMostRecentAddedEventsShouldReturnEmptyPage() {
    Pageable pageable = PageRequest.of(0, 5);
    Page<Long> emptyIdPage = new PageImpl<>(List.of(), pageable, 0L);
    when(eventRepository.findMostRecentAddedEventIds(pageable)).thenReturn(emptyIdPage);

    EventsPageDTO result = underTest.getMostRecentAddedEvents(pageable);

    assertNotNull(result);
    assertEquals(0, result.events().size());
    assertEquals(0, result.pagination().totalItems());
  }

  @Test
  void getMostRecentAddedEventsShouldReturnEmptySportCodes() {
    Event event = createEvent(1L, "Event", LocalDate.of(2026, Month.JULY, 1),
        LocalDate.of(2026, Month.JULY, 1), "City", "County", "District", Set.of()
    );

    EventDTO expectedDTO = new EventDTO(1L, "Event", LocalDate.of(2026, Month.JULY, 1),
        LocalDate.of(2026, Month.JULY, 1), "City", "County", "District", List.of()
    );

    Pageable pageable = PageRequest.of(0, 5);
    Page<Long> idPage = new PageImpl<>(List.of(1L), pageable, 1L);
    when(eventRepository.findMostRecentAddedEventIds(pageable)).thenReturn(idPage);
    when(eventRepository.findEventsByIdInWithCourses(List.of(1L))).thenReturn(List.of(event));
    when(eventMapper.mapToEventDTO(event)).thenReturn(expectedDTO);

    EventsPageDTO result = underTest.getMostRecentAddedEvents(pageable);

    assertNotNull(result);
    assertEquals(1, result.events().size());
    assertEquals(expectedDTO, result.events().getFirst());
  }

  private Event createEvent(
      Long id, String title, LocalDate startDate, LocalDate endDate,
      String city, String county, String district, Set<Course> courses
  ) {
    Event event = new Event();
    event.setId(id);
    event.setTitle(title);
    event.setStartDate(startDate);
    event.setEndDate(endDate);
    event.setCity(city);
    event.setCounty(county);
    event.setDistrict(district);
    event.setCourses(courses);
    return event;
  }

  private Course createCourse(Sport sport) {
    Course course = new Course();
    course.setSport(sport);
    return course;
  }
}
