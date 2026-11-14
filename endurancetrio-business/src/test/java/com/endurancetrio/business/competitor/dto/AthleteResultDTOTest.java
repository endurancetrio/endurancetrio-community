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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.endurancetrio.business.event.dto.RaceDTO;
import com.endurancetrio.data.event.model.enumerator.RaceType;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.Test;

class AthleteResultDTOTest {

  private static final String NULLS = "null, null, null, null, null, null, null, null, null, null, null, null";

  private static RaceDTO createRaceDTO() {
    return new RaceDTO(1L, "Triatlo de Peniche", "geral", LocalDate.of(1984, Month.AUGUST, 15), null, List.of("SWIM"),
        List.of("SPRINT"), RaceType.INDIVIDUAL_PARENT, "INDIVIDUAL", null, null, "UNKNOWN"
    );
  }

  @Test
  void dtoShouldRetainValues() {
    RaceDTO race = createRaceDTO();
    List<AthleteResultDTO> derived = List.of(new AthleteResultDTO(createRaceDTO(), 2, null, null, null, null, null, null,
        null, null, null, null, null, null, List.of()));
    AthleteResultDTO dto = new AthleteResultDTO(race, 3, null, null, null, null, null, null, null, null, null, null,
        null, null, derived);
    assertEquals(3, dto.rank());
    assertEquals(race, dto.race());
    assertEquals(derived, dto.derivedResults());
  }

  @Test
  void shouldAllowNullRank() {
    AthleteResultDTO dto = new AthleteResultDTO(createRaceDTO(), null, null, null, null, null, null, null, null, null,
        null, null, null, null, List.of());
    assertNull(dto.rank());
  }

  @Test
  void shouldRejectNullRace() {
    assertThrows(IllegalArgumentException.class, () -> new AthleteResultDTO(null, 1, null, null, null, null, null, null,
        null, null, null, null, null, null, List.of()));
  }

  @Test
  void shouldRejectNullDerivedResults() {
    RaceDTO race = createRaceDTO();
    assertThrows(IllegalArgumentException.class, () -> new AthleteResultDTO(race, 1, null, null, null, null, null, null,
        null, null, null, null, null, null, null));
  }

  @Test
  void derivedResultsCanBeEmpty() {
    AthleteResultDTO dto = new AthleteResultDTO(createRaceDTO(), 1, null, null, null, null, null, null, null, null, null,
        null, null, null, List.of());
    assertEquals(0, dto.derivedResults().size());
  }
}