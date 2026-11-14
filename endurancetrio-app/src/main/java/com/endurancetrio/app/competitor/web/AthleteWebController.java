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

import static com.endurancetrio.app.common.constants.AppConstants.LANGUAGE;
import static com.endurancetrio.app.common.constants.AppConstants.LOCALE_PORTUGUESE;
import static com.endurancetrio.app.common.constants.AppConstants.METADATA;
import static com.endurancetrio.app.common.constants.AppConstants.PAGINATION;

import com.endurancetrio.app.common.annotation.EnduranceTrioWebController;
import com.endurancetrio.app.common.model.PageMetadata;
import com.endurancetrio.app.common.service.MessageService;
import com.endurancetrio.app.common.utils.PageMetadataUtils;
import com.endurancetrio.app.common.utils.PaginationUtils;
import com.endurancetrio.app.config.AppProperties;
import com.endurancetrio.business.competitor.dto.AthleteDTO;
import com.endurancetrio.business.competitor.dto.AthleteFilterDTO;
import com.endurancetrio.business.competitor.dto.AthleteResultsPageDTO;
import com.endurancetrio.business.competitor.dto.AthletesPageDTO;
import com.endurancetrio.business.competitor.enumerator.AthleteLetterRange;
import com.endurancetrio.business.competitor.service.AthleteService;
import com.endurancetrio.business.insight.dto.InsightPageDTO;
import com.endurancetrio.business.insight.service.InsightService;
import com.endurancetrio.data.competitor.model.enumerator.AthleteGender;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * The {@link AthleteWebController} handles Thymeleaf views for the athletes section.
 */
@EnduranceTrioWebController
public class AthleteWebController {

  private static final String ATTRIBUTE_ARTICLES = "articles";
  private static final String ATTRIBUTE_ATHLETE = "athlete";
  private static final String ATTRIBUTE_ATHLETES = "athletes";
  private static final String ATTRIBUTE_ATHLETES_URL = "athletesUrl";
  private static final String ATTRIBUTE_FILTER = "filter";
  private static final String ATTRIBUTE_FILTERED = "isFiltered";
  private static final String ATTRIBUTE_GENDER_URLS = "genderUrls";
  private static final String ATTRIBUTE_LETTER_RANGE_URLS = "letterRangeUrls";
  private static final String ATTRIBUTE_LETTER_RANGES = "letterRanges";
  private static final String ATTRIBUTE_PAGE_WINDOW = "pageWindow";
  private static final String ATTRIBUTE_PAGINATION_NEXT_URL = "paginationNextUrl";
  private static final String ATTRIBUTE_PAGINATION_PREVIOUS_URL = "paginationPreviousUrl";
  private static final String ATTRIBUTE_PAGINATION_URLS = "paginationUrls";
  private static final String ATTRIBUTE_RESULTS = "results";
  private static final String VIEW_ATHLETE_PROFILE = "athlete-profile";
  private static final String VIEW_ATHLETES = "athletes";

  private static final int MAX_SEARCH_TERM_LENGTH = 100;
  private static final int PAGE_SIZE = 30;
  private static final int PROFILE_PAGE_SIZE = 10;

  private final MessageService messageService;
  private final AppProperties appProperties;
  private final AthleteService athleteService;
  private final InsightService insightService;

  @Autowired
  public AthleteWebController(
      MessageService messageService, AppProperties appProperties,
      AthleteService athleteService, InsightService insightService
  ) {
    this.messageService = messageService;
    this.appProperties = appProperties;
    this.athleteService = athleteService;
    this.insightService = insightService;
  }

  /**
   * Returns the athletes listing page with paginated athletes filtered by letter range, gender,
   * and free-text search term, ordered by known name. Invalid letter-range and gender values are
   * ignored. The search term is trimmed and capped in length but otherwise applied verbatim as an
   * accent-insensitive partial match against known and long names. Also builds the pagination and
   * filter-toggle URLs used by the view.
   *
   * @param language    the language path variable ({@code en} or {@code pt})
   * @param page        the page number from the query string (default {@code 0}, clamped to
   *                    non-negative)
   * @param letterRange the optional letter-range identifier ({@code a_f}, case-insensitive)
   * @param gender      the optional gender filter ({@code MALE} or {@code FEMALE},
   *                    case-insensitive)
   * @param searchTerm  the optional free-text search term (query parameter {@code q})
   * @param request     the current HTTP request for building page metadata
   * @param model       the model to populate with view attributes
   * @return the athletes view name
   */
  @GetMapping("/{language:en|pt}/athletes")
  public String getAthletes(
      @PathVariable String language, @RequestParam(defaultValue = "0") int page,
      @RequestParam(required = false) String letterRange, @RequestParam(required = false) String gender,
      @RequestParam(name = "q", required = false) String searchTerm, HttpServletRequest request, Model model
  ) {
    Locale locale = "pt".equalsIgnoreCase(language) ? LOCALE_PORTUGUESE : Locale.ENGLISH;

    PageMetadata metadata = PageMetadataUtils.create(VIEW_ATHLETES,
        messageService.getMessage("page.athletes.metadata.title", null, locale),
        messageService.getMessage("page.athletes.metadata.description", null, locale), request,
        appProperties
    );

    int clampedPage = Math.max(0, page);
    Pageable pageable = PageRequest.of(clampedPage, PAGE_SIZE);
    AthleteFilterDTO filter = normalizeFilter(letterRange, gender, searchTerm);
    AthletesPageDTO athletesPage = athleteService.getAthletes(pageable, filter);
    int totalPages = athletesPage.pagination().totalPages();
    boolean isFiltered = filter.letterRange() != null || filter.gender() != null || filter.searchTerm() != null;

    if (isFiltered || clampedPage > 0) {
      String baseUrl = PageMetadataUtils.getBaseUrl(request);
      metadata.setCanonicalUrl(buildAbsoluteAthletesUrl(baseUrl, locale.getLanguage()));
      metadata.setHreflangUrlEn(buildAbsoluteAthletesUrl(baseUrl, "en"));
      metadata.setHreflangUrlPt(buildAbsoluteAthletesUrl(baseUrl, "pt"));
    }

    String athletesPath = UriComponentsBuilder.fromPath("/")
        .pathSegment(locale.getLanguage(), VIEW_ATHLETES)
        .build()
        .toUriString();

    Map<Integer, String> paginationUrls = new LinkedHashMap<>();
    for (Integer pageIndex : PaginationUtils.pageWindow(clampedPage, totalPages).pages()) {
      paginationUrls.put(pageIndex, buildAthletesUrl(athletesPath, pageIndex, filter));
    }

    Map<String, String> letterRangeUrls = new LinkedHashMap<>();
    letterRangeUrls.put("ALL",
        buildAthletesUrl(athletesPath, null, new AthleteFilterDTO(null, filter.gender(), filter.searchTerm()))
    );
    for (AthleteLetterRange range : AthleteLetterRange.values()) {
      String rangeId =
          filter.letterRange() != null && filter.letterRange().equals(range.getId()) ? null : range.getId();
      letterRangeUrls.put(range.getId(),
          buildAthletesUrl(athletesPath, null, new AthleteFilterDTO(rangeId, filter.gender(), filter.searchTerm()))
      );
    }

    Map<String, String> genderUrls = new LinkedHashMap<>();
    genderUrls.put("ALL",
        buildAthletesUrl(athletesPath, null, new AthleteFilterDTO(filter.letterRange(), null, filter.searchTerm()))
    );
    for (AthleteGender athleteGender : AthleteGender.values()) {
      AthleteGender selectedGender = filter.gender() == athleteGender ? null : athleteGender;
      genderUrls.put(athleteGender.getCode(), buildAthletesUrl(athletesPath, null,
              new AthleteFilterDTO(filter.letterRange(), selectedGender, filter.searchTerm())
          )
      );
    }

    model.addAttribute(LANGUAGE, locale.getLanguage());
    model.addAttribute(METADATA, metadata);
    model.addAttribute(PAGINATION, athletesPage.pagination());
    model.addAttribute(ATTRIBUTE_ATHLETES, athletesPage.athletes());
    model.addAttribute(ATTRIBUTE_PAGE_WINDOW, PaginationUtils.pageWindow(clampedPage, totalPages));
    model.addAttribute(ATTRIBUTE_FILTER, filter);
    model.addAttribute(ATTRIBUTE_LETTER_RANGES, athletesPage.letterRanges());
    model.addAttribute(ATTRIBUTE_FILTERED, isFiltered);
    model.addAttribute(ATTRIBUTE_PAGINATION_URLS, paginationUrls);
    model.addAttribute(ATTRIBUTE_PAGINATION_PREVIOUS_URL,
        athletesPage.pagination().hasPrevious() ? buildAthletesUrl(athletesPath, clampedPage - 1, filter) : null
    );
    model.addAttribute(ATTRIBUTE_PAGINATION_NEXT_URL,
        athletesPage.pagination().hasNext() ? buildAthletesUrl(athletesPath, clampedPage + 1, filter) : null
    );
    model.addAttribute(ATTRIBUTE_ATHLETES_URL, athletesPath);
    model.addAttribute(ATTRIBUTE_LETTER_RANGE_URLS, letterRangeUrls);
    model.addAttribute(ATTRIBUTE_GENDER_URLS, genderUrls);

    return VIEW_ATHLETES;
  }

  /**
   * Returns the athlete profile page for a specific athlete, including their races and all
   * authored insight articles.
   *
   * @param language the language path variable ({@code en} or {@code pt})
   * @param id       the ID of the athlete
   * @param page     the page number from the query string (default {@code 0}, clamped to
   *                 non-negative)
   * @param request  the current HTTP request for building page metadata
   * @param model    the model to populate with view attributes
   * @return the athlete profile view name
   */
  @GetMapping("/{language:en|pt}/athletes/{id}")
  public String getAthleteById(
      @PathVariable String language,
      @PathVariable Long id,
      @RequestParam(defaultValue = "0") int page,
      HttpServletRequest request, Model model
  ) {
    Locale locale = "pt".equalsIgnoreCase(language) ? LOCALE_PORTUGUESE : Locale.ENGLISH;

    AthleteDTO athlete = athleteService.getAthleteById(id);

    PageMetadata metadata = PageMetadataUtils.create(VIEW_ATHLETE_PROFILE,
        messageService.getMessage("page.athlete.profile.metadata.title",
            new Object[]{athlete.knownName()}, locale
        ),
        messageService.getMessage("page.athlete.profile.metadata.description",
            new Object[]{athlete.knownName()}, locale
        ), request, appProperties
    );

    int clampedPage = Math.max(0, page);
    Pageable pageable = PageRequest.of(clampedPage, PROFILE_PAGE_SIZE);
    AthleteResultsPageDTO athleteResults = athleteService.getAthleteResults(id, pageable);

    InsightPageDTO athleteArticles = insightService.getArticlesByAthleteId(id, Pageable.unpaged(), locale);

    model.addAttribute(LANGUAGE, locale.getLanguage());
    model.addAttribute(METADATA, metadata);
    model.addAttribute(PAGINATION, athleteResults.pagination());
    model.addAttribute(ATTRIBUTE_ARTICLES, athleteArticles.articles());
    model.addAttribute(ATTRIBUTE_ATHLETE, athlete);
    model.addAttribute(ATTRIBUTE_RESULTS, athleteResults.results());

    return VIEW_ATHLETE_PROFILE;
  }

  private static String buildAbsoluteAthletesUrl(String baseUrl, String language) {
    return UriComponentsBuilder.fromUriString(baseUrl).pathSegment(language, VIEW_ATHLETES).build().toUriString();
  }

  private static String buildAthletesUrl(String path, Integer page, AthleteFilterDTO filter) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromPath(path);
    if (page != null) {
      builder.queryParam("page", page);
    }

    if (filter.letterRange() != null) {
      builder.queryParam("letterRange", filter.letterRange());
    }

    if (filter.gender() != null) {
      builder.queryParam("gender", filter.gender().getCode());
    }

    if (filter.searchTerm() != null) {
      builder.queryParam("q", filter.searchTerm());
    }

    return builder.build().encode().toUriString();
  }

  /**
   * Normalizes the raw request parameters into an {@link AthleteFilterDTO}. The letter range and
   * gender are validated against their enumerations, and the search term is trimmed and capped in
   * length. The search term content is otherwise kept verbatim: it is treated as plain data by the
   * persistence layer (Criteria API binding with LIKE escaping) and escaped by the view layer on
   * output.
   *
   * @param letterRange the raw letter-range request parameter
   * @param gender      the raw gender request parameter
   * @param searchTerm  the raw search-term request parameter
   * @return the normalized filter
   */
  private static AthleteFilterDTO normalizeFilter(String letterRange, String gender, String searchTerm) {
    AthleteLetterRange normalizedRange = AthleteLetterRange.fromId(letterRange);

    AthleteGender normalizedGender;
    if (gender != null) {
      try {
        normalizedGender = AthleteGender.valueOf(gender.trim().toUpperCase(Locale.ROOT));
      } catch (IllegalArgumentException exception) {
        normalizedGender = null;
      }
    } else {
      normalizedGender = null;
    }

    return new AthleteFilterDTO(normalizedRange == null ? null : normalizedRange.getId(), normalizedGender,
        normalizeSearchTerm(searchTerm)
    );
  }

  private static String normalizeSearchTerm(String searchTerm) {
    if (searchTerm == null) {
      return null;
    }

    String normalized = searchTerm.trim();
    return normalized.length() > MAX_SEARCH_TERM_LENGTH ? normalized.substring(0, MAX_SEARCH_TERM_LENGTH) : normalized;
  }
}
