package org.axway.grapes.server.db.datamodel;

/**
 * DB Collections
 *
 * <p>This interface contains all the collection names that could be found in Grapes database.</p>
 *
 * author: jdcoffre
 */
public interface DbCollections {

    static final String datamodelVersion = "2.2.0";

    public static final String DB_ORGANIZATION = DbOrganization.class.getSimpleName();
    public static final String DB_PRODUCT = DbProduct.class.getSimpleName();
    public static final String DB_MODULES = DbModule.class.getSimpleName();
    public static final String DB_ARTIFACTS = DbArtifact.class.getSimpleName();
    public static final String DB_LICENSES = DbLicense.class.getSimpleName();
    public static final String DB_CREDENTIALS = DbCredential.class.getSimpleName();
    public static final String DB_GRAPES_INFO = DbGrapesInfo.class.getSimpleName();

    public static final String DEFAULT_ID = "_id";

}
