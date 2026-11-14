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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.endurancetrio.business.common.exception.EnduranceTrioException;
import com.endurancetrio.business.competitor.dto.AthleteDTO;
import com.endurancetrio.business.competitor.dto.AthleteFilterDTO;
import com.endurancetrio.business.competitor.dto.AthleteResultDTO;
import com.endurancetrio.business.competitor.dto.AthleteResultsPageDTO;
import com.endurancetrio.business.competitor.dto.AthletesPageDTO;
import com.endurancetrio.business.competitor.fixtures.AthleteDTOFixtures;
import com.endurancetrio.business.competitor.fixtures.AthleteFixture;
import com.endurancetrio.business.competitor.mapper.AthleteMapper;
import com.endurancetrio.business.event.dto.RaceDTO;
import com.endurancetrio.business.event.mapper.RaceMapper;
import com.endurancetrio.data.competitor.model.entity.Athlete;
import com.endurancetrio.data.competitor.repository.AthleteRepository;
import com.endurancetrio.data.competitor.repository.projection.AthleteFirstLetterCount;
import com.endurancetrio.data.event.model.entity.IndividualResult;
import com.endurancetrio.data.event.model.entity.Race;
import com.endurancetrio.data.event.model.enumerator.GenderCategory;
import com.endurancetrio.data.event.model.enumerator.RaceStatus;
import com.endurancetrio.data.event.model.enumerator.RaceType;
import com.endurancetrio.data.event.repository.IndividualResultRepository;
import com.endurancetrio.data.event.repository.RaceRepository;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class AthleteServiceMainTest {

  private static final Pageable PAGEABLE = PageRequest.of(0, 30);
  private static final AthleteFilterDTO FILTER = new AthleteFilterDTO(null, null, null);
  private static final Long ATHLETE_ID = 1L;
  private static final String RACE_REFERENCE = "19840815NAC001-001";

  @Mock
  private AthleteRepository athleteRepository;

  @Mock
  private IndividualResultRepository individualResultRepository;

  @Mock
  private AthleteMapper athleteMapper;

  @Mock
  private RaceMapper raceMapper;


  @InjectMocks
  private AthleteServiceMain underTest;

  @Test
  void getAthletesShouldReturnPageWithAthletes() {
    Athlete testCarvalhoEntity = AthleteFixture.standard();
    Athlete testCavaleiroEntity = AthleteFixture.athleteCavaleiro();
    Athlete testBelloEntity = AthleteFixture.athleteBello();

    AthleteDTO testCarvalhoDTO = AthleteDTOFixtures.standard();
    AthleteDTO testCavaleiroDTO = AthleteDTOFixtures.athleteCavaleiro();
    AthleteDTO testBelloDTO = AthleteDTOFixtures.athleteBello();

    Page<Athlete> athletePage = new PageImpl<>(
        List.of(testCarvalhoEntity, testCavaleiroEntity, testBelloEntity), PAGEABLE, 3L);

    when(athleteRepository.findAll(org.mockito.ArgumentMatchers.<Specification<Athlete>>any(),
        any(Pageable.class)
    )).thenReturn(athletePage);
    when(athleteMapper.map(testCarvalhoEntity)).thenReturn(testCarvalhoDTO);
    when(athleteMapper.map(testCavaleiroEntity)).thenReturn(testCavaleiroDTO);
    when(athleteMapper.map(testBelloEntity)).thenReturn(testBelloDTO);

    AthletesPageDTO result = underTest.getAthletes(PAGEABLE, FILTER);

    assertNotNull(result);
    assertEquals(3, result.athletes().size());
    assertEquals(testCarvalhoDTO, result.athletes().get(0));
    assertEquals(testCavaleiroDTO, result.athletes().get(1));
    assertEquals(testBelloDTO, result.athletes().get(2));
    assertEquals(0, result.pagination().pageNumber());
    assertEquals(1, result.pagination().totalPages());
    assertEquals(3L, result.pagination().totalItems());
  }

  @Test
  void getAthletesShouldReturnEmptyPage() {
    Page<Athlete> emptyPage = new PageImpl<>(List.of(), PAGEABLE, 0L);
    when(athleteRepository.findAll(org.mockito.ArgumentMatchers.<Specification<Athlete>>any(),
        any(Pageable.class)
    )).thenReturn(emptyPage);

    AthletesPageDTO result = underTest.getAthletes(PAGEABLE, FILTER);

    assertNotNull(result);
    assertTrue(result.athletes().isEmpty());
    assertEquals(0, result.pagination().totalItems());
  }

  @Test
  void getAthletesShouldBuildGlobalLetterAvailability() {
    Page<Athlete> athletePage = new PageImpl<>(List.of(), PAGEABLE, 0L);
    when(athleteRepository.findAll(org.mockito.ArgumentMatchers.<Specification<Athlete>>any(),
        any(Pageable.class)
    )).thenReturn(athletePage);
    when(athleteRepository.findGlobalFirstLetterCounts()).thenReturn(
        List.of(firstLetterCount("a", 2), firstLetterCount("j", 3), firstLetterCount("r", 4), firstLetterCount("s", 5),
            firstLetterCount("1", 6)
        ));

    AthletesPageDTO result = underTest.getAthletes(PAGEABLE, FILTER);

    assertEquals(List.of("A_F", "G_L", "M_R", "S_Z"), result.letterRanges().stream().map(range -> range.id()).toList());
    assertEquals(List.of(2L, 3L, 4L, 5L), result.letterRanges().stream().map(range -> range.athleteCount()).toList());
  }

  @Test
  void getAthleteByIdShouldReturnAthlete() {
    Athlete testAthlete = AthleteFixture.standard();
    AthleteDTO testAthleteDTO = AthleteDTOFixtures.standard();

    when(athleteRepository.findById(ATHLETE_ID)).thenReturn(Optional.of(testAthlete));
    when(athleteMapper.map(testAthlete)).thenReturn(testAthleteDTO);

    AthleteDTO result = underTest.getAthleteById(ATHLETE_ID);

    assertNotNull(result);
    assertEquals(testAthleteDTO, result);
  }

  @Test
  void getAthleteByIdShouldThrowWhenNotFound() {
    when(athleteRepository.findById(ATHLETE_ID)).thenReturn(Optional.empty());

    EnduranceTrioException exception = assertThrows(EnduranceTrioException.class,
        () -> underTest.getAthleteById(ATHLETE_ID)
    );

    assertEquals(404, exception.getCode());
  }

  @Test
  void getAthleteResultsShouldReturnSourceWithoutDerived() {
    Race race1 = createRace(1L, "Race 1", LocalDate.of(1984, Month.AUGUST, 15), RaceType.INDIVIDUAL_PARENT);

    RaceDTO race1DTO = new RaceDTO(1L, "Race 1", "Subtitle 1", LocalDate.of(1984, Month.AUGUST, 15),
        null, List.of("SWIM"), List.of("SPRINT"), RaceType.INDIVIDUAL_PARENT, "INDIVIDUAL", null,
        null, "UNKNOWN"
    );

    IndividualResult result1 = new IndividualResult();
    result1.setId(10L);
    result1.setRace(race1);
    result1.setRank(5);

    Page<Long> idPage = new PageImpl<>(List.of(10L), PAGEABLE, 1L);

    when(individualResultRepository.findSourceResultIdsByAthleteId(ATHLETE_ID, PAGEABLE)).thenReturn(idPage);
    when(individualResultRepository.findResultsByIdInWithRaceGraph(List.of(10L))).thenReturn(List.of(result1));
    when(individualResultRepository.findDerivedResultsBySourceResultIdsWithRaceGraph(ATHLETE_ID, List.of(10L)))
        .thenReturn(List.of());
    when(raceMapper.mapWithEvent(race1)).thenReturn(race1DTO);

    AthleteResultsPageDTO result = underTest.getAthleteResults(ATHLETE_ID, PAGEABLE);

    assertNotNull(result);
    assertEquals(1, result.results().size());
    assertEquals(5, result.results().get(0).rank());
    assertEquals(race1DTO, result.results().get(0).race());
    assertTrue(result.results().get(0).derivedResults().isEmpty());
    assertEquals(0, result.pagination().pageNumber());
    assertEquals(1, result.pagination().totalPages());
    assertEquals(1L, result.pagination().totalItems());
  }

  @Test
  void getAthleteResultsShouldReturnEmptyPage() {
    Page<Long> emptyPage = new PageImpl<>(List.of(), PAGEABLE, 0L);
    when(individualResultRepository.findSourceResultIdsByAthleteId(ATHLETE_ID, PAGEABLE)).thenReturn(
        emptyPage);

    AthleteResultsPageDTO result = underTest.getAthleteResults(ATHLETE_ID, PAGEABLE);

    assertNotNull(result);
    assertTrue(result.results().isEmpty());
    assertEquals(0, result.pagination().totalItems());
  }

  @Test
  void getAthleteResultsShouldReturnGroupedRaces() {
    Race parent = createRace(1L, "Parent", LocalDate.of(1984, Month.AUGUST, 15), RaceType.INDIVIDUAL_PARENT);
    Race derived = createRace(2L, "Derived", LocalDate.of(1984, Month.AUGUST, 16), RaceType.INDIVIDUAL_DERIVED);

    RaceDTO parentDTO = new RaceDTO(1L, "Parent", "Subtitle 1", LocalDate.of(1984, Month.AUGUST, 15),
        null, List.of("SWIM"), List.of("SPRINT"), RaceType.INDIVIDUAL_PARENT, "INDIVIDUAL", null,
        null, "UNKNOWN"
    );
    RaceDTO derivedDTO = new RaceDTO(2L, "Derived", "Subtitle 2", LocalDate.of(1984, Month.AUGUST, 16),
        null, List.of("SWIM"), List.of("SPRINT"), RaceType.INDIVIDUAL_DERIVED, "INDIVIDUAL", null,
        null, "UNKNOWN"
    );

    IndividualResult sourceResult = new IndividualResult();
    sourceResult.setId(10L);
    sourceResult.setRace(parent);
    sourceResult.setRank(1);

    IndividualResult derivedResult = new IndividualResult();
    derivedResult.setId(20L);
    derivedResult.setRace(derived);
    derivedResult.setRank(2);
    derivedResult.setSourceResult(sourceResult);

    Page<Long> idPage = new PageImpl<>(List.of(10L), PAGEABLE, 1L);

    when(individualResultRepository.findSourceResultIdsByAthleteId(ATHLETE_ID, PAGEABLE)).thenReturn(idPage);
    when(individualResultRepository.findResultsByIdInWithRaceGraph(List.of(10L))).thenReturn(List.of(sourceResult));
    when(individualResultRepository.findDerivedResultsBySourceResultIdsWithRaceGraph(ATHLETE_ID, List.of(10L)))
        .thenReturn(List.of(derivedResult));
    when(raceMapper.mapWithEvent(parent)).thenReturn(parentDTO);
    when(raceMapper.mapWithEvent(derived)).thenReturn(derivedDTO);

    AthleteResultsPageDTO result = underTest.getAthleteResults(ATHLETE_ID, PAGEABLE);

    assertNotNull(result);
    assertEquals(1, result.results().size());
    assertEquals(parentDTO, result.results().get(0).race());
    assertEquals(List.of(new AthleteResultDTO(derivedDTO, 2, null, null, null, null, null, null, null, null, null, null, null, null, List.of())), result.results().get(0).derivedResults());
    assertEquals(1L, result.pagination().totalItems());
  }

  @Test
  void getAthleteResultsShouldSortDerivedRacesOfDifferentDatesByDateDescending() {
    Race parent = createRace(1L, "Parent", LocalDate.of(1984, Month.AUGUST, 15), RaceType.INDIVIDUAL_PARENT);
    Race derivedOld = createRace(2L, "Derived Old", LocalDate.of(1984, Month.AUGUST, 16), RaceType.INDIVIDUAL_DERIVED);
    Race derivedNew = createRace(3L, "Derived New", LocalDate.of(1984, Month.AUGUST, 17), RaceType.INDIVIDUAL_DERIVED);

    RaceDTO parentDTO = new RaceDTO(1L, "Parent", "Subtitle 1", LocalDate.of(1984, Month.AUGUST, 15),
        null, List.of("SWIM"), List.of("SPRINT"), RaceType.INDIVIDUAL_PARENT, "INDIVIDUAL", null,
        null, "UNKNOWN"
    );
    RaceDTO derivedOldDTO = new RaceDTO(2L, "Derived Old", "Subtitle 2", LocalDate.of(1984, Month.AUGUST, 16),
        null, List.of("SWIM"), List.of("SPRINT"), RaceType.INDIVIDUAL_DERIVED, "INDIVIDUAL", null,
        null, "UNKNOWN"
    );
    RaceDTO derivedNewDTO = new RaceDTO(3L, "Derived New", "Subtitle 3", LocalDate.of(1984, Month.AUGUST, 17),
        null, List.of("SWIM"), List.of("SPRINT"), RaceType.INDIVIDUAL_DERIVED, "INDIVIDUAL", null,
        null, "UNKNOWN"
    );

    IndividualResult sourceResult = new IndividualResult();
    sourceResult.setId(10L);
    sourceResult.setRace(parent);
    sourceResult.setRank(1);

    IndividualResult derivedOldResult = new IndividualResult();
    derivedOldResult.setId(20L);
    derivedOldResult.setRace(derivedOld);
    derivedOldResult.setRank(2);
    derivedOldResult.setSourceResult(sourceResult);

    IndividualResult derivedNewResult = new IndividualResult();
    derivedNewResult.setId(30L);
    derivedNewResult.setRace(derivedNew);
    derivedNewResult.setRank(3);
    derivedNewResult.setSourceResult(sourceResult);

    Page<Long> idPage = new PageImpl<>(List.of(10L), PAGEABLE, 1L);

    when(individualResultRepository.findSourceResultIdsByAthleteId(ATHLETE_ID, PAGEABLE)).thenReturn(idPage);
    when(individualResultRepository.findResultsByIdInWithRaceGraph(List.of(10L))).thenReturn(List.of(sourceResult));
    when(individualResultRepository.findDerivedResultsBySourceResultIdsWithRaceGraph(ATHLETE_ID, List.of(10L)))
        .thenReturn(List.of(derivedOldResult, derivedNewResult));
    when(raceMapper.mapWithEvent(parent)).thenReturn(parentDTO);
    when(raceMapper.mapWithEvent(derivedOld)).thenReturn(derivedOldDTO);
    when(raceMapper.mapWithEvent(derivedNew)).thenReturn(derivedNewDTO);

    AthleteResultsPageDTO result = underTest.getAthleteResults(ATHLETE_ID, PAGEABLE);

    assertNotNull(result);
    assertEquals(2, result.results().get(0).derivedResults().size());
    assertEquals(new AthleteResultDTO(derivedNewDTO, 3, null, null, null, null, null, null, null, null, null, null, null, null, List.of()), result.results().get(0).derivedResults().get(0));
    assertEquals(new AthleteResultDTO(derivedOldDTO, 2, null, null, null, null, null, null, null, null, null, null, null, null, List.of()), result.results().get(0).derivedResults().get(1));
  }

  @Test
  void getAthleteResultsShouldSortDerivedRacesByRaceReferenceAscending() {
    Race parent = createRace(1L, "Parent", LocalDate.of(1984, Month.AUGUST, 15), RaceType.INDIVIDUAL_PARENT);
    Race derivedOlder = createRace(2L, "Derived Older", LocalDate.of(1984, Month.AUGUST, 15),
        RaceType.INDIVIDUAL_DERIVED, "19840815NAC001-002");
    Race derivedNewer = createRace(3L, "Derived Newer", LocalDate.of(1984, Month.AUGUST, 15),
        RaceType.INDIVIDUAL_DERIVED, "19840815NAC001-003");

    RaceDTO parentDTO = new RaceDTO(1L, "Parent", "Subtitle 1", LocalDate.of(1984, Month.AUGUST, 15),
        null, List.of("SWIM"), List.of("SPRINT"), RaceType.INDIVIDUAL_PARENT, "INDIVIDUAL", null,
        null, "UNKNOWN"
    );
    RaceDTO derivedOlderDTO = new RaceDTO(2L, "Derived Older", "Subtitle 2", LocalDate.of(1984, Month.AUGUST, 15),
        null, List.of("SWIM"), List.of("SPRINT"), RaceType.INDIVIDUAL_DERIVED, "INDIVIDUAL", null,
        null, "UNKNOWN"
    );
    RaceDTO derivedNewerDTO = new RaceDTO(3L, "Derived Newer", "Subtitle 3", LocalDate.of(1984, Month.AUGUST, 15),
        null, List.of("SWIM"), List.of("SPRINT"), RaceType.INDIVIDUAL_DERIVED, "INDIVIDUAL", null,
        null, "UNKNOWN"
    );

    IndividualResult sourceResult = new IndividualResult();
    sourceResult.setId(10L);
    sourceResult.setRace(parent);
    sourceResult.setRank(1);

    IndividualResult derivedOlderResult = new IndividualResult();
    derivedOlderResult.setId(20L);
    derivedOlderResult.setRace(derivedOlder);
    derivedOlderResult.setRank(2);
    derivedOlderResult.setSourceResult(sourceResult);

    IndividualResult derivedNewerResult = new IndividualResult();
    derivedNewerResult.setId(30L);
    derivedNewerResult.setRace(derivedNewer);
    derivedNewerResult.setRank(3);
    derivedNewerResult.setSourceResult(sourceResult);

    Page<Long> idPage = new PageImpl<>(List.of(10L), PAGEABLE, 1L);

    when(individualResultRepository.findSourceResultIdsByAthleteId(ATHLETE_ID, PAGEABLE)).thenReturn(idPage);
    when(individualResultRepository.findResultsByIdInWithRaceGraph(List.of(10L))).thenReturn(List.of(sourceResult));
    when(individualResultRepository.findDerivedResultsBySourceResultIdsWithRaceGraph(ATHLETE_ID, List.of(10L)))
        .thenReturn(List.of(derivedNewerResult, derivedOlderResult));
    when(raceMapper.mapWithEvent(parent)).thenReturn(parentDTO);
    when(raceMapper.mapWithEvent(derivedOlder)).thenReturn(derivedOlderDTO);
    when(raceMapper.mapWithEvent(derivedNewer)).thenReturn(derivedNewerDTO);

    AthleteResultsPageDTO result = underTest.getAthleteResults(ATHLETE_ID, PAGEABLE);

    assertEquals(2, result.results().get(0).derivedResults().size());
    assertEquals(new AthleteResultDTO(derivedOlderDTO, 2, null, null, null, null, null, null, null, null, null, null, null, null, List.of()), result.results().get(0).derivedResults().get(0));
    assertEquals(new AthleteResultDTO(derivedNewerDTO, 3, null, null, null, null, null, null, null, null, null, null, null, null, List.of()), result.results().get(0).derivedResults().get(1));
  }

  @Test
  void getAthleteResultsShouldSkipSourceResultMissingFromRaceGraphFetch() {
    Race presentRace = createRace(2L, "Race 2", LocalDate.of(1984, Month.AUGUST, 15), RaceType.INDIVIDUAL_PARENT);

    RaceDTO presentRaceDTO = new RaceDTO(2L, "Race 2", "Subtitle 2", LocalDate.of(1984, Month.AUGUST, 15),
        null, List.of("SWIM"), List.of("SPRINT"), RaceType.INDIVIDUAL_PARENT, "INDIVIDUAL", null,
        null, "UNKNOWN"
    );

    IndividualResult presentResult = new IndividualResult();
    presentResult.setId(20L);
    presentResult.setRace(presentRace);
    presentResult.setRank(3);

    Page<Long> idPage = new PageImpl<>(List.of(10L, 20L), PAGEABLE, 2L);

    when(individualResultRepository.findSourceResultIdsByAthleteId(ATHLETE_ID, PAGEABLE)).thenReturn(idPage);
    when(individualResultRepository.findResultsByIdInWithRaceGraph(List.of(10L, 20L)))
        .thenReturn(List.of(presentResult));
    when(individualResultRepository.findDerivedResultsBySourceResultIdsWithRaceGraph(ATHLETE_ID, List.of(10L, 20L)))
        .thenReturn(List.of());
    when(raceMapper.mapWithEvent(presentRace)).thenReturn(presentRaceDTO);

    AthleteResultsPageDTO result = underTest.getAthleteResults(ATHLETE_ID, PAGEABLE);

    assertEquals(1, result.results().size());
    assertEquals(new AthleteResultDTO(presentRaceDTO, 3, null, null, null, null, null, null, null, null, null, null, null, null, List.of()), result.results().get(0));
    assertEquals(2L, result.pagination().totalItems());
  }

  @Test
  void getAthleteResultsShouldKeepNullRankForUnrankedSourceResult() {
    Race race = createRace(1L, "Race 1", LocalDate.of(1984, Month.AUGUST, 15), RaceType.INDIVIDUAL_PARENT);

    RaceDTO raceDTO = new RaceDTO(1L, "Race 1", "Subtitle 1", LocalDate.of(1984, Month.AUGUST, 15),
        null, List.of("SWIM"), List.of("SPRINT"), RaceType.INDIVIDUAL_PARENT, "INDIVIDUAL", null,
        null, "UNKNOWN"
    );

    IndividualResult unrankedResult = new IndividualResult();
    unrankedResult.setId(10L);
    unrankedResult.setRace(race);

    Page<Long> idPage = new PageImpl<>(List.of(10L), PAGEABLE, 1L);

    when(individualResultRepository.findSourceResultIdsByAthleteId(ATHLETE_ID, PAGEABLE)).thenReturn(idPage);
    when(individualResultRepository.findResultsByIdInWithRaceGraph(List.of(10L))).thenReturn(List.of(unrankedResult));
    when(individualResultRepository.findDerivedResultsBySourceResultIdsWithRaceGraph(ATHLETE_ID, List.of(10L)))
        .thenReturn(List.of());
    when(raceMapper.mapWithEvent(race)).thenReturn(raceDTO);

    AthleteResultsPageDTO result = underTest.getAthleteResults(ATHLETE_ID, PAGEABLE);

    assertEquals(1, result.results().size());
    assertNull(result.results().get(0).rank());
  }

  private static Race createRace(Long id, String title, LocalDate date, RaceType raceType) {
    return createRace(id, title, date, raceType, RACE_REFERENCE);
  }

  private static Race createRace(Long id, String title, LocalDate date, RaceType raceType, String raceReference) {
    Race race = new Race();
    race.setId(id);
    race.setRaceReference(raceReference);
    race.setRaceType(raceType);
    race.setTitle(title);
    race.setSubtitle("Subtitle");
    race.setGenderCategory(GenderCategory.OPEN);
    race.setDate(date);
    race.setRaceStatus(RaceStatus.COMPLETED);
    return race;
  }

  private static AthleteFirstLetterCount firstLetterCount(String letter, long total) {
    return new AthleteFirstLetterCount() {
      @Override
      public String getLetter() {
        return letter;
      }

      @Override
      public long getTotal() {
        return total;
      }
    };
  }
}
