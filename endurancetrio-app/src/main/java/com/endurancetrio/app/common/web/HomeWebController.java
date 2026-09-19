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

import static com.endurancetrio.app.common.constants.AppConstants.LANGUAGE;
import static com.endurancetrio.app.common.constants.AppConstants.LOCALE_PORTUGUESE;
import static com.endurancetrio.app.common.constants.AppConstants.METADATA;

import com.endurancetrio.app.common.annotation.EnduranceTrioWebController;
import com.endurancetrio.app.common.model.PageMetadata;
import com.endurancetrio.app.common.service.MessageService;
import com.endurancetrio.app.common.utils.PageMetadataUtils;
import com.endurancetrio.app.config.AppProperties;
import com.endurancetrio.business.event.dto.EventDTO;
import com.endurancetrio.business.event.dto.EventsPageDTO;
import com.endurancetrio.business.event.dto.RaceDTO;
import com.endurancetrio.business.event.service.EventService;
import com.endurancetrio.business.event.service.RaceService;
import com.endurancetrio.business.insight.dto.ArticleDTO;
import com.endurancetrio.business.insight.service.InsightService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@EnduranceTrioWebController
public class HomeWebController {

  private static final String VIEW_HOME = "home";

  private static final String ATTRIBUTE_EVENTS = "latestEvents";
  private static final String ATTRIBUTE_FEATURED_EVENTS = "featuredEvents";
  private static final String ATTRIBUTE_FEATURED_EVENT_ARTICLE = "featuredEventArticle";
  private static final String ATTRIBUTE_INSIGHTS = "featuredArticles";
  private static final String ATTRIBUTE_RACES = "latestRaces";

  private static final int RECENT_EVENTS_SIZE = 6;
  private static final int RECENT_RACES_SIZE = 3;

  private final MessageService messageService;
  private final AppProperties appProperties;
  private final EventService eventService;
  private final InsightService insightService;
  private final RaceService raceService;

  @Autowired
  public HomeWebController(
      MessageService messageService, AppProperties appProperties, EventService eventService,
      InsightService insightService, RaceService raceService
  ) {
    this.messageService = messageService;
    this.appProperties = appProperties;
    this.eventService = eventService;
    this.insightService = insightService;
    this.raceService = raceService;
  }

  @GetMapping("/{language:en|pt}/")
  public String homePage(@PathVariable String language, HttpServletRequest request, Model model) {
    Locale locale = "pt".equalsIgnoreCase(language) ? LOCALE_PORTUGUESE : Locale.ENGLISH;

    PageMetadata metadata = PageMetadataUtils.create(VIEW_HOME,
        messageService.getMessage("page.home.metadata.title", null, locale),
        messageService.getMessage("page.home.metadata.description", null, locale), request,
        appProperties
    );

    List<ArticleDTO> featuredArticles = insightService.getArticlesByIds(
        appProperties.getFeaturedArticleIds(), locale
    );

    List<ArticleDTO> featuredEventArticles = insightService.getArticlesByIds(
        List.of(appProperties.getFeaturedEventArticleId()), locale
    );
    ArticleDTO featuredEventArticle = featuredEventArticles.isEmpty()
        ? null
        : featuredEventArticles.getFirst();

    List<EventDTO> featuredEvents = eventService.getEventsByIds(
        appProperties.getFeaturedEventIds()
    );

    List<RaceDTO> latestRaces = raceService
        .getNonDerivedRacesWithMostRecentAddedResults(PageRequest.of(0, RECENT_RACES_SIZE))
        .getContent();

    EventsPageDTO latestEvents = eventService.getMostRecentAddedEvents(
        PageRequest.of(0, RECENT_EVENTS_SIZE)
    );

    model.addAttribute(LANGUAGE, locale.getLanguage());
    model.addAttribute(METADATA, metadata);
    model.addAttribute(ATTRIBUTE_INSIGHTS, featuredArticles);
    model.addAttribute(ATTRIBUTE_FEATURED_EVENT_ARTICLE, featuredEventArticle);
    model.addAttribute(ATTRIBUTE_FEATURED_EVENTS, featuredEvents);
    model.addAttribute(ATTRIBUTE_RACES, latestRaces);
    model.addAttribute(ATTRIBUTE_EVENTS, latestEvents.events());

    return VIEW_HOME;
  }
}
