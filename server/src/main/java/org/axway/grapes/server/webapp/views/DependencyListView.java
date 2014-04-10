package org.axway.grapes.server.webapp.views;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yammer.dropwizard.views.View;
import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.Dependency;
import org.axway.grapes.commons.datamodel.License;
import org.axway.grapes.server.webapp.views.serialization.DependencyListSerializer;
import org.axway.grapes.server.webapp.views.utils.Table;

import java.util.ArrayList;
import java.util.Hashtable;
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

    // Value of the header of the column "source" in the dependency table
    public final static String SOURCE_FIELD = "Source Name";

    // Should we display the column "source" in the dependency table
    private boolean showSourceName = true;

    // Value of the header of the column "source version" in the dependency table
    public final static String SOURCE_VERSION_FIELD = "Source Version";

    // Should we display the column "source version" in the dependency table
    private boolean showSourceVersion = true;

    // Value of the header of the column "target" in the dependency table
    public final static String TARGET_FIELD = "Target";

    // Should we display the column "target" in the dependency table
    private boolean showTarget = true;

    // Value of the header of the column "download url" in the dependency table
    public final static String DOWNLOAD_URL_FIELD = "Download Url";

    // Should we display the column "download url" in the dependency table
    private boolean showTargetDownloadUrl = false;

    // Value of the header of the column "size" in the dependency table
    public final static String SIZE_FIELD = "Size";

    // Should we display the column "size" in the dependency table
    private boolean showSize = false;

    // Value of the header of the column "scope" in the dependency table
    public final static String SCOPE_FIELD = "Scope";

    // Should we display the column "scope" in the dependency table
    private boolean showScope = true;

    // Value of the header of the column "license" in the dependency table
    public final static String LICENSE_FIELD = "License(s)";

    // Should we display the column "license" in the dependency table
    private boolean showLicense = true;

    // Value of the header of the column "License Long Name" in the dependency table
    public final static String LICENSE_LONG_NAME__FIELD = "License(s) (full name)";

    // Should we display the column "license long name" in the dependency table
    private boolean showLicenseLongName = false;

    // Value of the header of the column "license url" in the dependency table
    public final static String LICENSE_URL_FIELD = "License Url";

    // Should we display the column "license url" in the dependency table
    private boolean showLicenseUrl = false;

    // Value of the header of the column "license commentary" in the dependency table
    public final static String LICENSE_COMMENT_FIELD = "Do not use";

    // Should we display the column "license commentary" in the dependency table
    private boolean showLicenseComment = false;

    // The dependency list to display
    private final List<Dependency> dependencies = new ArrayList<Dependency>();

    // The available licenses to complete dependencies' information
    private Map<String, License> licenseDictionary = new Hashtable<String, License>();

    public DependencyListView(final String title, final List<License> licenses) {
        super("DependencyListView.ftl");
        this.title = title;
        setLicenses(licenses);
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
            license = DataModelFactory.createLicense("#" + licenseId + "#", "not identified yet", "not identified yet", "not identified yet", "not identified yet" );
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

        if(showSourceName){
            headers.add(SOURCE_FIELD);
        }

        if(showSourceVersion){
            headers.add(SOURCE_VERSION_FIELD);
        }

        if(showTarget){
            headers.add(TARGET_FIELD);
        }

        if(showTargetDownloadUrl){
            headers.add(DOWNLOAD_URL_FIELD);
        }

        if(showSize){
            headers.add(SIZE_FIELD);
        }

        if(showScope){
            headers.add(SCOPE_FIELD);
        }

        if(showLicense){
            headers.add(LICENSE_FIELD);
        }

        if(showLicenseLongName){
            headers.add(LICENSE_LONG_NAME__FIELD);
        }

        if(showLicenseUrl){
            headers.add(LICENSE_URL_FIELD);
        }

        if(showLicenseComment){
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

        if(showSourceName){
            cells.add(dependency.getSourceName());
        }

        if(showSourceVersion){
            cells.add(dependency.getSourceVersion());
        }

        if(showTarget){
            cells.add(dependency.getTarget().getGavc());
        }

        if(showTargetDownloadUrl){
            cells.add(dependency.getTarget().getDownloadUrl());
        }

        if(showSize){
            cells.add(dependency.getTarget().getSize());
        }

        if(showScope){
            cells.add(dependency.getScope().name());
        }

        if(showLicense){
            cells.add(license.getName());
        }

        if(showLicenseLongName){
            cells.add(license.getLongName());
        }

        if(showLicenseUrl){
            cells.add(license.getUrl());
        }

        if(showLicenseComment){
            cells.add(license.getComments());
        }

        return cells.toArray(new String[cells.size()]);
    }

    public void setShowSourceName(boolean showSourceName) {
        this.showSourceName = showSourceName;
    }

    public void setShowSourceVersion(boolean showSourceVersion) {
        this.showSourceVersion = showSourceVersion;
    }

    public void setShowTarget(boolean showTarget) {
        this.showTarget = showTarget;
    }

    public void setShowTargetDownloadUrl(boolean showTargetDownloadUrl) {
        this.showTargetDownloadUrl = showTargetDownloadUrl;
    }

    public void setShowSize(boolean showSize) {
        this.showSize = showSize;
    }

    public void setShowScope(boolean showScope) {
        this.showScope = showScope;
    }

    public void setShowLicense(boolean showLicense) {
        this.showLicense = showLicense;
    }

    public void setShowLicenseLongName(boolean showLicenseLongName) {
        this.showLicenseLongName = showLicenseLongName;
    }

    public void setShowLicenseUrl(boolean showLicenseUrl) {
        this.showLicenseUrl = showLicenseUrl;
    }

    public void setShowLicenseComment(boolean showLicenseComment) {
        this.showLicenseComment = showLicenseComment;
    }

    private void setLicenses(final List<License> licenses) {
        licenseDictionary.clear();
        for(License license: licenses){
            licenseDictionary.put(license.getName(), license);
        }
    }
}
