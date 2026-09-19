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

package com.endurancetrio.app.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

  private String copyrightYear;
  private List<Long> featuredArticleIds;
  private Long featuredEventArticleId;
  private List<Long> featuredEventIds;
  private Google google = new Google();
  private KoFi kofi = new KoFi();
  private OpenGraph openGraph = new OpenGraph();
  private String siteUrl;
  private Social social = new Social();
  private String version;

  public String getCopyrightYear() {
    return copyrightYear;
  }

  public void setCopyrightYear(String copyrightYear) {
    this.copyrightYear = copyrightYear;
  }

  public List<Long> getFeaturedArticleIds() {
    return featuredArticleIds;
  }

  public void setFeaturedArticleIds(List<Long> featuredArticleIds) {
    this.featuredArticleIds = featuredArticleIds;
  }

  public Long getFeaturedEventArticleId() {
    return featuredEventArticleId;
  }

  public void setFeaturedEventArticleId(Long featuredEventArticleId) {
    this.featuredEventArticleId = featuredEventArticleId;
  }

  public List<Long> getFeaturedEventIds() {
    return featuredEventIds;
  }

  public void setFeaturedEventIds(List<Long> featuredEventIds) {
    this.featuredEventIds = featuredEventIds;
  }

  public Google getGoogle() {
    return google;
  }

  public void setGoogle(Google google) {
    this.google = google;
  }

  public KoFi getKoFi() {
    return kofi;
  }

  public void setKoFi(KoFi kofi) {
    this.kofi = kofi;
  }

  public OpenGraph getOpenGraph() {
    return openGraph;
  }

  public void setOpenGraph(OpenGraph openGraph) {
    this.openGraph = openGraph;
  }

  public String getSiteUrl() {
    return siteUrl;
  }

  public void setSiteUrl(String siteUrl) {
    this.siteUrl = siteUrl;
  }

  public Social getSocial() {
    return social;
  }

  public void setSocial(Social social) {
    this.social = social;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public static class Google {

    private String adsenseId;

    public String getAdsenseId() {
      return adsenseId;
    }

    public void setAdsenseId(String adsenseId) {
      this.adsenseId = adsenseId;
    }
  }

  public static class KoFi {

    private String userId;

    public String getUserId() {
      return userId;
    }

    public void setUserId(String userId) {
      this.userId = userId;
    }
  }

  public static class OpenGraph {

    private String defaultImg;
    private Integer defaultImgWidth;
    private Integer defaultImgHeight;

    public String getDefaultImg() {
      return defaultImg;
    }

    public void setDefaultImg(String defaultImg) {
      this.defaultImg = defaultImg;
    }

    public Integer getDefaultImgWidth() {
      return defaultImgWidth;
    }

    public void setDefaultImgWidth(Integer defaultImgWidth) {
      this.defaultImgWidth = defaultImgWidth;
    }

    public Integer getDefaultImgHeight() {
      return defaultImgHeight;
    }

    public void setDefaultImgHeight(Integer defaultImgHeight) {
      this.defaultImgHeight = defaultImgHeight;
    }
  }

  public static class Social {

    private String facebookPageId;
    private String twitterSite;

    public String getFacebookPageId() {
      return facebookPageId;
    }

    public void setFacebookPageId(String facebookPageId) {
      this.facebookPageId = facebookPageId;
    }

    public String getTwitterSite() {
      return twitterSite;
    }

    public void setTwitterSite(String twitterSite) {
      this.twitterSite = twitterSite;
    }
  }
}
