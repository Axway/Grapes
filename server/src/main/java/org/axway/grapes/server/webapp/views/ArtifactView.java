package org.axway.grapes.server.webapp.views;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yammer.dropwizard.views.View;
import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.Organization;
import org.axway.grapes.server.webapp.views.serialization.ArtifactSerializer;

@JsonSerialize(using=ArtifactSerializer.class)
public class ArtifactView  extends View{

    private Boolean shouldNotBeUsed = false;
    private Boolean isCorporate = false;
    private Organization organization;

    public ArtifactView() {
		super("ArtifactView.ftl");
	}

	private Artifact artifact;

	public void setArtifact(final Artifact artifact) {
		this.artifact = artifact;
		
	}

	public Artifact getArtifact() {
		return artifact;
	}

    public void setShouldNotBeUse(final Boolean shouldNotBeUsed){
        this.shouldNotBeUsed = shouldNotBeUsed;
    }

    public Boolean shouldNotBeUsed(){
        return shouldNotBeUsed;
    }

    public void setIsCorporate(final Boolean isCorporate){
        this.isCorporate = isCorporate;
    }

    public Boolean isCorporate(){
        return isCorporate;
    }

    public void setOrganization(final Organization organization) {
        this.organization = organization;
    }
}
