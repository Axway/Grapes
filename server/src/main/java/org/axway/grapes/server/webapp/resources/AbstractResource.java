package org.axway.grapes.server.webapp.resources;

import com.yammer.dropwizard.views.View;
import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.commons.utils.JsonUtils;
import org.axway.grapes.server.config.CommunityConfig;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.*;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.ModelMapper;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbSearch;
import org.axway.grapes.server.reports.Report;
import org.axway.grapes.server.reports.ReportUtils;
import org.axway.grapes.server.reports.ReportsHandler;
import org.axway.grapes.server.reports.ReportsRegistry;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.axway.grapes.commons.datamodel.Tag.*;

/**
 * Abstract resource
 * 
 * <p>Provide documentation and repository management support. This class should be extended by all the server resources.</p>
 * 
 * @author jdcoffre
 */
public abstract class AbstractResource extends View {

    public static final String TWO_PLACES = "%s %s";
    private final RepositoryHandler repositoryHandler;
    private final GrapesServerConfig grapesConfig;

    private final ModelMapper modelMapper;
    
    protected AbstractResource(final RepositoryHandler repoHandler, final String templateName, final GrapesServerConfig dmConfig) {
		super(templateName);
        this.grapesConfig = dmConfig;
        this.repositoryHandler = repoHandler;
        this.modelMapper = new ModelMapper(repoHandler);
	}
    
    /**
	 * Provide the documentation of the LicenseResource when the server got a request GET <dm_url>/<resourceName>
	 * 
	 * @return Response The documentation in HTML
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getDocumentation(){
		return Response.ok(this).build();
	}

    /**
     * Return a OrganizationHandler
     *
     * @return OrganizationHandler
     */
    protected OrganizationHandler getOrganizationHandler(){
        return new OrganizationHandler(repositoryHandler);
    }

    /**
     * Return a ProductHandler
     *
     * @return ProductHandler
     */
    protected ProductHandler getProductHandler(){
        return new ProductHandler(repositoryHandler);
    }

    /**
     * Return a ModuleHandler
     *
     * @return ArtifactHandler
     */
    protected ModuleHandler getModuleHandler(){
        return new ModuleHandler(repositoryHandler);
    }

    /**
     * Return an ArtifactHandler
     *
     * @return ArtifactHandler
     */
    protected ArtifactHandler getArtifactHandler(){
        return new ArtifactHandler(repositoryHandler, getLicenseHandler());
    }

    /**
     * Return a DependencyHandler
     *
     * @return DependencyHandler
     */
    protected DependencyHandler getDependencyHandler(){
        return new DependencyHandler(repositoryHandler);
    }

    /**
     * Return a LicenseHandler
     *
     * @return LicenseHandler
     */
    protected LicenseHandler getLicenseHandler(){
        return new LicenseHandler(repositoryHandler);
    }

    /**
     * Return a GraphsHandler
     *
     * @return LicenseHandler
     */
    protected GraphsHandler getGraphsHandler(final FiltersHolder filtersHolder){
        return new GraphsHandler(repositoryHandler,filtersHolder);
    }

    protected ReportsHandler getReportsHandler() {
        return new ReportsHandler(repositoryHandler);
    }

    /**
     * Return Grapes configuration
     *
     * @return GrapesServerConfig
     */
    protected GrapesServerConfig getConfig(){
        return grapesConfig;
    }

    /**
     * Returns model mapper for data-model conversion
     *
     * @return ModelMapper
     */
    protected ModelMapper getModelMapper(){
        return modelMapper;
    }

    /**
     * Return the version of the application
     * 
     * @return String
     */
    public String getProgramVersion(){
		return getClass().getPackage().getImplementationVersion();
	}


    /**
     * Return the issue-tracker url configured in the server configuration file (null if empty)
     *
     * @return String
     */
    public String getIssueTrackerUrl(){
        final CommunityConfig communityConfig = getConfig().getCommunityConfiguration();
        if(communityConfig == null){
            return null;
        }
        return communityConfig.getIssueTracker();
    }

    /**
     * Return the online documentation url configured in the server configuration file (null if empty)
     *
     * @return String
     */
    public String getOnlineDocumentation(){
        final CommunityConfig communityConfig = getConfig().getCommunityConfiguration();
        if(communityConfig == null){
            return null;
        }
        return communityConfig.getOnlineHelp();
    }

    /**
     * Returns an empty model of a Organization in Json
     *
     * @return String
     * @throws IOException
     */
    public String getOrganizationJsonModel() throws IOException {
        return JsonUtils.serialize(DataModelFactory.createOrganization(""));
    }

    /**
     * Returns an empty model of a Module in Json
     *
     * @return String
     * @throws IOException
     */
    public String getModuleJsonModel() throws IOException {
        return JsonUtils.serialize(DataModelFactory.createModule("", ""));
    }

    /**
     * Returns an empty model of an Artifact in Json
     *
     * @return String
     * @throws IOException
     */
    public String getArtifactJsonModel() throws IOException {
        return JsonUtils.serialize(DataModelFactory.createArtifact("", "", "", "", "", ""));
    }

    /**
     * Returns an empty model of a Message to check Promotion status 
     *
     * @return String
     * @throws IOException
     */
    public String getArtifactPromtotionResponseMessage() throws IOException {
        return JsonUtils.serialize(DataModelFactory.createArtifactPromotionStatus(false, ""));
    }
    
    /**
     * Returns an empty model of a Dependency in Json
     *
     * @return String
     * @throws IOException
     */
    public String getDependencyJsonModel() throws IOException {
        final Artifact artifact = DataModelFactory.createArtifact("","","","","","","");
        return JsonUtils.serialize(DataModelFactory.createDependency(artifact, Scope.COMPILE));
    }

    /**
     * Returns an empty model of a License in Json
     *
     * @return String
     * @throws IOException
     */
    public String getLicenseJsonModel() throws IOException {
        return JsonUtils.serialize(DataModelFactory.createLicense("Apache 2",
                "The Apache Software License, Version 2.0",
                "",
                "(((.*)(Apache|apache|asf)(.*)(2)(.*))|(.*)(apache license|apache|Software Licenses))",
                "http://www.opensource.org/licenses/apache2.0.php"));
    }
    /**
     * Returns an empty Promotion details in Json
     *
     * @return String
     * @throws IOException
     */
    public String getPromotionDetailsJsonModel() throws IOException {
        final PromotionEvaluationReport sampleReport = new PromotionEvaluationReport();
        sampleReport.addMessage(String.format(TWO_PLACES, PromotionReportTranslator.UNPROMOTED_MSG, "com.acme.secure-smh:core-relay:1.2.0"), MAJOR);
        sampleReport.addMessage(String.format(TWO_PLACES, PromotionReportTranslator.DO_NOT_USE_MSG, "com.google.guava:guava:20.0"), MAJOR);
        sampleReport.addMessage(String.format(TWO_PLACES, PromotionReportTranslator.MISSING_LICENSE_MSG, "org.apache.maven.wagon:wagon-webdav-jackrabbit:2.12"), MINOR);
        sampleReport.addMessage(String.format(TWO_PLACES, PromotionReportTranslator.UNACCEPTABLE_LICENSE_MSG,
                "aopaliance:aopaliance:1.0 licensed as Attribution-ShareAlike 2.5 Generic, " +
                "org.polyjdbc:polyjdbc0.7.1 licensed as Creative Commons Attribution-ShareAlike 3.0 Unported License"),
                MINOR);

        sampleReport.addMessage(PromotionReportTranslator.SNAPSHOT_VERSION_MSG, Tag.CRITICAL);
        return JsonUtils.serialize(sampleReport);
    }

    /**
     * Returns an empty Search object in Json
     * @return String
     * @throws IOException
     */
    public String getSearchJsonModel() throws IOException {
        DbSearch search = new DbSearch();
        search.setArtifacts(new ArrayList<>());
        search.setModules(new ArrayList<>());
        return JsonUtils.serialize(search);
    }

    /**
     * Returns the comma separated list of available scopes
     *
     * @return String
     */
     public String getScopes() {
        final StringBuilder sb = new StringBuilder();
        for (final Scope scope : Scope.values()) {
            sb.append(scope);
            sb.append(", ");
        }
        final String scopes = sb.toString().trim();
        return scopes.substring(0, scopes.length() - 1);
    }
     /**
      * Returns an empty Delivery details in Json
      *
      * @return String
      * @throws IOException
      */
     public String getDeliveryJsonModel() throws IOException {
         return JsonUtils.serialize(DataModelFactory.createDelivery("", "", "", new ArrayList<String>()));
     }

    /**
     * Displays a sample model for the report request.
     * @return A string describing the structure of a certain report execution
     */
     public String[] getReportSamples() {
         final Map<String, String> sampleValues = new HashMap<>();
         sampleValues.put("name1", "Secure Transpiler Mars");
         sampleValues.put("version1", "4.7.0");
         sampleValues.put("name2", "Secure Transpiler Bounty");
         sampleValues.put("version2", "5.0.0");
         sampleValues.put("license", "CDDL-1.1");
         sampleValues.put("name", "Secure Pretender");
         sampleValues.put("version", "2.7.0");
         sampleValues.put("organization", "Axway");

         return ReportsRegistry.allReports()
                 .stream()
                 .map(report -> ReportUtils.generateSampleRequest(report, sampleValues))
                 .map(request -> {
                     try {
                         String desc = "";
                         final Optional<Report> byId = ReportsRegistry.findById(request.getReportId());

                         if(byId.isPresent()) {
                            desc = byId.get().getDescription() + "<br/><br/>";
                         }

                         return String.format(TWO_PLACES, desc, JsonUtils.serialize(request));
                     } catch(IOException e) {
                         return "Error " + e.getMessage();
                     }
                 })
                 .collect(Collectors.toList())
                 .toArray(new String[] {});
     }

     public int getAvailableReportsCount() {
        return ReportsRegistry.allReports().size();
     }

    /**
     * Returns a list of Artifact Validation types
     *
     * @return List<String>
     */
    public List<String> externalValidatedTypes() {
        return getConfig().getExternalValidatedTypes();
    }

    /**
     * Get comment handler class
     *
     * @return CommentHandler class
     */
    protected CommentHandler getCommentHandler(){
        return new CommentHandler(repositoryHandler);
    }

    /**
     * Get search handler class
     *
     * @return SearchHandler class
     */
    protected SearchHandler getSearchHandler() { return new SearchHandler(repositoryHandler);}
}
