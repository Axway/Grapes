package org.axway.grapes.server.db.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jongo.marshall.jackson.oid.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * Database Credential
 * 
 * <p>Class that represent Grapes credentials that are stored in the database.
 * Passwords have to be encrypted.</p>
 * 
 * @author jdcoffre
 */
public class DbCorporateGroupIds {

    public static final String DATA_MODEL_VERSION = "data_model_version";
    private String datamodelVersion = "1.2.0";
	
	@Id
    @JsonProperty("_id")
	private String id;
	
	public static final String CORPORATE_GROUPIDS_FIELD = "corporateGroupIds";
	private List<String> corporateGroupIds = new ArrayList<String>();
	
	public void changeDataModelVersion(final String newVersion){
        this.datamodelVersion = newVersion;
    }

    public String getDataModelVersion(){
        return datamodelVersion;
    }
	
	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

    public List<String> getCorporateGroupIds() {
        return corporateGroupIds;
    }

    public void addCorporateGroupId(final String corporateGroupId) {
        if(!corporateGroupIds.contains(corporateGroupId)){
            corporateGroupIds.add(corporateGroupId);
        }
    }

    public void removeCorporateGroupId(final String corporateGroupId) {
        corporateGroupIds.remove(corporateGroupId);
    }
}
