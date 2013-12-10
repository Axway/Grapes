package org.axway.grapes.server.core.options;

import org.axway.grapes.commons.api.ServerAPI;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Decorator
 *
 * <p>The Decorator holds all the parameters </p>
 *
 * <author>jdcoffre</author>
 */
public class Decorator {

    /** Value - {@value}, boolean query parameter used to show the licenses in reports.
     * Default value: true. */
    private Boolean showLicenses = false;

    /** Value - {@value}, boolean query parameter used to show the sizes in reports.
     * Default value: false. */
    private Boolean showSizes = false;

    /** Value - {@value}, boolean query parameter used to show the download URLs in reports.
     * Default value: false. */
    private Boolean showURLs = false;

    /** Value - {@value}, boolean query parameter used to show the scopes in reports.
     * Default value: true. */
    private Boolean showScopes = true;

    /** Value - {@value}, boolean query parameter used to show the ancestors in reports.
     * Default value: false. */
    private Boolean showAncestors = false;

    /** Value - {@value}, boolean query parameter used to show the third party in reports.
     * Default value: false. */
    private Boolean showThirdparty = false;

    /** Value - {@value}, boolean query parameter used to show artifacts provider in reports.*/
    private Boolean showProviders = false;

    /** Value - {@value}, boolean query parameter used to show the dependency sources in reports.
     * Default value: false. */
    private Boolean showSources = true;public Boolean getShowLicenses() {
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

    public Boolean getShowSizes() {
        return showSizes;
    }

    public void setShowSizes(final Boolean showSizes) {
        if(showSizes != null){
            this.showSizes = showSizes;
        }
    }

    private void setShowSizes(final String showSizes) {
        if(showSizes != null){
            setShowSizes(Boolean.valueOf(showSizes));
        }
    }

    public Boolean getShowURLs() {
        return showURLs;
    }

    public void setShowURLs(final Boolean showURLs) {
        if(showURLs != null){
            this.showURLs = showURLs;
        }
    }

    private void setShowURLs(final String showURLs) {
        if(showURLs != null){
            setShowURLs(Boolean.valueOf(showURLs));
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


    public void init(final MultivaluedMap<String, String> queryParameters){
        setShowScopes(queryParameters.getFirst(ServerAPI.SHOW_SCOPE_PARAM));
        setShowLicenses(queryParameters.getFirst(ServerAPI.SHOW_LICENSE_PARAM));
        setShowSizes(queryParameters.getFirst(ServerAPI.SHOW_SIZE));
        setShowURLs(queryParameters.getFirst(ServerAPI.SHOW_URL_PARAM));
        setShowThirdparty(queryParameters.getFirst(ServerAPI.SHOW_THIRPARTY_PARAM));
        setShowSources(queryParameters.getFirst(ServerAPI.SHOW_SOURCES_PARAM));
        setShowAncestors(queryParameters.getFirst(ServerAPI.SHOW_ANCESTOR_PARAM));
        setShowProviders(queryParameters.getFirst(ServerAPI.SHOW_PROVIDERS_PARAM));
    }
}
