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

package com.endurancetrio.data.event.repository;

import com.endurancetrio.data.event.model.entity.IndividualResult;
import java.util.Collection;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * The {@link IndividualResultRepository} provides database access for {@link IndividualResult}
 * entities.
 */
@Repository
public interface IndividualResultRepository
    extends JpaRepository<@NonNull IndividualResult, @NonNull Long> {


  /**
   * Returns a {@link List} of {@link IndividualResult IndividualResult} for the race with the given
   * {@code raceId}, ordered by their {@link IndividualResult#getRank() rank} ascending.
   * <p>
   * The athlete, team, and source result are eagerly fetched to avoid N+1 queries when mapping to
   * DTOs. For derived results (where {@link IndividualResult#getSourceResult() sourceResult} is not
   * null), the triathlon performance fields must be read from the source result.
   *
   * @param raceId the ID of the race to fetch results for
   * @return a {@link List} of {@link IndividualResult} for the given race, ordered by rank
   */
  @Query(
      "SELECT ir FROM IndividualResult ir "
          + "LEFT JOIN FETCH ir.athlete "
          + "LEFT JOIN FETCH ir.team "
          + "LEFT JOIN FETCH ir.sourceResult sr "
          + "LEFT JOIN FETCH sr.team "
          + "WHERE ir.race.id = :raceId "
          + "ORDER BY ir.rank ASC"
  )
  List<IndividualResult> findByRaceIdWithGraph(@Param("raceId") Long raceId);

  /**
   * Returns the derived {@link IndividualResult individual results} of the athlete with the given
   * {@code athleteId} whose {@code sourceResult} is among {@code sourceResultIds}, with their race
   * graph ({@code course → distance, event}) eagerly fetched. The query is intentionally not
   * paginated: it is only ever called with the IDs of a single page's source results.
   * <p>
   * The athlete predicate is a guard against violations of the data contract (a derived result
   * must belong to the same athlete as its source result), so that a corrupt row can never surface
   * another athlete's result on this athlete's profile.
   *
   * @param athleteId       the ID of the athlete that must own the derived results
   * @param sourceResultIds the IDs of the source results
   * @return the athlete's derived individual results with their race graph
   */
  @Query(
      "SELECT ir FROM IndividualResult ir "
          + "LEFT JOIN FETCH ir.race r "
          + "LEFT JOIN FETCH r.courses c "
          + "LEFT JOIN FETCH c.event e "
          + "LEFT JOIN FETCH c.distance d "
          + "WHERE ir.sourceResult.id IN :sourceResultIds AND ir.athlete.id = :athleteId"
  )
  List<IndividualResult> findDerivedResultsBySourceResultIdsWithRaceGraph(
      @Param("athleteId") Long athleteId,
      @Param("sourceResultIds") Collection<Long> sourceResultIds
  );

  /**
   * Returns the {@link IndividualResult individual results} whose IDs are in {@code ids}, with their
   * race graph ({@code course → distance, event}) eagerly fetched. The query is intentionally not
   * paginated: it is only ever called with the IDs of a single page.
   *
   * @param ids the IDs of the results to fetch
   * @return the matching individual results with their race graph
   */
  @Query(
      "SELECT ir FROM IndividualResult ir "
          + "LEFT JOIN FETCH ir.race r "
          + "LEFT JOIN FETCH r.courses c "
          + "LEFT JOIN FETCH c.event e "
          + "LEFT JOIN FETCH c.distance d "
          + "WHERE ir.id IN :ids"
  )
  List<IndividualResult> findResultsByIdInWithRaceGraph(@Param("ids") Collection<Long> ids);

  /**
   * Returns a {@link Page} of IDs of the source {@link IndividualResult individual results} of the
   * athlete with the given {@code athleteId}, ordered by their race date descending and, as a
   * tiebreaker, by their race reference descending. A source result is one whose
   * {@code sourceResult} is {@code null}; the results derived from it are resolved separately
   * through {@link #findDerivedResultsBySourceResultIdsWithRaceGraph(Long, Collection)}.
   * <p>
   * Only IDs are selected (no joins, no collection fetch) so SQL {@code LIMIT}/{@code OFFSET} is
   * safe and no in-memory pagination occurs. The planner drives from
   * {@code idx_individual_result_athlete_id}.
   *
   * @param athleteId the ID of the athlete
   * @param pageable  the pagination information
   * @return a {@link Page} of source result IDs for the given athlete
   */
  @Query(
      value = "SELECT ir.id FROM IndividualResult ir "
          + "WHERE ir.athlete.id = :athleteId AND ir.sourceResult IS NULL "
          + "ORDER BY ir.race.date DESC, ir.race.raceReference DESC",
      countQuery = "SELECT COUNT(ir.id) FROM IndividualResult ir "
          + "WHERE ir.athlete.id = :athleteId AND ir.sourceResult IS NULL"
  )
  Page<Long> findSourceResultIdsByAthleteId(@Param("athleteId") Long athleteId, Pageable pageable);
}
