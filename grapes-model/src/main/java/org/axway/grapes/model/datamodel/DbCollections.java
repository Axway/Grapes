package org.axway.grapes.model.datamodel;

/**
 * DB Collections
 * <p>
 * <p>This interface contains all the collection names that could be found in Grapes database.</p>
 * <p>
 * author: jdcoffre
 */
public interface DbCollections {

    static final String datamodelVersion = "2.2.0";

    public static final String DB_ORGANIZATION = Organization.class.getSimpleName();
    public static final String DB_PRODUCT = Product.class.getSimpleName();
    public static final String DB_MODULES = Module.class.getSimpleName();
    public static final String DB_ARTIFACTS = Artifact.class.getSimpleName();
    public static final String DB_LICENSES = License.class.getSimpleName();
    public static final String DB_CREDENTIALS = Credential.class.getSimpleName();
    public static final String DB_GRAPES_INFO = GrapesInfo.class.getSimpleName();

    public static final String DEFAULT_ID = "_id";

}
