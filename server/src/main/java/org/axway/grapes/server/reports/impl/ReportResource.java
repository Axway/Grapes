package org.axway.grapes.server.reports.impl;

import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.webapp.resources.AbstractResource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * This class represents the entry point for serving requests related to the reporting engine
 */
@Path(ServerAPI.GET_REPORTS)
public class ReportResource extends AbstractResource {

    static {
        ReportsRegistry.init();
    }

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
    public ReportExecution execute(ReportRequest req) {

        if(req == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Report execution payload expected")
                    .build());
        }

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
    }
}
