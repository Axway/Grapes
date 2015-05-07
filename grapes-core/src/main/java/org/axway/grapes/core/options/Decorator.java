package org.axway.grapes.core.options;

import org.axway.grapes.model.api.ServerAPI;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Decorator
 *
 * <p>The Decorator holds all the filters that take part in the display rendering.</p>
 *
 * @author jdcoffre
 */
public class Decorator {

    /** Value - {@value}, boolean query parameter used to show the licenses in reports.
     * Default value: true. */
    private Boolean showLicenses = true;

    /** Value - {@value}, boolean query parameter used to show the license long name in reports.
     * Default value: false. */
    private Boolean showLicensesLongName = false;

    /** Value - {@value}, boolean query parameter used to show the license url in reports.
     * Default value: false. */
    private Boolean showLicensesUrl = false;

    /** Value - {@value}, boolean query parameter used to show the license comment in reports.
     * Default value: false. */
    private Boolean showLicensesComment = false;

    /** Value - {@value}, boolean query parameter used to show the scopes in reports.
     * Default value: true. */
    private Boolean showScopes = true;

    /** Value - {@value}, boolean query parameter used to show the ancestors in reports.
     * Default value: false. */
    private Boolean showAncestors = false;

    /** Value - {@value}, boolean query parameter used to show the third party in reports.
     * Default value: false. */
    private Boolean showThirdparty = false;

    /** Value - {@value}, boolean query parameter used to show artifacts provider in reports.
     * Default value: false. */
    private Boolean showProviders = false;

    /** Value - {@value}, boolean query parameter used to show the dependency sources in reports.
     * Default value: true. */
    private Boolean showSources = true;

    /** Value - {@value}, boolean query parameter used to show the dependency source version in reports.
     * Default value: false. */
    private Boolean showSourcesVersion = false;

    /** Value - {@value}, boolean query parameter used to show the dependency target gavc in reports.
     * Default value: true. */
    private Boolean showTargets = true;

    /** Value - {@value}, boolean query parameter used to show the dependency target download url in reports.
     * Default value: false. */
    private Boolean showTargetsDownloadUrl = false;

    /** Value - {@value}, boolean query parameter used to show the dependency target size in reports.
     * Default value: false. */
    private Boolean showTargetsSize = false;

    /** Value - {@value}, boolean query parameter used to show the corporate dependencies in reports.
     * Default value: true. */
    private Boolean showCorporate = true;


    public Boolean getShowLicenses() {
        return showLicenses;
    }

    public void setShowLicenses(final Boolean showLicenses) {
        if(showLicenses != null){
            this.showLicenses = showLicenses;
        }
    }

    private void setShowLicenses(final String showLicenses) {
        if(showLicenses != null){
            setShowLicenses(Boolean.valueOf(showLicenses));
        }
    }

    public Boolean getShowScopes() {
        return showScopes;
    }

    public void setShowScopes(final Boolean showScopes) {
        if(showScopes != null){
            this.showScopes = showScopes;
        }
    }

    private void setShowScopes(final String showScopes) {
        if(showScopes != null){
            setShowScopes(Boolean.valueOf(showScopes));
        }
    }

    public Boolean getShowAncestors() {
        return showAncestors;
    }

    public void setShowAncestors(final Boolean showAncestors) {
        if(showAncestors != null){
            this.showAncestors = showAncestors;
        }
    }

    private void setShowAncestors(final String showAncestors) {
        if(showAncestors != null){
            setShowAncestors(Boolean.valueOf(showAncestors));
        }
    }

    public Boolean getShowCorporate() {
        return showCorporate;
    }

    public void setShowCorporate(final Boolean showCorporate) {
        if(showCorporate != null){
            this.showCorporate = showCorporate;
        }
    }

    private void setShowCorporate(final String showCorporate) {
        if(showCorporate != null){
            setShowCorporate(Boolean.valueOf(showCorporate));
        }
    }

    public Boolean getShowThirdparty() {
        return showThirdparty;
    }

    public void setShowThirdparty(final Boolean showThirdparty) {
        if(showThirdparty != null){
            this.showThirdparty = showThirdparty;
        }
    }

    private void setShowThirdparty(final String showThirdparty) {
        if(showThirdparty != null){
            setShowThirdparty(Boolean.valueOf(showThirdparty));
        }
    }

    public Boolean getShowSources() {
        return showSources;
    }

    public void setShowSources(final Boolean showSources) {
        if(showSources != null){
            this.showSources = showSources;
        }
    }

    private void setShowSources(final String showSources) {
        if(showSources != null){
            setShowSources(Boolean.valueOf(showSources));
        }
    }

    public Boolean getShowProviders() {
        return showProviders;
    }

    public void setShowProviders(final Boolean showProvider) {
        if(showProvider != null){
            this.showProviders = showProvider;
        }
    }

    private void setShowProviders(final String showProvider) {
        if(showProvider != null){
            setShowProviders(Boolean.valueOf(showProvider));
        }
    }

    public Boolean getShowLicensesLongName() {
        return showLicensesLongName;
    }

    public void setShowLicensesLongName(final Boolean showLicensesLongName) {
        if (showLicensesLongName != null){
            this.showLicensesLongName = showLicensesLongName;
        }
    }

    public void setShowLicensesLongName(final String showLicensesLongName) {
        if (showLicensesLongName != null){
            this.showLicensesLongName = Boolean.valueOf(showLicensesLongName);
        }
    }

    public Boolean getShowLicensesUrl() {
        return showLicensesUrl;
    }

    public void setShowLicensesUrl(final Boolean showLicensesUrl) {
        if(showLicensesUrl != null) {
            this.showLicensesUrl = showLicensesUrl;
        }
    }

    public void setShowLicensesUrl(final String showLicensesUrl) {
        if(showLicensesUrl != null) {
            this.showLicensesUrl = Boolean.valueOf(showLicensesUrl);
        }
    }

    public Boolean getShowLicensesComment() {
        return showLicensesComment;
    }

    public void setShowLicensesComment(final Boolean showLicensesComment) {
        if(showLicensesComment != null) {
            this.showLicensesComment = showLicensesComment;
        }
    }

    public void setShowLicensesComment(final String showLicensesComment) {
        if(showLicensesComment != null) {
            this.showLicensesComment = Boolean.valueOf(showLicensesComment);
        }
    }

    public Boolean getShowSourcesVersion() {
        return showSourcesVersion;
    }

    public void setShowSourcesVersion(final Boolean showSourcesVersion) {
        if( showSourcesVersion != null ) {
            this.showSourcesVersion = showSourcesVersion;
        }
    }

    public void setShowSourcesVersion(final String showSourcesVersion) {
        if( showSourcesVersion != null ) {
            this.showSourcesVersion = Boolean.valueOf(showSourcesVersion);
        }
    }

    public boolean getShowTargets() {
        return showTargets;
    }

    public void setShowTargets(final Boolean showTargets) {
        if(showTargets != null) {
            this.showTargets = showTargets;
        }
    }

    public void setShowTargets(final String showTargets) {
        if(showTargets != null) {
            this.showTargets = Boolean.valueOf(showTargets);
        }
    }

    public Boolean getShowTargetsDownloadUrl() {
        return showTargetsDownloadUrl;
    }

    public void setShowTargetsDownloadUrl(final Boolean showTargetsDownloadUrl) {
        if(showTargetsDownloadUrl != null) {
            this.showTargetsDownloadUrl = showTargetsDownloadUrl;
        }
    }

    public void setShowTargetsDownloadUrl(final String showTargetsDownloadUrl) {
        if(showTargetsDownloadUrl != null) {
            this.showTargetsDownloadUrl = Boolean.valueOf(showTargetsDownloadUrl);
        }
    }

    public Boolean getShowTargetsSize() {
        return showTargetsSize;
    }

    public void setShowTargetsSize(final Boolean showTargetsSize) {
        if(showTargetsSize != null) {
            this.showTargetsSize = showTargetsSize;
        }
    }

    public void setShowTargetsSize(final String showTargetsSize) {
        if(showTargetsSize != null) {
            this.showTargetsSize = Boolean.valueOf(showTargetsSize);
        }
    }

    public void init(final MultivaluedMap<String, String> queryParameters){
        setShowScopes(queryParameters.getFirst(ServerAPI.SHOW_SCOPE_PARAM));
        setShowLicenses(queryParameters.getFirst(ServerAPI.SHOW_LICENSE_PARAM));
        setShowThirdparty(queryParameters.getFirst(ServerAPI.SHOW_THIRPARTY_PARAM));
        setShowCorporate(queryParameters.getFirst(ServerAPI.SHOW_CORPORATE_PARAM));
        setShowSources(queryParameters.getFirst(ServerAPI.SHOW_SOURCES_PARAM));
        setShowAncestors(queryParameters.getFirst(ServerAPI.SHOW_ANCESTOR_PARAM));
        setShowProviders(queryParameters.getFirst(ServerAPI.SHOW_PROVIDERS_PARAM));
        setShowTargets(queryParameters.getFirst(ServerAPI.SHOW_TARGET_PARAM));
        setShowTargetsDownloadUrl(queryParameters.getFirst(ServerAPI.SHOW_TARGET_URL_PARAM));
        setShowTargetsSize(queryParameters.getFirst(ServerAPI.SHOW_SIZE));
        setShowSourcesVersion(queryParameters.getFirst(ServerAPI.SHOW_SOURCES_VERSION_PARAM));
        setShowLicensesLongName(queryParameters.getFirst(ServerAPI.SHOW_LICENSE_FULL_NAME_PARAM));
        setShowLicensesComment(queryParameters.getFirst(ServerAPI.SHOW_LICENSE_COMMENT_PARAM));
        setShowLicensesUrl(queryParameters.getFirst(ServerAPI.SHOW_LICENSE_URL_PARAM));
    }
}
