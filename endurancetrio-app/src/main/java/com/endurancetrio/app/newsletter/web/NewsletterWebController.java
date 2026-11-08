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

package com.endurancetrio.app.newsletter.web;

import static com.endurancetrio.app.common.constants.AppConstants.LANGUAGE;
import static com.endurancetrio.app.common.constants.AppConstants.LOCALE_PORTUGUESE;
import static com.endurancetrio.app.common.constants.AppConstants.METADATA;

import com.endurancetrio.app.common.annotation.EnduranceTrioWebController;
import com.endurancetrio.app.common.model.PageMetadata;
import com.endurancetrio.app.common.service.MessageService;
import com.endurancetrio.app.common.utils.PageMetadataUtils;
import com.endurancetrio.app.common.utils.WebUtils;
import com.endurancetrio.app.config.AppProperties;
import com.endurancetrio.business.common.exception.EnduranceTrioException;
import com.endurancetrio.business.newsletter.dto.NewsletterSubscriptionDTO;
import com.endurancetrio.business.newsletter.service.NewsletterService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Web controller for the newsletter subscription flow.
 * <p>
 * Handles the footer form POST, delegates to {@link NewsletterService} to subscribe the visitor via
 * the Kit.com v4 API, and redirects to a dedicated result page. Also serves the three result pages:
 * {@code newsletter-success}, {@code newsletter-confirmed}, and {@code newsletter-error}.
 */
@EnduranceTrioWebController
public class NewsletterWebController {

  private static final Logger LOG = LoggerFactory.getLogger(NewsletterWebController.class);

  private static final String VIEW_SUCCESS = "newsletter-success";
  private static final String VIEW_CONFIRMED = "newsletter-confirmed";
  private static final String VIEW_ERROR = "newsletter-error";

  private final AppProperties appProperties;
  private final MessageService messageService;
  private final NewsletterService newsletterService;

  @Autowired
  public NewsletterWebController(
      AppProperties appProperties, MessageService messageService,
      NewsletterService newsletterService

  ) {
    this.appProperties = appProperties;
    this.messageService = messageService;
    this.newsletterService = newsletterService;
  }

  @GetMapping("/{language:en|pt}/newsletter/confirmed")
  public String confirmed(@PathVariable String language, HttpServletRequest request, Model model) {
    return renderView(VIEW_CONFIRMED, language, request, model);
  }

  @GetMapping("/{language:en|pt}/newsletter/error")
  public String error(@PathVariable String language, HttpServletRequest request, Model model) {
    return renderView(VIEW_ERROR, language, request, model);
  }

  @PostMapping("/{language:en|pt}/newsletter/subscribe")
  public String subscribe(
      @PathVariable String language, @RequestParam(required = false) String firstName,
      @RequestParam(required = false) String email
  ) {

    firstName = sanitize(firstName);
    email = sanitize(email);

    String emailDomain = extractDomain(email);

    if (!isValid(firstName, email)) {
      LOG.warn("Newsletter subscription validation failed for domain [{}]", emailDomain);
      return WebUtils.redirect(language, "/newsletter/error");
    }

    NewsletterSubscriptionDTO dto = new NewsletterSubscriptionDTO(firstName, email);

    try {
      newsletterService.subscribe(dto, language);
      LOG.info("Newsletter subscription succeeded for domain [{}]", emailDomain);
      return WebUtils.redirect(language, "/newsletter/success");
    } catch (EnduranceTrioException exception) {
      LOG.warn("Newsletter subscription API failed for domain [{}]: {}", emailDomain,
          exception.getMessage()
      );
      return WebUtils.redirect(language, "/newsletter/error");
    }
  }

  @GetMapping("/{language:en|pt}/newsletter/success")
  public String success(@PathVariable String language, HttpServletRequest request, Model model) {
    return renderView(VIEW_SUCCESS, language, request, model);
  }

  private static String extractDomain(String email) {
    int atIndex = email.indexOf("@");
    return atIndex > 0 && atIndex < email.length() - 1 ? email.substring(atIndex + 1) : "unknown";
  }

  private boolean isValid(String firstName, String email) {
    return firstName.length() >= 2 && firstName.length() <= 100 && email.length() <= 254
        && email.contains("@") && !email.startsWith("@") && !email.endsWith("@");
  }

  private String renderView(String view, String language, HttpServletRequest request, Model model) {
    Locale locale = "pt".equalsIgnoreCase(language) ? LOCALE_PORTUGUESE : Locale.ENGLISH;

    PageMetadata metadata = PageMetadataUtils.create(view,
        messageService.getMessage("page.newsletter." + view + ".metadata.title", null, locale),
        messageService.getMessage("page.newsletter." + view + ".metadata.description", null,
            locale
        ), request, appProperties
    );

    model.addAttribute(LANGUAGE, locale.getLanguage());
    model.addAttribute(METADATA, metadata);

    return view;
  }

  private String sanitize(String value) {
    if (value == null || value.isBlank()) {
      return "";
    }
    return value.strip();
  }
}
