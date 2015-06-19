package org.axway.grapes.core.options;

import org.axway.grapes.core.options.filters.*;
import org.axway.grapes.model.api.ServerAPI;
import org.axway.grapes.model.datamodel.Dependency;
import org.axway.grapes.model.datamodel.License;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *  Holds the filters.
 *
 * @author jdcoffre
 */
public class FiltersHolder {

    private final List<Filter> filters = new ArrayList<Filter>();

    private final ScopeHandler scopeHandler = new ScopeHandler();
    private final Decorator decorator = new Decorator();
    private final DepthHandler depthHandler = new DepthHandler();
    private CorporateFilter corporateFilter;

    public ScopeHandler getScopeHandler(){
        return scopeHandler;
    }

    public Decorator getDecorator() {
        return decorator;
    }

    public DepthHandler getDepthHandler() {
        return depthHandler;
    }

    public CorporateFilter getCorporateFilter() {
        return corporateFilter;
    }

    public void setCorporateFilter(final CorporateFilter filter) {
        corporateFilter = filter;
    }

    public void addFilter(final Filter newFilter) {
        Filter toRemove = null;

        for(Filter filter: filters){
            if(filter.getClass().equals(newFilter.getClass())){
                toRemove = filter;
            }
        }

        if(toRemove != null){
            filters.remove(toRemove);
        }

        filters.add(newFilter);
    }

    public void init(final Map<String, List<String>> queryParameters) {
        scopeHandler.init(queryParameters);
        decorator.init(queryParameters);
        depthHandler.init(queryParameters);

        final List<String> approved = queryParameters.get(ServerAPI.APPROVED_PARAM);
        if(approved != null){
            filters.add(new ApprovedFilter(Boolean.valueOf(approved.get(0))));
        }

        final List<String> promoted = queryParameters.get(ServerAPI.PROMOTED_PARAM);
        if(promoted != null){
            filters.add(new PromotedFilter(Boolean.valueOf(promoted.get(0))));
        }

        final List<String> doNotUse = queryParameters.get(ServerAPI.DO_NOT_USE);
        if(doNotUse != null){
            filters.add(new DoNotUseFilter(Boolean.valueOf(doNotUse.get(0))));
        }

        final List<String> gavc = queryParameters.get(ServerAPI.GAVC);
        if(gavc != null){
            filters.add(new GavcFilter(gavc.get(0)));
        }

        final List<String> hasLicense = queryParameters.get(ServerAPI.HAS_LICENSE_PARAM);
        if(hasLicense != null){
            filters.add(new HasLicenseFilter(Boolean.valueOf(hasLicense.get(0))));
        }

        final List<String> toBeValidated = queryParameters.get(ServerAPI.TO_BE_VALIDATED_PARAM);
        if(toBeValidated != null){
           filters.add(new ToBeValidatedFilter(Boolean.valueOf(toBeValidated.get(0))));
        }

        final List<String> licenseId = queryParameters.get(ServerAPI.LICENSE_ID_PARAM);
        if(licenseId != null){
            filters.add(new LicenseIdFilter(licenseId.get(0)));
        }

        final List<String> classifier = queryParameters.get(ServerAPI.CLASSIFIER_PARAM);
        if(classifier != null){
            filters.add(new ClassifierFilter(classifier.get(0)));
        }

        final List<String> extension = queryParameters.get(ServerAPI.EXTENSION_PARAM);
        if(extension != null){
            filters.add(new ExtensionFilter(extension.get(0)));
        }

        final List<String> type = queryParameters.get(ServerAPI.TYPE_PARAM);
        if(type != null){
            filters.add(new TypeFilter(type.get(0)));
        }

        final List<String> version = queryParameters.get(ServerAPI.VERSION_PARAM);
        if(version != null){
            filters.add(new VersionFilter(version.get(0)));
        }

        final List<String> artifactId = queryParameters.get(ServerAPI.ARTIFACTID_PARAM);
        if(artifactId != null){

            filters.add(new ArtifactIdFilter(artifactId.get(0)));

        }

        final List<String> groupId = queryParameters.get(ServerAPI.GROUPID_PARAM);

        if(groupId != null){
            filters.add(new GroupIdFilter(groupId.get(0)));
        }

        final List<String> name = queryParameters.get(ServerAPI.NAME_PARAM);
        if(name != null){
            filters.add(new ModuleNameFilter(name.get(0)));
        }

        final List<String> organization = queryParameters.get(ServerAPI.ORGANIZATION_PARAM);
        if(organization != null){
            filters.add(new OrganizationFilter(organization.get(0)));
        }
    }
    public boolean shouldBeInReport(final License license) {
        for(Filter filter: filters){
            if(!filter.filter(license)){
                return false;
            }
        }

        return true;
    }

    /**
     * Check if a dependency matches the filters
     *
     * @param dependency
     *
     * @return boolean
     */
    public boolean shouldBeInReport(final Dependency dependency) {
        if(dependency == null){
            return false;
        }
        if(dependency.getTarget() == null){
            return false;
        }
        if(corporateFilter != null){
            if(!decorator.getShowThirdparty() && !corporateFilter.filter(dependency)){
                return false;
            }
            if(!decorator.getShowCorporate() && corporateFilter.filter(dependency)){
                return false;
            }
        }

        if(!scopeHandler.filter(dependency)){
            return false;
        }

        return true;
    }

	/**
	 * Generates a Map of query parameters for Artifact regarding the filters
     *
	 * @return Map<String, Object>
	 */
	public Map<String, Object> getArtifactFieldsFilters() {
		final Map<String, Object> params = new HashMap<String, Object>();

        for(Filter filter: filters){
            params.putAll(filter.artifactFilterFields());
        }

		return params;
	}

    /**
     * Generates a Map of query parameters for Module regarding the filters
     *
     * @return Map<String, Object>
     */
    public Map<String, Object> getModuleFieldsFilters() {
        final Map<String, Object> params = new HashMap<String, Object>();

        for(Filter filter: filters){
            params.putAll(filter.moduleFilterFields());
        }

        return params;
    }
}