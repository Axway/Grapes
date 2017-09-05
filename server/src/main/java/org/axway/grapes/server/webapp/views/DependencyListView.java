package org.axway.grapes.server.webapp.views;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yammer.dropwizard.views.View;
import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.Dependency;
import org.axway.grapes.commons.datamodel.License;
import org.axway.grapes.server.core.interfaces.LicenseMatcher;
import org.axway.grapes.server.core.options.Decorator;
import org.axway.grapes.server.db.ModelMapper;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.webapp.views.serialization.DependencyListSerializer;
import org.axway.grapes.server.webapp.views.utils.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Dependency List View
 *
 * <p>Handles the dependency list for the web-app display. It is able to generate tables that contains custom
 * dependencies information.</p>
 *
 * @author jdcoffre
 */
@JsonSerialize(using=DependencyListSerializer.class)
public class DependencyListView extends View {

    private static final Logger LOG = LoggerFactory.getLogger(DependencyListView.class);

    public static final String NOT_IDENTIFIED_YET = "not identified yet";
    // Title of the HTML page
    private final String title;
    private final ModelMapper mapper;
    private LicenseMatcher licenseMatcher;

    // Gathers all the display options
    private Decorator decorator;

    // Value of the header of the column "source" in the dependency table
    public static final String SOURCE_FIELD = "Source";

    // Value of the header of the column "source version" in the dependency table
    public static final String SOURCE_VERSION_FIELD = "Source Version";

    // Value of the header of the column "target" in the dependency table
    public static final String TARGET_FIELD = "Target";

    // Value of the header of the column "download url" in the dependency table
    public static final String DOWNLOAD_URL_FIELD = "Download Url";

    // Value of the header of the column "size" in the dependency table
    public static final String SIZE_FIELD = "Size";

    // Value of the header of the column "scope" in the dependency table
    public static final String SCOPE_FIELD = "Scope";

    // Value of the header of the column "license" in the dependency table
    public static final String LICENSE_FIELD = "License";

    // Value of the header of the column "License Long Name" in the dependency table
    public static final String LICENSE_LONG_NAME_FIELD = "License Full Name";

    // Value of the header of the column "license url" in the dependency table
    public static final String LICENSE_URL_FIELD = "License Url";

    // Value of the header of the column "license commentary" in the dependency table
    public static final String LICENSE_COMMENT_FIELD = "License Comment";

    // The dependency list to display
    private final List<Dependency> dependencies = new ArrayList<>();

    public DependencyListView(final String title,
                              final Decorator decorator,
                              final LicenseMatcher licenseMatcher,
                              final ModelMapper mapper,
                              final String templateName) {
        super(templateName);
        this.title = title;
        this.licenseMatcher = licenseMatcher;
        this.mapper = mapper;
        this.decorator = decorator;
    }

    /**
     * Returns the HTML page title
     *
     * @return String
     */
    public String getTitle() {
        return title;
    }

    /**
     * Add a dependency to the list
     *
     * @param dependency Dependency
     */
    public void addDependency(final Dependency dependency) {
        if(!dependencies.contains(dependency)){
            dependencies.add(dependency);
        }
    }

    /**
     * Add many dependencies to the list
     *
     * @param dependencies List<Dependency>
     */
    public void addAll(final List<Dependency> dependencies) {
        for(final Dependency dependency: dependencies){
            addDependency(dependency);
        }
    }

    /**
     * Get the dependencies of the view
     *
     * @return List<Dependency>
     */
    public List<Dependency> getDependencies() {
        return dependencies;
    }

    /**
     * Generate a table that contains the dependencies information with the column that match the configured filters
     *
     * @return Table
     */
    public Table getTable(){
        final Table table = new Table(getHeaders());

        // Create row(s) per dependency
        for(final Dependency dependency: dependencies){
            final List<String> licenseIds = dependency.getTarget().getLicenses();

            // A dependency can have many rows if it has many licenses
            if(licenseIds.isEmpty()){
                table.addRow(getDependencyCells(dependency, DataModelFactory.createLicense("","","","","")));
            }
            else{
                for(final String licenseId: dependency.getTarget().getLicenses()){
                    final License license = getLicense(licenseId);
                    table.addRow(getDependencyCells(dependency, license));
                }
            }
        }

        return table;
    }

    /**
     * Returns a licenses regarding its Id and a fake on if no license exist with such an Id
     *
     * @param licenseId String
     * @return License
     */
    private License getLicense(final String licenseId) {
        License result = null;
        final Set<DbLicense> matchingLicenses = licenseMatcher.getMatchingLicenses(licenseId);

        if (matchingLicenses.isEmpty()) {
            result = DataModelFactory.createLicense("#" + licenseId + "# (to be identified)", NOT_IDENTIFIED_YET, NOT_IDENTIFIED_YET, NOT_IDENTIFIED_YET, NOT_IDENTIFIED_YET);
            result.setUnknown(true);
        } else {
            if (matchingLicenses.size() > 1 && LOG.isWarnEnabled()) {
                LOG.warn(String.format("%s matches multiple licenses %s. " +
                                "Please run the report showing multiple matching on licenses",
                        licenseId, matchingLicenses.toString()));
            }
            result = mapper.getLicense(matchingLicenses.iterator().next());

        }

        return result;
    }

    /**
     * Init the headers of the table regarding the filters
     *
     * @return String[]
     */
    private String[] getHeaders() {
        final List<String> headers = new ArrayList<>();

        if(decorator.getShowSources()){
            headers.add(SOURCE_FIELD);
        }

        if(decorator.getShowSourcesVersion()){
            headers.add(SOURCE_VERSION_FIELD);
        }

        if(decorator.getShowTargets()){
            headers.add(TARGET_FIELD);
        }

        if(decorator.getShowTargetsDownloadUrl()){
            headers.add(DOWNLOAD_URL_FIELD);
        }

        if(decorator.getShowTargetsSize()){
            headers.add(SIZE_FIELD);
        }

        if(decorator.getShowScopes()){
            headers.add(SCOPE_FIELD);
        }

        if(decorator.getShowLicenses()){
            headers.add(LICENSE_FIELD);
        }

        if(decorator.getShowLicensesLongName()){
            headers.add(LICENSE_LONG_NAME_FIELD);
        }

        if(decorator.getShowLicensesUrl()){
            headers.add(LICENSE_URL_FIELD);
        }

        if(decorator.getShowLicensesComment()){
            headers.add(LICENSE_COMMENT_FIELD);
        }

        return headers.toArray(new String[headers.size()]);
    }

    /**
     * Retrieve the require information (regarding filters) of a dependency
     *
     * @param dependency Dependency
     * @param license License
     * @return String[]
     */
    private String[] getDependencyCells(final Dependency dependency, final License license) {
        final List<String> cells = new ArrayList<>();

        if(decorator.getShowSources()){
            cells.add(dependency.getSourceName());
        }

        if(decorator.getShowSourcesVersion()){
            cells.add(dependency.getSourceVersion());
        }

        if(decorator.getShowTargets()){
            cells.add(dependency.getTarget().getGavc());
        }

        if(decorator.getShowTargetsDownloadUrl()){
            cells.add(dependency.getTarget().getDownloadUrl());
        }

        if(decorator.getShowTargetsSize()){
            cells.add(dependency.getTarget().getSize());
        }

        if(decorator.getShowScopes()){
            cells.add(dependency.getScope().name());
        }

        if(decorator.getShowLicenses()){
            cells.add(license.getName());
        }

        if(decorator.getShowLicensesLongName()){
            cells.add(license.getLongName());
        }

        if(decorator.getShowLicensesUrl()){
            cells.add(license.getUrl());
        }

        if(decorator.getShowLicensesComment()){
            cells.add(license.getComments());
        }

        return cells.toArray(new String[cells.size()]);
    }

    public String getArtifactLink(String gavc) {
        final Optional<Dependency> first = dependencies.stream().filter(dep -> dep.getTarget().getGavc().equalsIgnoreCase(gavc)).findFirst();

        if(first.isPresent()) {
            final Artifact a = first.get().getTarget();
            return String.format("/webapp?section=artifacts&groupId=%s&artifactId=%s&version=%s",
                    a.getGroupId(), a.getArtifactId(), a.getVersion());
        }

        return "/webapp/section=artifacts";
    }
}
