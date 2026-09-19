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
 * EVEN IF YOU HAVE BEEN INFORMED OF THE POSSIBILITY IN ADVANCE.
 */

package com.endurancetrio.data.event.repository;

import com.endurancetrio.data.common.model.entity.AuditableEntity;
import com.endurancetrio.data.event.model.entity.IndividualResult;
import com.endurancetrio.data.event.model.entity.Race;
import com.endurancetrio.data.event.model.entity.TeamResult;
import com.endurancetrio.data.event.model.enumerator.RaceType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * The {@link RaceRepository} provides database access for {@link Race} entities.
 */
@Repository
public interface RaceRepository extends JpaRepository<@NonNull Race, @NonNull Long> {

  /**
   * Finds a {@link Race} by its ID with its courses and their distances eagerly fetched, avoiding
   * N+1 queries when accessing the full race object graph.
   *
   * @param id the ID of the race to find
   * @return the {@link Race} with its courses and distances loaded, or empty if not found
   */
  @Query(
      "SELECT r FROM Race r "
          + "LEFT JOIN FETCH r.courses c "
          + "LEFT JOIN FETCH c.distance "
          + "WHERE r.id = :id"
  )
  Optional<Race> findByIdWithGraph(@Param("id") Long id);

  /**
   * Returns a {@link Page} of IDs of non-derived {@link Race races} that have at least one
   * {@link IndividualResult} or {@link TeamResult} recorded in the database.
   * <p>
   * Derived race types ({@link RaceType#INDIVIDUAL_DERIVED INDIVIDUAL_DERIVED},
   * {@link RaceType#TEAM_RELAY_DERIVED TEAM_RELAY_DERIVED},
   * {@link RaceType#MIXED_RELAY_DERIVED MIXED_RELAY_DERIVED}) are excluded. Results are ordered
   * by the most recent result creation date
   * ({@link AuditableEntity#getCreatedAt() createdAt} of {@link IndividualResult}
   * or {@link TeamResult}) descending and, as a tiebreaker, by {@link Race#getId() id} descending.
   * <p>
   * This query selects only IDs to avoid the PostgreSQL restriction that ORDER BY expressions
   * must appear in the SELECT list when DISTINCT is used, which occurs when fetching collections
   * with JOIN FETCH. Use {@link #findRacesByIdInWithCoursesAndEvent(Collection)} to load the
   * full entity graph for the returned IDs.
   *
   * @param pageable the pagination information; use
   *                 {@link PageRequest#of(int, int) PageRequest.of(0, limit)} to retrieve the most
   *                 recent N races
   * @return a {@link Page} of race IDs
   */
  @Query(
      value = "SELECT r.id FROM Race r "
          + "WHERE r.raceType NOT IN ('INDIVIDUAL_DERIVED', 'TEAM_RELAY_DERIVED', 'MIXED_RELAY_DERIVED', 'TEAM_BY_RANK', 'TEAM_BY_TIME', 'TEAM_BY_POINTS', 'TEAM_RELAY_PARENT', 'MIXED_RELAY_PARENT') "
          + "AND (r.id IN (SELECT ir.race.id FROM IndividualResult ir) "
          + "OR r.id IN (SELECT tr.race.id FROM TeamResult tr)) "
          + "ORDER BY COALESCE("
          + "  (SELECT MAX(ir2.createdAt) FROM IndividualResult ir2 WHERE ir2.race = r), "
          + "  (SELECT MAX(tr2.createdAt) FROM TeamResult tr2 WHERE tr2.race = r)"
          + ") DESC, r.id DESC",
      countQuery = "SELECT COUNT(r.id) FROM Race r "
          + "WHERE r.raceType NOT IN ('INDIVIDUAL_DERIVED', 'TEAM_RELAY_DERIVED', 'MIXED_RELAY_DERIVED', 'TEAM_BY_RANK', 'TEAM_BY_TIME', 'TEAM_BY_POINTS', 'TEAM_RELAY_PARENT', 'MIXED_RELAY_PARENT') "
          + "AND (r.id IN (SELECT ir.race.id FROM IndividualResult ir) "
          + "OR r.id IN (SELECT tr.race.id FROM TeamResult tr))"
  )
  Page<Long> findNonDerivedRaceIdsWithMostRecentAddedResults(Pageable pageable);

  /**
   * Returns a {@link List} of {@link Race races} matching the given IDs, with their courses and
   * course events eagerly fetched to avoid N+1 queries when mapping to DTOs.
   * <p>
   * The returned list may not preserve the order of the input IDs. The caller is responsible for
   * restoring the desired order after fetching.
   *
   * @param ids the IDs of the races to fetch
   * @return a {@link List} of {@link Race races} with courses and events loaded
   */
  @Query(
      "SELECT DISTINCT r FROM Race r "
          + "LEFT JOIN FETCH r.courses c "
          + "LEFT JOIN FETCH c.event "
          + "WHERE r.id IN :ids"
  )
  List<Race> findRacesByIdInWithCoursesAndEvent(@Param("ids") Collection<Long> ids);
}
