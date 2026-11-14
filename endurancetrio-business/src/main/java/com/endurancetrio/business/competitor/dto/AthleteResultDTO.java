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

package com.endurancetrio.business.competitor.dto;

import com.endurancetrio.business.event.dto.RaceDTO;
import com.endurancetrio.data.event.model.enumerator.Penalty;
import com.endurancetrio.data.event.model.enumerator.RaceType;
import java.time.Duration;
import java.util.List;

/**
 * A simplified view of a result obtained by an athlete in a race, suitable for display on the athlete
 * profile page.
 * <p>
 * The DTO is intentionally result-type agnostic: it can be populated from an individual result as
 * well as from a team result to which the athlete contributed, since both share the same set of
 * display fields (the race context is carried by the wrapped {@link RaceDTO}, which exposes the race
 * id and {@link RaceType} needed for linking and rendering).
 * <p>
 * Results are grouped hierarchically by their source result: a source result carries the derived
 * results (if any) that were computed from it, mirroring the race hierarchy displayed on the
 * profile.
 *
 * @param race            the race context for this result (never null)
 * @param rank            the athlete's (or their team's) finishing position in the race (may be null
 *                        when unranked, e.g. DNF/DNS/DSQ)
 * @param penalty         the penalty applied to the athlete in this race (may be null)
 * @param swim            the swim split time (may be null)
 * @param firstBike       the first bike split time (may be null)
 * @param firstRun        the first run split time (may be null)
 * @param t1              the first transition time (may be null)
 * @param bike            the bike split time (may be null)
 * @param t2              the second transition time (may be null)
 * @param run             the run split time (may be null)
 * @param secondRun       the second run split time (may be null)
 * @param t3              the third transition time (may be null)
 * @param secondBike      the second bike split time (may be null)
 * @param total           the total race time (may be null)
 * @param derivedResults  the derived results of this result (possibly empty; never null), ordered by
 *                        race reference ascending ("older" derived race first)
 */
public record AthleteResultDTO(
    RaceDTO race,
    Integer rank,
    Penalty penalty,
    Duration swim,
    Duration firstBike,
    Duration firstRun,
    Duration t1,
    Duration bike,
    Duration t2,
    Duration run,
    Duration secondRun,
    Duration t3,
    Duration secondBike,
    Duration total,
    List<AthleteResultDTO> derivedResults
) {

  public AthleteResultDTO {
    if (race == null) {
      throw new IllegalArgumentException("race must not be null");
    }
    if (derivedResults == null) {
      throw new IllegalArgumentException("derivedResults must not be null");
    }
  }
}