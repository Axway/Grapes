package org.axway.grapes.server.webapp.resources;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.testing.ResourceTest;
import com.yammer.dropwizard.views.ViewMessageBodyWriter;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.License;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.core.CacheUtils;
import org.axway.grapes.server.core.cache.CacheName;
import org.axway.grapes.server.db.ModelMapper;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCredential;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.util.InjectionUtils;
import org.axway.grapes.server.webapp.auth.GrapesAuthenticator;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ArtifactResourceTest2 {

    private RepositoryHandler repositoryHandler;
    private ModelMapper mapper;
    private ArtifactResource sut = null;

    @Before
    public void setup() {
        repositoryHandler = GrapesTestUtils.getRepoHandlerMock();
        mapper = new ModelMapper(repositoryHandler);
        sut = new ArtifactResource(repositoryHandler, GrapesTestUtils.getGrapesConfig());
    }

    @Test
    public void testAddLicenseClearsPromotionReportsCache() throws NoSuchFieldException, IllegalAccessException {
        final String aLicenseString = "ASF 2";
        final Artifact artifact = DataModelFactory.createArtifact(GrapesTestUtils.CORPORATE_GROUPID_4TEST, "artifactId", "version", "classifier", "type", "extension");
        artifact.addLicense(aLicenseString);
        withArtifact(repositoryHandler, artifact);

        CacheUtils cacheMock = mock(CacheUtils.class);
        InjectionUtils.injectField(sut, ArtifactResource.class, "cacheUtils", cacheMock);

        sut.deleteLicense(withRoles(DbCredential.AvailableRoles.DATA_UPDATER), artifact.getGavc(), aLicenseString);

        verify(cacheMock, times(1)).clear(eq(CacheName.PROMOTION_REPORTS));
    }


    @Test
    public void testRemoveLicenseClearsPromotionReportsCache() throws NoSuchFieldException, IllegalAccessException {
        final String licId = "ASF 2.0";

        final Artifact artifact = DataModelFactory.createArtifact(GrapesTestUtils.CORPORATE_GROUPID_4TEST, "artifactId", "version", "classifier", "type", "extension");
        withArtifact(repositoryHandler, artifact);
        withLicense(repositoryHandler, licId);
        CacheUtils cacheMock = mock(CacheUtils.class);
        InjectionUtils.injectField(sut, ArtifactResource.class, "cacheUtils", cacheMock);

        sut.addLicense(withRoles(DbCredential.AvailableRoles.DATA_UPDATER),
                       artifact.getGavc(),
                       licId);

        verify(cacheMock, times(1)).clear(eq(CacheName.PROMOTION_REPORTS));
    }


    private void withArtifact(final RepositoryHandler repoHandler, final Artifact a) {
        when(repoHandler.getArtifact(eq(a.getGavc()))).thenReturn(mapper.getDbArtifact(a));
    }

    private void withLicense(final RepositoryHandler repoHandler, final String licId) {
        License license = DataModelFactory.createLicense(licId, licId, "", "", "");
        when(repoHandler.getLicense(eq(licId))).thenReturn(mapper.getDbLicense(license));
    }

    private DbCredential withRoles(DbCredential.AvailableRoles... roles) {
        DbCredential result = new DbCredential();
        for(DbCredential.AvailableRoles role : roles) {
            result.addRole(role);
        }

        return result;
    }

}
