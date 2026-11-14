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

package com.endurancetrio.app.competitor.web;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.endurancetrio.app.common.handler.EnduranceTrioExceptionHandlerWeb;
import com.endurancetrio.app.common.model.PageWindow;
import com.endurancetrio.app.common.service.MessageService;
import com.endurancetrio.app.config.AppProperties;
import com.endurancetrio.business.common.exception.EnduranceTrioError;
import com.endurancetrio.business.common.exception.EnduranceTrioException;
import com.endurancetrio.business.competitor.dto.AthleteDTO;
import com.endurancetrio.business.competitor.dto.AthleteFilterDTO;
import com.endurancetrio.business.competitor.dto.AthleteResultsPageDTO;
import com.endurancetrio.business.competitor.service.AthleteService;
import com.endurancetrio.business.insight.dto.InsightPageDTO;
import com.endurancetrio.business.insight.service.InsightService;
import com.endurancetrio.data.competitor.model.enumerator.AthleteGender;
import com.endurancetrio.data.competitor.model.enumerator.Country;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@ExtendWith(MockitoExtension.class)
class AthleteWebControllerTest {

  @Mock
  MessageService messageService;

  @Mock
  AthleteService athleteService;

  @Mock
  InsightService insightService;

  AppProperties appProperties;

  AthleteWebController athleteWebController;

  MockMvc mockMvc;

  EnduranceTrioExceptionHandlerWeb exceptionHandler;

  @BeforeEach
  void setUp() {
    appProperties = new AppProperties();
    appProperties.getOpenGraph().setDefaultImg("/img/endurancetrio-open-graph.png");
    appProperties.getOpenGraph().setDefaultImgWidth(1200);
    appProperties.getOpenGraph().setDefaultImgHeight(628);
    appProperties.getSocial().setFacebookPageId("1692877750958091");
    appProperties.getSocial().setTwitterSite("@EnduranceTrio");

    athleteWebController = new AthleteWebController(messageService, appProperties, athleteService,
        insightService);
    exceptionHandler = new EnduranceTrioExceptionHandlerWeb(messageService, appProperties);

    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("/WEB-INF/views/");
    viewResolver.setSuffix(".html");

    mockMvc = MockMvcBuilders.standaloneSetup(athleteWebController)
        .setControllerAdvice(exceptionHandler)
        .setViewResolvers(viewResolver)
        .build();
  }

  @Test
  void athletesPageWithEnglishLocale() throws Exception {
    when(messageService.getMessage(eq("page.athletes.metadata.title"), any(), any())).thenReturn(
        "Athletes - EnduranceTrio");
    when(messageService.getMessage(eq("page.athletes.metadata.description"), any(),
        any()
    )).thenReturn("Browse endurance sports athletes");
    when(athleteService.getAthletes(any(), any())).thenReturn(
        com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page0());

    mockMvc.perform(get("/en/athletes"))
        .andExpect(status().isOk())
        .andExpect(view().name("athletes"))
        .andExpect(model().attribute("language", "en"))
        .andExpect(model().attributeExists("metadata"));
  }

  @Test
  void athletesPageWithPortugueseLocale() throws Exception {
    when(messageService.getMessage(eq("page.athletes.metadata.title"), any(), any())).thenReturn(
        "Atletas - EnduranceTrio");
    when(messageService.getMessage(eq("page.athletes.metadata.description"), any(),
        any()
    )).thenReturn("Consulte os atletas de desportos de endurance");
    when(athleteService.getAthletes(any(), any())).thenReturn(
        com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page0());

    mockMvc.perform(get("/pt/athletes"))
        .andExpect(status().isOk())
        .andExpect(view().name("athletes"))
        .andExpect(model().attribute("language", "pt"))
        .andExpect(model().attributeExists("metadata"));
  }

  @Test
  void athletesPageMetadataHasCorrectTitle() throws Exception {
    when(messageService.getMessage(eq("page.athletes.metadata.title"), any(), any())).thenReturn(
        "Athletes - EnduranceTrio");
    when(messageService.getMessage(eq("page.athletes.metadata.description"), any(),
        any()
    )).thenReturn("Browse endurance sports athletes");
    when(athleteService.getAthletes(any(), any())).thenReturn(
        com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page0());

    mockMvc.perform(get("/en/athletes"))
        .andExpect(model().attribute("metadata", org.hamcrest.Matchers.hasProperty("title",
                org.hamcrest.Matchers.is("Athletes - EnduranceTrio")
            )
        ));
  }

  @Test
  void athletesPageFirstPage() throws Exception {
    when(messageService.getMessage(eq("page.athletes.metadata.title"), any(), any())).thenReturn(
        "Athletes - EnduranceTrio");
    when(messageService.getMessage(eq("page.athletes.metadata.description"), any(),
        any()
    )).thenReturn("Browse endurance sports athletes");
    when(athleteService.getAthletes(any(), any())).thenAnswer(invocation -> {
      Pageable pageable = invocation.getArgument(0);
      return switch (pageable.getPageNumber()) {
        case 0 -> com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page0();
        case 1 -> com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page1();
        case 2 -> com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page2();
        default -> com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.empty();
      };
    });

    mockMvc.perform(get("/en/athletes"))
        .andExpect(status().isOk())
        .andExpect(view().name("athletes"))
        .andExpect(model().attribute("athletes",
            com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page0().athletes()
        ))
        .andExpect(model().attribute("pagination",
            com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page0().pagination()
        )).andExpect(model().attribute("pageWindow", new PageWindow(List.of(0, 1, 2), false, false)
        ));

    verify(athleteService).getAthletes(eq(PageRequest.of(0, 30)), any());
  }

  @Test
  void athletesPageSecondPage() throws Exception {
    when(messageService.getMessage(eq("page.athletes.metadata.title"), any(), any())).thenReturn(
        "Athletes - EnduranceTrio");
    when(messageService.getMessage(eq("page.athletes.metadata.description"), any(),
        any()
    )).thenReturn("Browse endurance sports athletes");
    when(athleteService.getAthletes(any(), any())).thenAnswer(invocation -> {
      Pageable pageable = invocation.getArgument(0);
      return switch (pageable.getPageNumber()) {
        case 0 -> com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page0();
        case 1 -> com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page1();
        case 2 -> com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page2();
        default -> com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.empty();
      };
    });

    mockMvc.perform(get("/en/athletes").param("page", "1"))
        .andExpect(status().isOk())
        .andExpect(view().name("athletes"))
        .andExpect(model().attribute("athletes",
            com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page1().athletes()
        ))
        .andExpect(model().attribute("pagination",
            com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page1().pagination()
        ));

    verify(athleteService).getAthletes(eq(PageRequest.of(1, 30)), any());
  }

  @Test
  void athletesPageThirdPage() throws Exception {
    when(messageService.getMessage(eq("page.athletes.metadata.title"), any(), any())).thenReturn(
        "Athletes - EnduranceTrio");
    when(messageService.getMessage(eq("page.athletes.metadata.description"), any(),
        any()
    )).thenReturn("Browse endurance sports athletes");
    when(athleteService.getAthletes(any(), any())).thenAnswer(invocation -> {
      Pageable pageable = invocation.getArgument(0);
      return switch (pageable.getPageNumber()) {
        case 0 -> com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page0();
        case 1 -> com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page1();
        case 2 -> com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page2();
        default -> com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.empty();
      };
    });

    mockMvc.perform(get("/en/athletes").param("page", "2"))
        .andExpect(status().isOk())
        .andExpect(view().name("athletes"))
        .andExpect(model().attribute("athletes",
            com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page2().athletes()
        ))
        .andExpect(model().attribute("pagination",
            com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page2().pagination()
        ));

    verify(athleteService).getAthletes(eq(PageRequest.of(2, 30)), any());
  }

  @Test
  void athletesPageNegativePageShouldBeClamped() throws Exception {
    when(messageService.getMessage(eq("page.athletes.metadata.title"), any(), any())).thenReturn(
        "Athletes - EnduranceTrio");
    when(messageService.getMessage(eq("page.athletes.metadata.description"), any(),
        any()
    )).thenReturn("Browse endurance sports athletes");
    when(athleteService.getAthletes(any(), any())).thenAnswer(invocation -> {
      Pageable pageable = invocation.getArgument(0);
      return switch (pageable.getPageNumber()) {
        case 0 -> com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page0();
        case 1 -> com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page1();
        case 2 -> com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page2();
        default -> com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.empty();
      };
    });

    mockMvc.perform(get("/en/athletes").param("page", "-1"))
        .andExpect(status().isOk())
        .andExpect(view().name("athletes"))
        .andExpect(model().attribute("athletes",
            com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page0().athletes()
        ))
        .andExpect(model().attribute("pagination",
            com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page0().pagination()
        ));

    verify(athleteService).getAthletes(eq(PageRequest.of(0, 30)), any());
  }

  @Test
  void athletesPageWithEmptyList() throws Exception {
    when(messageService.getMessage(eq("page.athletes.metadata.title"), any(), any())).thenReturn(
        "Athletes - EnduranceTrio");
    when(messageService.getMessage(eq("page.athletes.metadata.description"), any(),
        any()
    )).thenReturn("Browse endurance sports athletes");
    when(athleteService.getAthletes(any(), any())).thenReturn(
        com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.empty());

    mockMvc.perform(get("/en/athletes"))
        .andExpect(status().isOk())
        .andExpect(view().name("athletes"))
        .andExpect(model().attribute("athletes",
            com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.empty().athletes()
        ))
        .andExpect(model().attribute("pagination",
            com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.empty().pagination()
        )).andExpect(model().attribute("pageWindow", new PageWindow(List.of(), false, false)));

    verify(athleteService).getAthletes(eq(PageRequest.of(0, 30)), any());
  }

  @Test
  void athletesPageNormalizesFilters() throws Exception {
    when(messageService.getMessage(eq("page.athletes.metadata.title"), any(), any())).thenReturn(
        "Athletes - EnduranceTrio");
    when(messageService.getMessage(eq("page.athletes.metadata.description"), any(), any())).thenReturn(
        "Browse endurance sports athletes");
    when(athleteService.getAthletes(any(), any())).thenReturn(
        com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page0());

    mockMvc.perform(
            get("/en/athletes").param("letterRange", " a_f ").param("gender", " female ").param("q", "  João  "))
        .andExpect(status().isOk())
        .andExpect(model().attribute("filter", new AthleteFilterDTO("A_F", AthleteGender.FEMALE, "João")))
        .andExpect(model().attribute("isFiltered", true))
        .andExpect(model().attribute("metadata", hasProperty("canonicalUrl", equalTo("http://localhost/en/athletes"))))
        .andExpect(model().attribute("metadata", hasProperty("hreflangUrlEn", equalTo("http://localhost/en/athletes"))))
        .andExpect(
            model().attribute("metadata", hasProperty("hreflangUrlPt", equalTo("http://localhost/pt/athletes"))));
  }

  @Test
  void athletesPageBuildsNavigationUrls() throws Exception {
    when(messageService.getMessage(eq("page.athletes.metadata.title"), any(), any())).thenReturn(
        "Athletes - EnduranceTrio");
    when(messageService.getMessage(eq("page.athletes.metadata.description"), any(), any())).thenReturn(
        "Browse endurance sports athletes");
    when(athleteService.getAthletes(any(), any())).thenReturn(
        com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page0());

    mockMvc.perform(get("/en/athletes"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("athletesUrl", "/en/athletes"))
        .andExpect(model().attribute("paginationUrls", hasEntry(0, "/en/athletes?page=0")))
        .andExpect(model().attribute("paginationUrls", hasEntry(2, "/en/athletes?page=2")))
        .andExpect(model().attribute("paginationPreviousUrl", nullValue()))
        .andExpect(model().attribute("paginationNextUrl", "/en/athletes?page=1"))
        .andExpect(model().attribute("letterRangeUrls", hasEntry("ALL", "/en/athletes")))
        .andExpect(model().attribute("letterRangeUrls", hasEntry("A_F", "/en/athletes?letterRange=A_F")))
        .andExpect(model().attribute("genderUrls", hasEntry("MALE", "/en/athletes?gender=MALE")))
        .andExpect(model().attribute("metadata", hasProperty("canonicalUrl", equalTo("http://localhost/en/athletes"))))
        .andExpect(
            model().attribute("metadata", hasProperty("hreflangUrlPt", equalTo("http://localhost/pt/athletes"))));
  }

  @Test
  void athletesPageKeepsSearchTermVerbatim() throws Exception {
    when(messageService.getMessage(eq("page.athletes.metadata.title"), any(), any())).thenReturn(
        "Athletes - EnduranceTrio");
    when(messageService.getMessage(eq("page.athletes.metadata.description"), any(), any())).thenReturn(
        "Browse endurance sports athletes");
    when(athleteService.getAthletes(any(), any())).thenReturn(
        com.endurancetrio.app.competitor.fixtures.AthletesPageDTOFixtures.page0());

    String searchTerm = "' OR 1=1 -- <script>alert('xss')</script> %_";

    mockMvc.perform(get("/en/athletes").param("q", searchTerm))
        .andExpect(status().isOk())
        .andExpect(model().attribute("filter", new AthleteFilterDTO(null, null, searchTerm)))
        .andExpect(model().attribute("isFiltered", true));

    verify(athleteService).getAthletes(PageRequest.of(0, 30), new AthleteFilterDTO(null, null, searchTerm));
  }

  @Test
  void athleteProfileWithEnglishLocale() throws Exception {
    when(messageService.getMessage(eq("page.athlete.profile.metadata.title"), any(),
        any()
    )).thenReturn("Athlete Profile - EnduranceTrio");
    when(messageService.getMessage(eq("page.athlete.profile.metadata.description"), any(),
        any()
    )).thenReturn("View athlete profile and race history");
    when(athleteService.getAthleteById(1L)).thenReturn(
        new AthleteDTO(1L, "Paulo José Paula Carvalho", null, "Paulo Paula Carvalho",
            AthleteGender.MALE, Country.POR, 1961
        ));
    when(athleteService.getAthleteResults(eq(1L), any())).thenReturn(
        new AthleteResultsPageDTO(List.of(),
            new com.endurancetrio.business.common.dto.PaginationDTO(0, 0, 0, false, false)
        ));
    when(insightService.getArticlesByAthleteId(eq(1L), any(), any())).thenReturn(
        new InsightPageDTO(List.of(),
            new com.endurancetrio.business.common.dto.PaginationDTO(0, 0, 0, false, false)
        ));

    mockMvc.perform(get("/en/athletes/1"))
        .andExpect(status().isOk())
        .andExpect(view().name("athlete-profile"))
        .andExpect(model().attribute("language", "en"))
        .andExpect(model().attributeExists("metadata"))
        .andExpect(model().attributeExists("athlete"))
        .andExpect(model().attributeExists("results"))
        .andExpect(model().attributeExists("pagination"))
        .andExpect(model().attributeExists("articles"));

    verify(insightService).getArticlesByAthleteId(eq(1L), eq(Pageable.unpaged()), any());
  }

  @Test
  void athleteProfileWithPortugueseLocale() throws Exception {
    when(messageService.getMessage(eq("page.athlete.profile.metadata.title"), any(),
        any()
    )).thenReturn("Perfil do Atleta - EnduranceTrio");
    when(messageService.getMessage(eq("page.athlete.profile.metadata.description"), any(),
        any()
    )).thenReturn("Ver perfil e histórico de provas do atleta");
    when(athleteService.getAthleteById(4L)).thenReturn(
        new AthleteDTO(4L, "Paulo Cavaleiro", null, "Paulo Cavaleiro", AthleteGender.MALE,
            Country.POR, null
        ));
    when(athleteService.getAthleteResults(eq(4L), any())).thenReturn(
        new AthleteResultsPageDTO(List.of(),
            new com.endurancetrio.business.common.dto.PaginationDTO(0, 0, 0, false, false)
        ));
    when(insightService.getArticlesByAthleteId(eq(4L), any(), any())).thenReturn(
        new InsightPageDTO(List.of(),
            new com.endurancetrio.business.common.dto.PaginationDTO(0, 0, 0, false, false)
        ));

    mockMvc.perform(get("/pt/athletes/4"))
        .andExpect(status().isOk())
        .andExpect(view().name("athlete-profile"))
        .andExpect(model().attribute("language", "pt"))
        .andExpect(model().attributeExists("metadata"))
        .andExpect(model().attributeExists("athlete"))
        .andExpect(model().attributeExists("results"))
        .andExpect(model().attributeExists("pagination"))
        .andExpect(model().attributeExists("articles"));

    verify(insightService).getArticlesByAthleteId(eq(4L), eq(Pageable.unpaged()), any());
  }

  @Test
  void athleteProfileMetadataHasCorrectTitle() throws Exception {
    when(messageService.getMessage(eq("page.athlete.profile.metadata.title"), any(),
        any()
    )).thenReturn("Athlete Profile - EnduranceTrio");
    when(messageService.getMessage(eq("page.athlete.profile.metadata.description"), any(),
        any()
    )).thenReturn("View athlete profile and race history");
    when(athleteService.getAthleteById(1L)).thenReturn(
        new AthleteDTO(1L, "Paulo José Paula Carvalho", null, "Paulo Paula Carvalho",
            AthleteGender.MALE, Country.POR, 1961
        ));
    when(athleteService.getAthleteResults(eq(1L), any())).thenReturn(
        new AthleteResultsPageDTO(List.of(),
            new com.endurancetrio.business.common.dto.PaginationDTO(0, 0, 0, false, false)
        ));
    when(insightService.getArticlesByAthleteId(eq(1L), any(), any())).thenReturn(
        new InsightPageDTO(List.of(),
            new com.endurancetrio.business.common.dto.PaginationDTO(0, 0, 0, false, false)
        ));

    mockMvc.perform(get("/en/athletes/1"))
        .andExpect(model().attribute("metadata", org.hamcrest.Matchers.hasProperty("title",
                org.hamcrest.Matchers.is("Athlete Profile - EnduranceTrio")
            )
        ));

    verify(insightService).getArticlesByAthleteId(eq(1L), eq(Pageable.unpaged()), any());
  }

  @Test
  void athleteProfileWhenAthleteNotFound() throws Exception {
    when(messageService.getMessage(eq("page.error.404.metadata.title"), any(), any())).thenReturn(
        "Page Not Found");
    when(messageService.getMessage(eq("page.error.404.metadata.description"), any(),
        any()
    )).thenReturn("The requested page was not found");
    when(athleteService.getAthleteById(999L)).thenThrow(new EnduranceTrioException(
        new com.endurancetrio.business.common.dto.ErrorDTO(EnduranceTrioError.NOT_FOUND,
            "No athlete found with ID 999"
        )));

    mockMvc.perform(get("/en/athletes/999")).andExpect(status().isNotFound());
  }
}
