package org.axway.grapes.server.db.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.axway.grapes.commons.datamodel.Scope;
import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * Database Module
 * 
 * <p>Class that holds the representation of modules stored in the database. 
 * Uid composed of the name and the version of the module is used as an ID. A database index has been created on it.</p>
 * @author jdcoffre
 *
 */
public class DbModule {

    public static final String DATA_MODEL_VERSION = "data_model_version";
    private String datamodelVersion = "1.0.0";

    @ObjectId
    private String _id;

	public static final String UID_DB_FIELD = "uid"; 
	private String uid = "";

	public static final String NAME_DB_FIELD = "name"; 
	private String name = "";

	public static final String VERSION_DB_FIELD = "version"; 
	private String version = "";

	public static final String PROMOTION_DB_FIELD = "promoted"; 
	private boolean promoted = false;

	public static final String IS_SUBMODULE_DB_FIELD = "isSubmodule"; 
	private boolean isSubmodule = false;
	
	public static final String ARTIFACTS_DB_FIELD = "artifacts"; 
	private List<String> artifacts = new ArrayList<String>();
	
	public static final String SUBMODULES_DB_FIELD = "submodules"; 
	private List<DbModule> submodules = new ArrayList<DbModule>();

	public static final String DEPENDENCIES_DB_FIELD = "dependencies"; 
	private List<DbDependency> dependencies = new ArrayList<DbDependency>();

    public void setDataModelVersion(final String newVersion){
        this.datamodelVersion = newVersion;
    }

    public String getDataModelVersion(){
        return datamodelVersion;
    }

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
		updateUid();
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(final String version) {
		this.version = version;
		updateUid();
	}

	public Boolean isPromoted() {
		return promoted;
	}

	public void setPromoted(final boolean promoted) {
		this.promoted = promoted;
	}

	public String getId() {
		return _id;
	}

	public void setId(final String id) {
		this._id = id;
		
	}

	public List<String> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(final List<String> artifacts2) {
		this.artifacts = artifacts2;
	}

	public void addArtifact(final DbArtifact artifact) {
		final String artifactGavc = artifact.getGavc();
		
		if(!artifacts.contains(artifactGavc)){
			artifacts.add(artifactGavc);
		}
	}	

	public void flushArtifacts() {
		artifacts.clear();
	}

	public List<DbModule> getSubmodules() {
		return submodules;
	}

	public void setSubmodules(final List<DbModule> submodules) {
		this.submodules = submodules;
	}

	public void addSubmodule(final DbModule submodule) {
		submodules.add(submodule);
	}	

	public void flushSubmodules() {
		submodules.clear();
	}

	public void addArtifacts(final List<DbArtifact> artifacts) {
		for(DbArtifact artifact: artifacts){
			addArtifact(artifact);
		}
	}	
	
	public final void updateUid(){		
		uid = generateUID(name, version);
	}

	public String getUid() {
		return uid;
	}
	
	public boolean isSubmodule() {
		return isSubmodule;
	}

	public void setSubmodule(final boolean isSubmodule) {
		this.isSubmodule = isSubmodule;
	}

	public List<DbDependency> getDependencies() {
		return dependencies;
	}

    public void setDependencies(final List<DbDependency> dependencies) {
        this.dependencies = dependencies;
    }

    public void addDependencies(final List<DbDependency> dependencies) {
        this.dependencies.addAll(dependencies);
    }

    public void addDependency(final String artifactGavc,final Scope scope) {
        final DbDependency dependency = new DbDependency(this.getUid(), artifactGavc, scope);
        this.dependencies.add(dependency);
    }

	public void flushDependencies() {
		dependencies.clear();
	}
	
	@Override
	public String toString(){
		final StringBuilder sb = new StringBuilder();

		sb.append("Name: ");
		sb.append(name);
		sb.append(", Version: ");
		sb.append(version);
		
		return sb.toString();
	}

	public static String generateUID(final String moduleName, final String moduleVersion) {
		final StringBuilder sb = new StringBuilder();
		sb.append(moduleName);
		sb.append(":");
		sb.append(moduleVersion);
		
		return sb.toString();
	}

    /**
     * Here is a workaround because of this mongodb open issue:
     * https://jira.mongodb.org/browse/SERVER-267
     */
    public static final String HAS_DB_FIELD = "has";
    private List<String> has = new ArrayList<String>();
    public static final String USE_DB_FIELD = "uses";
    private List<String> uses = new ArrayList<String>();

    public List<String> getHas() {
        return has;
    }

    public List<String> getUses() {
        return uses;
    }

    public void updateHasAndUse(){
        has.clear();
        uses.clear();

        for(DbModule submodule: submodules){
            submodule.updateHasAndUse();
            has.addAll(submodule.has);
            uses.addAll(submodule.uses);
        }

        has.addAll(artifacts);

        for(DbDependency dependency: dependencies){
            uses.add(dependency.getTarget());
        }
    }
}
