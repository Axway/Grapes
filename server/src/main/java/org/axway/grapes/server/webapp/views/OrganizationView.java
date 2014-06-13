package org.axway.grapes.server.webapp.views;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yammer.dropwizard.views.View;
import org.axway.grapes.commons.datamodel.Organization;
import org.axway.grapes.server.webapp.views.serialization.OrganizationSerializer;

@JsonSerialize(using= OrganizationSerializer.class)
public class OrganizationView extends View {

    final Organization organization;

    public OrganizationView(final Organization organization) {
        super("OrganizationView.ftl");
        this.organization = organization;
    }

    public Organization getOrganization(){
        return organization;
    }


}
