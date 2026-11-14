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
 * EVEN IF WE HAVE BEEN INFORMED OF THE POSSIBILITY IN ADVANCE.
 */

package com.endurancetrio.business.event.mapper;

import com.endurancetrio.business.event.dto.DistanceLegDTO;
import com.endurancetrio.business.event.dto.DistanceMetadataDTO;
import com.endurancetrio.business.event.dto.EventDTO;
import com.endurancetrio.business.event.dto.RaceDTO;
import com.endurancetrio.business.event.enumerator.CoreSport;
import com.endurancetrio.business.event.enumerator.RaceTypeGroup;
import com.endurancetrio.data.event.model.entity.AquabikeDistance;
import com.endurancetrio.data.event.model.entity.AquathlonDistance;
import com.endurancetrio.data.event.model.entity.BiathlonDistance;
import com.endurancetrio.data.event.model.entity.Course;
import com.endurancetrio.data.event.model.entity.Distance;
import com.endurancetrio.data.event.model.entity.DoubleBiathlonDistance;
import com.endurancetrio.data.event.model.entity.DuathlonDistance;
import com.endurancetrio.data.event.model.entity.Event;
import com.endurancetrio.data.event.model.entity.Race;
import com.endurancetrio.data.event.model.entity.SingleSportDistance;
import com.endurancetrio.data.event.model.entity.TriathlonDistance;
import com.endurancetrio.data.event.model.enumerator.DistanceType;
import com.endurancetrio.data.event.model.enumerator.Sport;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RaceMapper {

  private static final Logger LOG = LoggerFactory.getLogger(RaceMapper.class);

  /**
   * Maps a {@link Race} entity to a {@link RaceDTO}.
   * <p>
   * This method navigates through the race's courses and its distance to populate the sports
   * and distance metadata fields. The caller must ensure that courses and their distances are
   * eagerly fetched to avoid lazy-initialization errors. This method doesn't populate
   * the {@link RaceDTO#event()} field in the RaceDTO; use {@link #mapWithEvent(Race)}
   * if event data is needed.
   *
   * @param entity the Race entity to be mapped
   * @return the corresponding RaceDTO, or {@code null} if the entity is {@code null}
   */
  public RaceDTO map(Race entity) {
    if (entity == null) {
      return null;
    }

    List<Distance> distances = getDistances(entity.getCourses());

    return new RaceDTO(entity.getId(), entity.getTitle(), entity.getSubtitle(), entity.getDate(),
        entity.getTime(), getSports(entity.getCourses()), getDistanceTypes(distances),
        entity.getRaceType(), getRaceTypeGroup(entity), getDistanceMetadata(distances), null,
        entity.getResultStatus().getCode()
    );
  }

  /**
   * Maps a {@link Race} entity to a {@link RaceDTO} including the parent {@link Event} data.
   * <p>
   * Unlike {@link #map(Race)}, this method navigates through the race's courses to populate the
   * {@link RaceDTO#event()} field with the associated {@link Event} information. The caller must
   * ensure that courses and their events and distances are eagerly fetched to avoid
   * lazy-initialization errors.
   *
   * @param entity the Race entity to be mapped
   * @return the corresponding RaceDTO with event data, or {@code null} if the entity is null
   */
  public RaceDTO mapWithEvent(Race entity) {
    if (entity == null) {
      return null;
    }

    List<Distance> distances = getDistances(entity.getCourses());

    return new RaceDTO(entity.getId(), entity.getTitle(), entity.getSubtitle(), entity.getDate(),
        entity.getTime(), getSports(entity.getCourses()), getDistanceTypes(distances),
        entity.getRaceType(), getRaceTypeGroup(entity), getDistanceMetadata(distances),
        getEvent(entity), entity.getResultStatus().getCode()
    );
  }

  /**
   * Maps a {@link Race} entity to a {@link RaceDTO} with courses and event data but without
   * distance information.
   * <p>
   * Unlike {@link #mapWithEvent(Race)}, this method does not navigate through the courses'
   * distances. The resulting {@link RaceDTO} has a {@code null} distance metadata, and the event
   * populated from the first course. The caller must ensure that courses and their events
   * are eagerly fetched to avoid lazy-initialization errors.
   *
   * @param entity the Race entity to be mapped
   * @return the corresponding RaceDTO with event data, or {@code null} if the entity is {@code null}
   */
  public RaceDTO mapWithoutDistanceWithEvent(Race entity) {
    if (entity == null) {
      return null;
    }

    return new RaceDTO(entity.getId(), entity.getTitle(), entity.getSubtitle(), entity.getDate(),
        entity.getTime(), getSports(entity.getCourses()), List.of(), entity.getRaceType(),
        getRaceTypeGroup(entity), null, getEvent(entity), entity.getResultStatus().getCode()
    );
  }

  /**
   * Builds a {@link DistanceMetadataDTO} from the given list of {@link Distance distances}.
   * <p>
   * Metadata is only produced when the list contains exactly one distance. For multiple distances
   * (e.g., collective races that span multiple courses) or an empty list, this returns
   * {@code null}.
   *
   * @param distances the list of distances extracted from the race's courses
   * @return a {@link DistanceMetadataDTO} with leg breakdown, or {@code null} if conditions are not
   * met
   */
  private DistanceMetadataDTO getDistanceMetadata(List<Distance> distances) {
    if (distances == null || distances.size() != 1) {
      return null;
    }

    Distance distance = distances.getFirst();

    if (distance == null) {
      return null;
    }

    return switch (distance) {
      case AquabikeDistance d -> new DistanceMetadataDTO(
          List.of(new DistanceLegDTO(CoreSport.SWIM.getCode(), d.getSwimDistance()),
              new DistanceLegDTO(CoreSport.BIKE.getCode(), d.getBikeDistance())
          ));
      case AquathlonDistance d -> new DistanceMetadataDTO(
          List.of(new DistanceLegDTO(CoreSport.SWIM.getCode(), d.getSwimDistance()),
              new DistanceLegDTO(CoreSport.RUN.getCode(), d.getRunDistance())
          ));
      case BiathlonDistance d -> new DistanceMetadataDTO(
          List.of(new DistanceLegDTO(CoreSport.BIKE.getCode(), d.getBikeDistance()),
              new DistanceLegDTO(CoreSport.RUN.getCode(), d.getRunDistance())
          ));
      case DoubleBiathlonDistance d -> new DistanceMetadataDTO(
          List.of(new DistanceLegDTO(CoreSport.BIKE.getCode(), d.getFirstBikeDistance()),
              new DistanceLegDTO(CoreSport.RUN.getCode(), d.getFirstRunDistance()),
              new DistanceLegDTO(CoreSport.BIKE.getCode(), d.getSecondBikeDistance()),
              new DistanceLegDTO(CoreSport.RUN.getCode(), d.getSecondRunDistance())
          ));
      case DuathlonDistance d -> new DistanceMetadataDTO(
          List.of(new DistanceLegDTO(CoreSport.RUN.getCode(), d.getFirstRunDistance()),
              new DistanceLegDTO(CoreSport.BIKE.getCode(), d.getBikeDistance()),
              new DistanceLegDTO(CoreSport.RUN.getCode(), d.getSecondRunDistance())
          ));
      case TriathlonDistance d -> new DistanceMetadataDTO(
          List.of(new DistanceLegDTO(CoreSport.SWIM.getCode(), d.getSwimDistance()),
              new DistanceLegDTO(CoreSport.BIKE.getCode(), d.getBikeDistance()),
              new DistanceLegDTO(CoreSport.RUN.getCode(), d.getRunDistance())
          ));
      case SingleSportDistance d ->
          new DistanceMetadataDTO(List.of(new DistanceLegDTO(null, d.getDistance())));
      default -> null;
    };
  }

  /**
   * Extracts a distinct list of {@link Distance distances} from the given set of
   * {@link Course courses}.
   *
   * @param courses the set of courses to extract distances from
   * @return a distinct list of non-null distances, or an empty list if courses is null or empty
   */
  private List<Distance> getDistances(Set<Course> courses) {
    if (courses == null || courses.isEmpty()) {
      return List.of();
    }

    return courses.stream().map(Course::getDistance).distinct().filter(Objects::nonNull).toList();
  }

  /**
   * Extracts the distance type codes from the given list of {@link Distance distances}.
   *
   * @param distances the list of distances to extract types from
   * @return a list of distinct distance type codes, or an empty list if distances is null or empty
   */
  private List<String> getDistanceTypes(List<Distance> distances) {
    if (distances == null || distances.isEmpty()) {
      return List.of();
    }

    return distances.stream()
        .map(Distance::getDistanceType)
        .distinct()
        .filter(Objects::nonNull)
        .map(DistanceType::getCode)
        .toList();
  }

  /**
   * Returns the race type group code for the given {@link Race}.
   * <p>
   * Individual races ({@code INDIVIDUAL_PARENT}, {@code INDIVIDUAL_DERIVED}) map to
   * {@code "INDIVIDUAL"}, team races ({@code TEAM_BY_RANK}, {@code TEAM_BY_TIME},
   * {@code TEAM_BY_POINTS}) map to {@code "COLLECTIVE"}, and relay races map to {@code "RELAY"}.
   *
   * @param race the Race entity whose type group is to be determined
   * @return the race type group code, or {@code null} if the race type is not recognized
   */
  private String getRaceTypeGroup(Race race) {
    switch (race.getRaceType()) {
      case INDIVIDUAL_PARENT, INDIVIDUAL_DERIVED -> {
        return RaceTypeGroup.INDIVIDUAL.getCode();
      }
      case TEAM_BY_RANK, TEAM_BY_TIME, TEAM_BY_POINTS -> {
        return RaceTypeGroup.COLLECTIVE.getCode();
      }
      case TEAM_RELAY_PARENT, TEAM_RELAY_DERIVED, MIXED_RELAY_PARENT, MIXED_RELAY_DERIVED -> {
        return RaceTypeGroup.RELAY.getCode();
      }
      default -> {
        LOG.warn("Unrecognized race type '{}' for race ID {}", race.getRaceType(), race.getId());
        return null;
      }
    }
  }

  /**
   * Extracts the sport codes from the given set of {@link Course courses}.
   *
   * @param courses the set of courses to extract sport codes from
   * @return a list of distinct sport codes, or an empty list if courses is null or empty
   */
  private List<String> getSports(Set<Course> courses) {
    if (courses == null || courses.isEmpty()) {
      return List.of();
    }

    return courses.stream()
        .map(Course::getSport)
        .distinct()
        .filter(Objects::nonNull)
        .map(Sport::getCode)
        .toList();
  }

  /**
   * Extracts the parent {@link Event} from the given {@link Race} and builds an {@link EventDTO}.
   * <p>
   * Uses the first course's event since all courses for a race belong to the same event. By the
   * data contract a race always has at least one course and a course always has an event, so a
   * {@code null} return means the stored data violates that contract; the violation is logged so it
   * can be traced back to the offending race.
   *
   * @param race the race whose event is to be extracted
   * @return an {@link EventDTO} with the event data, or {@code null} if the data violates the
   * contract
   */
  private EventDTO getEvent(Race race) {
    Set<Course> courses = race.getCourses();
    if (courses == null || courses.isEmpty()) {
      LOG.warn("Race {} has no courses; a race must always have at least one course", race.getId());
      return null;
    }

    Event event = courses.iterator().next().getEvent();
    if (event == null) {
      LOG.warn("A course of race {} has no event; a course must always have an event", race.getId());
      return null;
    }

    List<String> sportCodes = courses.stream()
        .map(Course::getSport)
        .filter(Objects::nonNull)
        .map(Sport::getCode)
        .distinct()
        .sorted()
        .toList();

    return new EventDTO(event.getId(), event.getTitle(), event.getStartDate(), event.getEndDate(),
        event.getCity(), event.getCounty(), event.getDistrict(), sportCodes
    );
  }
}
