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

package com.endurancetrio.business.competitor.service;

import com.endurancetrio.business.common.exception.EnduranceTrioException;
import com.endurancetrio.business.competitor.dto.AthleteDTO;
import com.endurancetrio.business.competitor.dto.AthleteFilterDTO;
import com.endurancetrio.business.competitor.dto.AthleteResultsPageDTO;
import com.endurancetrio.business.competitor.dto.AthletesPageDTO;
import com.endurancetrio.data.event.model.entity.IndividualResult;
import org.springframework.data.domain.Pageable;

/**
 * The {@link AthleteService} defines the business operations for managing athlete data.
 */
public interface AthleteService {

  /**
   * Returns an {@link AthletesPageDTO} containing the filtered, paginated list of athletes.
   *
   * @param pageable the pagination information (page number, page size, etc.)
   * @param filter the normalized directory filters
   * @return an {@link AthletesPageDTO} with the athletes for the current page and pagination
   * metadata
   */
  AthletesPageDTO getAthletes(Pageable pageable, AthleteFilterDTO filter);

  /**
   * Returns the {@link AthleteDTO} for the athlete with the given ID.
   *
   * @param id the unique identifier of the athlete
   * @return the {@link AthleteDTO} for the requested athlete
   * @throws EnduranceTrioException if no athlete with the given ID is found
   */
  AthleteDTO getAthleteById(Long id);

  /**
   * Returns an {@link AthleteResultsPageDTO} containing the paginated list of source results for the
   * athlete with the given ID. A source result is one whose {@link IndividualResult#getSourceResult() sourceResult}
   * is {@code null}. Source results are ordered by race date descending and, as a tiebreaker, by
   * race reference descending. Each source result carries the derived results that were computed
   * from it, ordered by race reference ascending. Pagination counts source results only.
   *
   * @param athleteId the unique identifier of the athlete
   * @param pageable  the pagination information (page number, page size, etc.)
   * @return an {@link AthleteResultsPageDTO} with the results for the athlete and pagination metadata
   */
  AthleteResultsPageDTO getAthleteResults(Long athleteId, Pageable pageable);
}
