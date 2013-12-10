package org.axway.grapes.server.materials.cases;

import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.db.datamodel.DbModule;

import java.util.List;

public interface DependencyCase {

	public List<DbArtifact> dbArtifactsToLoad();
    public List<DbModule> dbModulesToLoad();
    public List<DbLicense> dbLicensesToLoad();
	
}
