package org.axway.grapes.server.webapp.views;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yammer.dropwizard.views.View;
import org.axway.grapes.commons.datamodel.License;
import org.axway.grapes.server.webapp.views.serialization.LicenseLisSerializer;
import org.axway.grapes.server.webapp.views.utils.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * License List View
 *
 * <p>View that can either be displayed in HTML or serialized in JSON that contains a list of license</p>
 *
 * @author jdcoffre
 */
@JsonSerialize(using=LicenseLisSerializer.class)
public class LicenseListView extends View {


    private final String title;

    private final List<License> licenses = new ArrayList<License>();


    public LicenseListView(final String title) {
        super("DependencyListView.ftl");
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void add(final License license) {
        if(!licenses.contains(license)){
            licenses.add(license);
        }
    }

    public void addAll(final List<License> licenses) {
        for(final License license: licenses){
            add(license);
        }
    }

    public List<License> getLicenses() {
        return licenses;
    }

    /**
     * Generate a table that contains the dependencies information with the column that match the configured filters
     *
     * @return Table
     */
    public Table getTable(){
        final Table table = new Table("Name", "Long Name", "URL", "Comment");

        // Create row(s) per dependency
        for(final License license: licenses){
            table.addRow(license.getName(), license.getLongName(), license.getUrl(), license.getComments());
        }

        return table;
    }
}
