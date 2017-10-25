package org.axway.grapes.server.webapp.resources;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.testing.ResourceTest;
import com.yammer.dropwizard.views.ViewMessageBodyWriter;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.config.PromoValidationConfig;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.axway.grapes.server.promo.validations.PromotionValidation;
import org.axway.grapes.server.webapp.auth.GrapesAuthenticator;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SnapshotExclusionTest extends ResourceTest {

    private RepositoryHandler repositoryHandler;
    private GrapesServerConfig config;

    private static final DbModule SNAPSHOT_VERSION_MODULE = new DbModule();
    private static String PROMOTION_REPORT_ENDPOINT;

    @BeforeClass
    public static void setupModule() {
        final String version = "1.2.3-SNAPSHOT";
        final String name = "a-module";
        SNAPSHOT_VERSION_MODULE.setName(name);
        SNAPSHOT_VERSION_MODULE.setVersion(version);

        PROMOTION_REPORT_ENDPOINT = String.format("/%s/%s/%s%s%s",
                ServerAPI.MODULE_RESOURCE,
                name,
                version,
                ServerAPI.PROMOTION,
                ServerAPI.GET_REPORT);
        System.out.println(PROMOTION_REPORT_ENDPOINT);
    }

    @Override
    protected void setUpResources() throws Exception {
        repositoryHandler = GrapesTestUtils.getRepoHandlerMock();
        when(repositoryHandler.getModule(SNAPSHOT_VERSION_MODULE.getId())).thenReturn(SNAPSHOT_VERSION_MODULE);

        config = mock(GrapesServerConfig.class);
        PromoValidationConfig cfgMock = mock(PromoValidationConfig.class);

        when(config.getPromoValidationCfg()).thenReturn(cfgMock);
        when(cfgMock.getErrors()).thenReturn(Collections.emptyList());

        final ModuleResource resource = new ModuleResource(repositoryHandler, config);
        addProvider(new BasicAuthProvider<>(new GrapesAuthenticator(repositoryHandler), "test auth"));
        addProvider(ViewMessageBodyWriter.class);
        addResource(resource);
    }

    @Test
    public void fullValidationsNotPromotable() {
        //
        // Given:
        //   VERSION_IS_SNAPSHOT is configured as error
        //   module version is 1.2.3-SNAPSHOT
        // When
        //   requesting full release validation report
        // Then
        //   the promotable field should be false
        //   the message contains the "is SNAPSHOT" indication
        //
        withErrors(PromotionValidation.VERSION_IS_SNAPSHOT);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        final WebResource resource = client().resource(PROMOTION_REPORT_ENDPOINT);
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);

        final PromotionEvaluationReport report = response.getEntity(PromotionEvaluationReport.class);
        assertFalse(report.isPromotable());
        assertEquals(1, report.getMessages().stream().filter(msg -> msg.getBody().contains("is SNAPSHOT")).count());
    }

    @Test
    public void excludingSnapshotIsPromotable() {
        withErrors(PromotionValidation.VERSION_IS_SNAPSHOT);
        //
        // Given:
        //   VERSION_IS_SNAPSHOT is configured as error
        //   module version is 1.2.3-SNAPSHOT
        // When
        //   requesting validation by excluding the SNAPSHOT condition
        // Then
        //   the promotable field should be true
        //
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        final WebResource resource = client().resource(PROMOTION_REPORT_ENDPOINT + "?" + ServerAPI.EXCLUDE_SNAPSHOT_PARAM + "=true");
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);

        final PromotionEvaluationReport report = response.getEntity(PromotionEvaluationReport.class);
        assertTrue(report.isPromotable());
        assertTrue(report.getMessages().isEmpty());
    }

    private void withErrors(PromotionValidation... validations) {
        final PromoValidationConfig promoValidationMock = mock(PromoValidationConfig.class);
        final List<String> errors = Arrays.stream(validations).map(PromotionValidation::name).collect(Collectors.toList());
        when(promoValidationMock.getErrors()).thenReturn(errors);

        when(config.getPromoValidationCfg()).thenReturn(promoValidationMock);
        PromotionReportTranslator.setConfig(promoValidationMock);
    }

}
