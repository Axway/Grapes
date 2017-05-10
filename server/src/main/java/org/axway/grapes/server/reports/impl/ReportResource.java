package org.axway.grapes.server.reports.impl;

import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.reports.ReportId;
import org.axway.grapes.server.webapp.resources.AbstractResource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("reports")
public class ReportResource extends AbstractResource {
    public ReportResource(RepositoryHandler repoHandler, GrapesServerConfig dmConfig) {
        super(repoHandler, "No template.ftl", dmConfig);
    }


    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/")
    public Set<Report> listReports() {
        return ReportsRegistry.allReports();
    }

    @POST
    @Produces({"text/csv", MediaType.APPLICATION_JSON})
    @Path("/execution")
    public ReportExecution executeReport(ReportRequest req) {

        //
        // Is there any such report registered
        //
        final Optional<Report> reportOp = ReportsRegistry.findById(req.getReportId());

        if(!reportOp.isPresent()) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Invalid report id")
                    .build());
        }

        final Report reportDef = reportOp.get();

        //
        // Validating the request to contain all the report expected input values
        //
        List<String> missing = new ArrayList<>();
        for(final ParameterDefinition def : reportDef.getParameters()) {
            if(!req.getParamValues().containsKey(def.getName())) {
                missing.add(def.getName());
            }
        };

        if(!missing.isEmpty()) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("Missing properties: " + missing.toString())
                            .build());
        }

        return getReportsHandler().execute(reportDef, req);

//        return reportDef.execute(this.get get req, ReportUtils.getTabularColumnNames(reportDef));
//        Playground p = new Playground(getModuleHandler().get);
//        p.printLicenseReport("Some Name", "1.5.8");

        // return getSampleData();
    }

    private ReportExecution getSampleData() {

        ReportRequest req = new ReportRequest();
        req.setReportId(ReportId.LICENSES_PER_PRODUCT_RELEASE.getId());
        Map<String, String> params = new HashMap<>();
        params.put("name", "Gateway");
        params.put("version", "2.3.5");
        req.setParamValues(params);

        ReportExecution result = new ReportExecution(req, new String[] {"License Type", "Small Desc", "Min"});

        // result.setRequest(req);
        result.addResultRow(new String[] {"L1", "Vegan", "1.2.3"});
        result.addResultRow(new String[] {"L2", "Meat", "3.99.3"});
        result.addResultRow(new String[] {"L3", "Vegetarian", "1.2.3"});
        result.addResultRow(new String[] {"L4", "Raw-Vegan", "6.20.30"});

        return result;
    }
}
