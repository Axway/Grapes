package org.axway.grapes.server.db.datamodel;

import org.bson.types.ObjectId;

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
    private String datamodelVersion = "1.0.0";

    private ObjectId _id;
	
	public static final String CORPORATE_GROUPIDS_FIELD = "corporateGroupIds";
	private List<String> corporateGroupIds = new ArrayList<String>();
	
	public void setDataModelVersion(final String newVersion){
        this.datamodelVersion = newVersion;
    }

    public String getDataModelVersion(){
        return datamodelVersion;
    }
	
	public ObjectId getId() {
		return _id;
	}

	public void setId(final ObjectId id) {
		this._id = id;
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
