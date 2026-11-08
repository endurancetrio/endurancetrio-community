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
import com.endurancetrio.app.common.utils.WebUtils;
import com.endurancetrio.app.config.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@EnduranceTrioWebController
public class MissionWebController {

  private static final String VIEW = "mission";

  private final MessageService messageService;
  private final AppProperties appProperties;

  @Autowired
  public MissionWebController(MessageService messageService, AppProperties appProperties) {
    this.messageService = messageService;
    this.appProperties = appProperties;
  }

  @GetMapping("/{language:en|pt}/about")
  public String redirectAboutToMission(@PathVariable String language) {

    return WebUtils.redirect(language, "/mission");
  }

  @GetMapping("/{language:en|pt}/mission")
  public String mission(@PathVariable String language, HttpServletRequest request, Model model) {
    Locale locale = "pt".equalsIgnoreCase(language) ? LOCALE_PORTUGUESE : Locale.ENGLISH;

    PageMetadata metadata = PageMetadataUtils.create(VIEW,
        messageService.getMessage("page.mission.metadata.title", null, locale),
        messageService.getMessage("page.mission.metadata.description", null, locale), request,
        appProperties
    );

    model.addAttribute(LANGUAGE, locale.getLanguage());
    model.addAttribute(METADATA, metadata);

    return VIEW;
  }
}
