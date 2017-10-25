package org.axway.grapes.commons.api;

/**
 * <b>Grapes Server Resources Interface</b>
 *
 * <p>This interface gathers the resources available on a Grapes server.</p>
 *
 * <p><b>Warning:</b> Make sure that the version of grapes-commons you use is compatible with the targeted Grapes server</p>
 *
 * @author jdcoffre
 */
public class ServerAPI {

    private ServerAPI() {}
    

    //RESSOURCES
    /** Value - {@value}, Organization is a Grapes server resource that handles the information about organization.*/
    public static final String ORGANIZATION_RESOURCE = "organization";

    /** Value - {@value}, Product is a Grapes server resource that handles the information about products & deliveries.*/
    public static final String PRODUCT_RESOURCE = "product";

    /** Value - {@value}, Module is a Grapes server resource that handles the information about modules.*/
    public static final String MODULE_RESOURCE = "module";

    /** Value - {@value}, Artifact is a Grapes server resource that handles the information about artifacts.*/
    public static final String ARTIFACT_RESOURCE = "artifact";

    /** Value - {@value}, License is a Grapes server resource that handles the information about license.*/
    public static final String LICENSE_RESOURCE = "license";

    /** Value - {@value}, Report is a custom Grapes server report allowing dissemination of the stored information.*/
    public static final String REPORT_RESOURCE = "report";

    /** Value - {@value}, Sequoia is a Grapes server provides graphs.*/
    public static final String SEQUOIA_RESOURCE = "sequoia";

    /** Value - {@value}, Webapp is an UI that is over Grapes server server REST API.*/
    public static final String WEBAPP_RESOURCE = "webapp";

    /** Value = {@value}, Search is a Grapes server resource for searching modules and artifacts*/
    public static final String SEARCH_RESOURCE = "search";

    /** Value = {@value}, Search is a Grapes server resource for searching modules and artifacts*/
    public static final String SEARCH_DOC_RESOURCE = "searchdoc";

    //METHODS
    /** Value - {@value}, GET methods that returns the deliveries of a product. Usually used as /product/{name}/<GET_DELIVERIES>.*/
    public static final String GET_DELIVERIES = "/deliveries";

    /** Value - {@value}, GET methods that returns the module of an artifact. Usually used as /artifact/{gavc}/<GET_MODULE>.*/
    public static final String GET_MODULE = "/module";

    /** Value - {@value}, GET methods that returns the modules of a product. Usually used as /product/{name}/<GET_MODULES>.*/
    public static final String GET_MODULES = "/modules";

    /** Value - {@value}, GET methods that returns the list of corporate groupIds. Usually used as /organization/{name}/<GET_CORPORATE_GROUPIDS>.*/
    public static final String GET_CORPORATE_GROUPIDS = "/corporateGroupIds";

    /** Value - {@value}, GET methods that returns the names of the targeted resources. Usually used as /resourceName<GET_NAMES>.*/
    public static final String GET_NAMES = "/names";

    /** Value - {@value}, GET methods that returns the gavcs of the targeted artifacts. Used as /artifact<GET_GAVCS>.*/
    public static final String GET_GAVCS = "/gavcs";

    /** Value - {@value}, GET methods that returns the groupids of the targeted artifacts. Used as /artifact<GET_GAVCS>.*/
    public static final String GET_GROUPIDS = "/groupIds";

    /** Value - {@value}, GET methods that returns the versions of the targeted resource. Usually used as /resourceName/{resourceNameID}<GET_VERSIONS>.*/
    public static final String GET_VERSIONS = "/versions";

    /** Value - {@value}, GET methods that returns the last version of the targeted resource. Usually used as /resourceName/{resourceNameID}<GET_LAST_VERSION>.*/
    public static final String GET_LAST_VERSION = "/lastversion";

    /** Value - {@value}, GET methods that returns the versions of the targeted resource. Usually used as /resourceName/{resourceNameID}<GET_DOWNLOAD_URL>.*/
    public static final String GET_DOWNLOAD_URL = "/downloadurl";

    /** Value - {@value}, GET methods that returns the versions of the targeted resource. Usually used as /resourceName/{resourceNameID}<GET_PROVIDER>.*/
    public static final String GET_PROVIDER = "/provider";

    /** Value - {@value}, POST methods used as /module/{name}/{version}<POST_PROMOTION>.*/
    public static final String PROMOTION = "/promotion";

    /** Value - {@value}, POST methods used to know if the promotion can be done.*/
    public static final String GET_FEASIBLE = "/doable";

    /** Value - {@value}, GET methods that returns TRUE if the targeted module is promoted. Used as /module/{name}/{version}<GET_BUILD_INFO>.*/
    public static final String GET_BUILD_INFO = "/buildInfo";

    /** Value - {@value}, GET methods that returns the ancestor of the targeted resource. Usually used as /resourceName/{resourceUID}<GET_ANCESTORS>.*/
    public static final String GET_ANCESTORS = "/ancestors";

    /** Value - {@value}, GET methods that returns the dependencies of the targeted resource. Usually used as /resourceName/{resourceUID}<GET_DEPENDENCIES>.*/
    public static final String GET_DEPENDENCIES = "/dependencies";

    /** Value - {@value}, GET methods a report of regarding the promotion or the dependencies. Usually used as /resourceName/{resourceUID}<GET_DEPENDENCIES><GET_DEPENDENCIES_REPORT>.*/
    public static final String GET_REPORT = "/report";
    
    /** Value - {@value}, GET methods that returns the licenses of the targeted resource. Usually used as /resourceName/{resourceUID}<GET_LICENSES>.*/
    public static final String GET_LICENSES = "/licenses";

    /** Value - {@value}, GET methods that returns the organization of an Artifact or a Module.*/
    public static final String GET_ORGANIZATION = "/organization";

    /** Value - {@value}, GET methods that returns the list of available reports.*/
    public static final String GET_REPORTS = "/reports";

    /** Value - {@value}, POST/GET flag "DO_NOT_USE" for the targeted artifact. Usually used as /<ARTIFACT_RESOURCE>/{gavc}/<SET_DO_NOT_USE>?<DO_NOT_USE>=<Boolean></></>.*/
    public static final String SET_DO_NOT_USE = "/donotuse";

    /** Value - {@value}, GET methods that retrieve all the elements of a resource type. Usually used as /<RESOURCE><GET_ALL>.*/
    public static final String GET_ALL = "/all";

    //QUERY PARAMETERS
    /** Value - {@value}, boolean query parameter that is used to get the result of the request going till the end of the dependency depth. Override depth parameter if exist.*/
    public static final String RECURSIVE_PARAM = "fullRecursive";

    /** Value - {@value}, integer query parameter that is used to set a depth into the dependency result.*/
    public static final String DEPTH_PARAM = "depth";

    /** Value - {@value}, boolean query parameter used for licenses.*/
    public static final String APPROVED_PARAM = "approved";

    /** Value - {@value}, string query parameter used for licenses.*/
    public static final String LICENSE_ID_PARAM = "licenseId";

    /** Value - {@value}, boolean query parameter used to filter modules or artifacts.*/
    public static final String PROMOTED_PARAM = "promoted";

    /** Value - {@value}, boolean query parameter used to filter modules.*/
    public static final String ORGANIZATION_PARAM = "organization";

    /** Value - {@value}, boolean query parameter used to filter dependencies.*/
    public static final String SCOPE_COMPILE_PARAM = "scopeComp";

    /** Value - {@value}, boolean query parameter used to filter dependencies.*/
    public static final String SCOPE_RUNTIME_PARAM = "scopeRun";

    /** Value - {@value}, boolean query parameter used to filter dependencies.*/
    public static final String SCOPE_PROVIDED_PARAM = "scopePro";

    /** Value - {@value}, boolean query parameter used to filter dependencies.*/
    public static final String SCOPE_TEST_PARAM = "scopeTest";

    /** Value - {@value}, boolean query parameter used to filter dependencies.*/
    public static final String TO_UPDATE_PARAM = "toUpdate";

    /** Value - {@value}, boolean query parameter used to filter artifacts.*/
    public static final String HAS_LICENSE_PARAM = "hasLicense";

    /** Value - {@value}, boolean query parameter used to filter licenses that has to be validated.*/
    public static final String TO_BE_VALIDATED_PARAM = "toBeValidated";

    /** Value - {@value}, boolean query parameter used to showCorporate Dependencies.*/
    public static final String SHOW_CORPORATE_PARAM = "showCorporate";

    /** Value - {@value}, boolean query parameter used to show the licenses in reports.*/
    public static final String SHOW_LICENSE_PARAM = "showLicenses";

    /** Value - {@value}, boolean query parameter used to show the license comments in reports.*/
    public static final String SHOW_LICENSE_COMMENT_PARAM = "showLicenseComments";

    /** Value - {@value}, boolean query parameter used to show the license urls in reports.*/
    public static final String SHOW_LICENSE_URL_PARAM = "showLicenseUrls";

    /** Value - {@value}, boolean query parameter used to show the license long names in reports.*/
    public static final String SHOW_LICENSE_FULL_NAME_PARAM = "showLicenseFullNames";

    /** Value - {@value}, boolean query parameter used to exclude the VERSION_IS_SNAPSHOT validation from the promotion report.*/
    public static final String EXCLUDE_SNAPSHOT_PARAM = "excludeSNAPSHOT";

    /** Value - {@value}, boolean query parameter used to show the sizes in reports.*/
    public static final String SHOW_SIZE = "showSize";

    /** Value - {@value}, boolean query parameter used to show the target GAVC in reports.*/
    public static final String SHOW_TARGET_PARAM = "showTargets";

    /** Value - {@value}, boolean query parameter used to show the download URLs in reports.*/
    public static final String SHOW_TARGET_URL_PARAM = "showTargetUrls";

    /** Value - {@value}, boolean query parameter used to show the scopes in reports.*/
    public static final String SHOW_SCOPE_PARAM = "showScopes";

    /** Value - {@value}, boolean query parameter used to show the ancestors in reports.*/
    public static final String SHOW_ANCESTOR_PARAM = "showAncestors";

    /** Value - {@value}, boolean query parameter used to show the ancestors in reports.*/
    public static final String SHOW_THIRPARTY_PARAM = "showThirdparty";

    /** Value - {@value}, boolean query parameter used to show dependency sources in reports.*/
    public static final String SHOW_SOURCES_PARAM = "showSources";

    /** Value - {@value}, boolean query parameter used to show dependency sources version in reports.*/
    public static final String SHOW_SOURCES_VERSION_PARAM = "showSourceVersions";

    /** Value - {@value}, boolean query parameter used to show artifacts provider in reports.*/
    public static final String SHOW_PROVIDERS_PARAM = "showProviders";

    /** Value - {@value}, String query parameter used to filter modules.*/
    public static final String NAME_PARAM = "name";

    /** Value - {@value}, String query parameter used to filter artifacts.*/
    public static final String GROUPID_PARAM = "groupId";

    /** Value - {@value}, String query parameter used to filter artifacts.*/
    public static final String ARTIFACTID_PARAM = "artifactId";

    /** Value - {@value}, String query parameter used to filter artifacts.*/
    public static final String VERSION_PARAM = "version";

    /** Value - {@value}, String query parameter used to filter artifacts.*/
    public static final String TYPE_PARAM = "type";

    /** Value - {@value}, String query parameter used to filter artifacts.*/
    public static final String CLASSIFIER_PARAM = "classifier";

    /** Value - {@value}, String query parameter used to filter artifacts.*/
    public static final String EXTENSION_PARAM = "extension";

    /** Value - {@value}, String query parameter used to filter artifacts.*/
    public static final String ORIGIN_PARAM = "origin";

    /** Value - {@value}, String query parameter used to add credentials.*/
    public static final String USER_PARAM = "user";

    /** Value - {@value}, String query parameter used to add or remove user roles.*/
    public static final String USER_ROLE_PARAM = "role";

    /** Value - {@value}, String query parameter used to add credentials.*/
    public static final String PASSWORD_PARAM = "password";

    /** Value - {@value}, String query parameter used to filter/update the field "DO_NOT_USE" of an artifact.*/
    public static final String DO_NOT_USE = "doNotUse";

    /** Value - {@value}, String query parameter used to filter artifacts.*/
    public static final String GAVC = "gavc";

    /** Value - {@value}, String query parameter used to filter artifacts.*/
    public static final String URL_PARAM = "url";

    /** Value - {@value}, String query parameter used to filter artifacts.*/
    public static final String PROVIDER_PARAM = "provider";

    /** Value - {@value}, String query parameter used to filter modules while searching.*/
    public static final String MODULES_PARAM = "modules";

    /** Value - {@value}, String query parameter used to filter artifacts while searching.*/
    public static final String ARTIFACTS_PARAM = "artifacts";
}