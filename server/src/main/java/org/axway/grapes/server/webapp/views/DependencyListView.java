package org.axway.grapes.server.webapp.views;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yammer.dropwizard.views.View;
import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.Dependency;
import org.axway.grapes.commons.datamodel.License;
import org.axway.grapes.server.core.options.Decorator;
import org.axway.grapes.server.webapp.views.serialization.DependencyListSerializer;
import org.axway.grapes.server.webapp.views.utils.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // Title of the HTML page
    private final String title;

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
    private final List<Dependency> dependencies = new ArrayList<Dependency>();

    // The available licenses to complete dependencies' information
    private Map<String, License> licenseDictionary = new HashMap<String, License>();

    public DependencyListView(final String title, final List<License> licenses, final Decorator decorator) {
        super("DependencyListView.ftl");
        this.title = title;
        setLicenses(licenses);
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
        for(Dependency dependency: dependencies){
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
        for(Dependency dependency: dependencies){
            final List<String> licenseIds = dependency.getTarget().getLicenses();

            // A dependency can have many rows if it has many licenses
            if(!licenseIds.isEmpty()){
                for(String licenseId: dependency.getTarget().getLicenses()){
                    final License license = getLicense(licenseId);
                    table.addRow(getDependencyCells(dependency, license));
                }
            }
            else{
                table.addRow(getDependencyCells(dependency, DataModelFactory.createLicense("","","","","")));
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
        License license = licenseDictionary.get(licenseId);

        if(license == null){
            license = DataModelFactory.createLicense("#" + licenseId + "# (to be identified)", "not identified yet", "not identified yet", "not identified yet", "not identified yet" );
            license.setUnknown(true);
        }

        return license;
    }

    /**
     * Init the headers of the table regarding the filters
     *
     * @return String[]
     */
    private String[] getHeaders() {
        final List<String> headers = new ArrayList<String>();

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
        final List<String> cells = new ArrayList<String>();

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

    private void setLicenses(final List<License> licenses) {
        licenseDictionary.clear();
        for(License license: licenses){
            licenseDictionary.put(license.getName(), license);
        }
    }
}
