package org.axway.grapes.server.webapp.resources;

import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.License;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.commons.datamodel.Organization;
import org.axway.grapes.commons.utils.JsonUtils;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.mockito.Mockito.mock;

public class AbstractResourceTest {

    @Test
    public void checkModuleJsonModel(){
        final FakeResource resource = new FakeResource();
        Exception exception = null;

        try {
            final Module module = JsonUtils.unserializeModule(resource.getModuleJsonModel());
            assertNotNull(module);
        }catch (Exception e){
            exception = e;
        }

        assertNull(exception);
    }

    @Test
    public void checkOrganizationJsonModel(){
        final FakeResource resource = new FakeResource();
        Exception exception = null;

        try {
            final Organization organization = JsonUtils.unserializeOrganization(resource.getOrganizationJsonModel());
            assertNotNull(organization);
        }catch (Exception e){
            exception = e;
        }

        assertNull(exception);
    }

    @Test
    public void checkArtifactJsonModel(){
        final FakeResource resource = new FakeResource();
        Exception exception = null;

        try {
            final Artifact artifact = JsonUtils.unserializeArtifact(resource.getArtifactJsonModel());
            assertNotNull(artifact);
        }catch (Exception e){
            exception = e;
        }

        assertNull(exception);
    }

    @Test
    public void checkLicenseJsonModel(){
        final FakeResource resource = new FakeResource();
        Exception exception = null;

        try {
            final License license = JsonUtils.unserializeLicense(resource.getLicenseJsonModel());
            assertNotNull(license);
        }catch (Exception e){
            exception = e;
        }

        assertNull(exception);
    }

    @Test
    public void checkScopes(){
        final FakeResource resource = new FakeResource();
        Exception exception = null;

        try {
            assertNotNull(resource.getScopes());
        }catch (Exception e){
            exception = e;
        }

        assertNull(exception);
    }

    private class FakeResource extends AbstractResource {
        protected FakeResource() {
            super(GrapesTestUtils.getRepoHandlerMock(), GrapesTestUtils.getServiceHandlerMock(), "", mock(GrapesServerConfig.class));
        }
    }
}
