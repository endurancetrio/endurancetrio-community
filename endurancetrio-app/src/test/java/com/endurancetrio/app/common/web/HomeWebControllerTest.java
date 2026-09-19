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

package com.endurancetrio.app.common.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.endurancetrio.app.common.service.MessageService;
import com.endurancetrio.app.config.AppProperties;
import com.endurancetrio.business.common.dto.PaginationDTO;
import com.endurancetrio.business.event.dto.EventDTO;
import com.endurancetrio.business.event.dto.EventsPageDTO;
import com.endurancetrio.business.event.dto.RaceDTO;
import com.endurancetrio.business.event.service.EventService;
import com.endurancetrio.business.event.service.RaceService;
import com.endurancetrio.business.insight.dto.ArticleDTO;
import com.endurancetrio.business.insight.service.InsightService;
import com.endurancetrio.data.event.model.enumerator.RaceType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@ExtendWith(MockitoExtension.class)
class HomeWebControllerTest {

  private static final LocalDate EVENT_DATE = LocalDate.of(1984, Month.AUGUST, 15);

  private static final ArticleDTO ARTICLE_DTO = new ArticleDTO(3L,
      "the-origins-of-portuguese-triathlon", "The Origins of Portuguese Triathlon",
      "A retrospective", "<p>How it all began.</p>", null, "Ricardo do Canto",
      LocalDateTime.of(2026, Month.SEPTEMBER, 1, 10, 0), "/img/article.png", 1200, 628, null,
      null, "en"
  );

  private static final EventDTO EVENT_DTO = new EventDTO(1L, "Triatlo de Peniche", EVENT_DATE,
      EVENT_DATE, "Peniche", "Peniche", "Leiria", List.of("TRIATHLON")
  );

  private static final EventDTO EVENT_DTO_WITH_RACE = new EventDTO(1L, "Triatlo de Peniche",
      EVENT_DATE, EVENT_DATE, "Peniche", "Peniche", "Leiria", List.of("TRIATHLON")
  );

  private static final RaceDTO RACE_DTO = new RaceDTO(1L, "Triatlo de Peniche", "Geral", EVENT_DATE,
      null, List.of(), List.of(), RaceType.INDIVIDUAL_PARENT, "INDIVIDUAL", null,
      EVENT_DTO_WITH_RACE, "COMPLETE"
  );

  private static final Page<RaceDTO> RACE_PAGE = new PageImpl<>(List.of(RACE_DTO));

  private static final EventsPageDTO EVENTS_PAGE = new EventsPageDTO(List.of(EVENT_DTO),
      new PaginationDTO(0, 6, 1, false, false)
  );

  private static final EventsPageDTO EVENTS_PAGE_EMPTY = new EventsPageDTO(List.of(),
      new PaginationDTO(0, 6, 0, false, false)
  );

  private static final Page<RaceDTO> RACE_PAGE_EMPTY = new PageImpl<>(List.of());

  @Mock
  MessageService messageService;

  @Mock
  EventService eventService;

  @Mock
  InsightService insightService;

  @Mock
  RaceService raceService;

  AppProperties appProperties;

  HomeWebController homeWebController;

  MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    appProperties = new AppProperties();
    appProperties.setFeaturedArticleIds(List.of(1L, 2L));
    appProperties.setFeaturedEventArticleId(3L);
    appProperties.setFeaturedEventIds(List.of(300001L, 300002L, 300003L, 300004L));
    appProperties.getOpenGraph().setDefaultImg("/img/endurancetrio-open-graph.png");
    appProperties.getOpenGraph().setDefaultImgWidth(1200);
    appProperties.getOpenGraph().setDefaultImgHeight(628);
    appProperties.getSocial().setFacebookPageId("1692877750958091");
    appProperties.getSocial().setTwitterSite("@EnduranceTrio");

    homeWebController = new HomeWebController(messageService, appProperties, eventService,
        insightService, raceService
    );

    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("/WEB-INF/views/");
    viewResolver.setSuffix(".html");

    mockMvc = MockMvcBuilders.standaloneSetup(homeWebController)
        .setViewResolvers(viewResolver)
        .build();
  }

  @Test
  void homePageShouldReturnHomeView() throws Exception {
    when(messageService.getMessage(eq("page.home.metadata.title"), any(), any())).thenReturn(
        "Home - EnduranceTrio");
    when(messageService.getMessage(eq("page.home.metadata.description"), any(), any())).thenReturn(
        "The central hub");
    when(insightService.getArticlesByIds(any(), any())).thenReturn(List.of(ARTICLE_DTO));
    when(eventService.getEventsByIds(any())).thenReturn(List.of());
    when(raceService.getNonDerivedRacesWithMostRecentAddedResults(
        any(PageRequest.class))).thenReturn(RACE_PAGE);
    when(eventService.getMostRecentAddedEvents(any(PageRequest.class))).thenReturn(EVENTS_PAGE);

    mockMvc.perform(get("/en/"))
        .andExpect(status().isOk())
        .andExpect(view().name("home"))
        .andExpect(model().attributeExists("metadata"))
        .andExpect(model().attribute("language", "en"))
        .andExpect(model().attributeExists("featuredEventArticle"))
        .andExpect(model().attributeExists("featuredEvents"))
        .andExpect(model().attributeExists("latestRaces"))
        .andExpect(model().attributeExists("latestEvents"));
  }

  @Test
  void homePagePortugueseShouldReturnHomeView() throws Exception {
    when(messageService.getMessage(eq("page.home.metadata.title"), any(), any())).thenReturn(
        "Início - EnduranceTrio");
    when(messageService.getMessage(eq("page.home.metadata.description"), any(), any())).thenReturn(
        "A plataforma central");
    when(insightService.getArticlesByIds(any(), any())).thenReturn(List.of(ARTICLE_DTO));
    when(eventService.getEventsByIds(any())).thenReturn(List.of());
    when(raceService.getNonDerivedRacesWithMostRecentAddedResults(
        any(PageRequest.class))).thenReturn(RACE_PAGE);
    when(eventService.getMostRecentAddedEvents(any(PageRequest.class))).thenReturn(EVENTS_PAGE);

    mockMvc.perform(get("/pt/"))
        .andExpect(status().isOk())
        .andExpect(view().name("home"))
        .andExpect(model().attribute("language", "pt"));
  }

  @Test
  void homePageWithEmptyDataShouldReturnHomeView() throws Exception {
    when(messageService.getMessage(eq("page.home.metadata.title"), any(), any())).thenReturn(
        "Home - EnduranceTrio");
    when(messageService.getMessage(eq("page.home.metadata.description"), any(), any())).thenReturn(
        "The central hub");
    when(insightService.getArticlesByIds(any(), any())).thenReturn(List.of(ARTICLE_DTO));
    when(eventService.getEventsByIds(any())).thenReturn(List.of());
    when(raceService.getNonDerivedRacesWithMostRecentAddedResults(
        any(PageRequest.class))).thenReturn(RACE_PAGE_EMPTY);
    when(eventService.getMostRecentAddedEvents(any(PageRequest.class))).thenReturn(
        EVENTS_PAGE_EMPTY);

    mockMvc.perform(get("/en/"))
        .andExpect(status().isOk())
        .andExpect(view().name("home"))
        .andExpect(model().attributeExists("featuredEventArticle"))
        .andExpect(model().attributeExists("featuredEvents"))
        .andExpect(model().attributeExists("latestRaces"))
        .andExpect(model().attributeExists("latestEvents"));
  }
}
