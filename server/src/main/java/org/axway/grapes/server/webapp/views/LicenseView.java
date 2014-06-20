package org.axway.grapes.server.webapp.views;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yammer.dropwizard.views.View;
import org.axway.grapes.commons.datamodel.License;
import org.axway.grapes.server.webapp.views.serialization.LicenseSerializer;

@JsonSerialize(using=LicenseSerializer.class)
public class LicenseView  extends View{

    public LicenseView() {
        super("LicenseView.ftl");
    }

    private License license;

    public void setLicense(final License license) {
        this.license = license;

    }

    public License getLicense() {
        return license;
    }

}
