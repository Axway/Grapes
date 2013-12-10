package org.axway.grapes.server.webapp.views;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yammer.dropwizard.views.View;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.webapp.views.serialization.LicenseSerializer;

@JsonSerialize(using=LicenseSerializer.class)
public class LicenseView  extends View{

    public LicenseView() {
        super("LicenseView.ftl");
    }

    private DbLicense license;

    public void setLicense(final DbLicense license) {
        this.license = license;

    }

    public DbLicense getLicense() {
        return license;
    }

}
