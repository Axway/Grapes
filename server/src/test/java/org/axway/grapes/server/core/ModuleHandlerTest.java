package org.axway.grapes.server.core;


import org.axway.grapes.commons.datamodel.Scope;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.*;

public class ModuleHandlerTest {

    @Test
    public void checkStoreModule(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ModuleHandler handler = new ModuleHandler(repositoryHandler);

        final DbModule module = new DbModule();
        handler.store(module);

        verify(repositoryHandler, times(1)).store(module);
    }

    @Test
    public void checkGetAllModuleNames(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ModuleHandler handler = new ModuleHandler(repositoryHandler);

        final FiltersHolder filters = mock(FiltersHolder.class);
        handler.getModuleNames(filters);

        verify(repositoryHandler, times(1)).getModuleNames(filters);
    }

    @Test
    public void getVersionsOfAModule(){
        final DbModule module = new DbModule();
        module.setName("module");
        module.setVersion("1.0.0-SNAPSHOT");

        final FiltersHolder filters = mock(FiltersHolder.class);

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getModuleVersions(module.getName(), filters)).thenReturn(Collections.singletonList("1.0.0-SNAPSHOT"));
        final ModuleHandler handler = new ModuleHandler(repositoryHandler);

        final List<String> versions = handler.getModuleVersions(module.getName(), filters);

        assertNotNull(versions);
        assertEquals(1, versions.size());
        assertEquals(module.getVersion(), versions.get(0));
        verify(repositoryHandler, times(1)).getModuleVersions(module.getName(),filters);

    }


    @Test
    public void getVersionsOfAModuleWhichDoesNotExist(){
        final FiltersHolder filters = mock(FiltersHolder.class);
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getModuleVersions("doesNotExist", filters)).thenReturn(new ArrayList<String>());
        final ModuleHandler handler = new ModuleHandler(repositoryHandler);

        WebApplicationException exception = null;

        try{
            handler.getModuleVersions("doesNotExist", filters);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(javax.ws.rs.core.Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
    }

    @Test
    public void getAModule(){
        final DbModule module = new DbModule();
        module.setName("module");
        module.setVersion("1.0.0-SNAPSHOT");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getModule(module.getId())).thenReturn(module);
        final ModuleHandler handler = new ModuleHandler(repositoryHandler);

        final DbModule gotModule = handler.getModule(module.getId());

        assertNotNull(gotModule);
        assertEquals(module, gotModule);
        verify(repositoryHandler, times(1)).getModule(module.getId());

    }


    @Test
    public void getAModuleThatDoesNotExist(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ModuleHandler handler = new ModuleHandler(repositoryHandler);

        WebApplicationException exception = null;

        try{
            handler.getModule("doesNotExist");
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(javax.ws.rs.core.Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
    }

    @Test
    public void deleteAModule(){
        final DbModule module = new DbModule();
        module.setName("module");
        module.setVersion("1.0.0-SNAPSHOT");

        final DbArtifact artifact1 = new DbArtifact();
        artifact1.setArtifactId("artifact1");
        artifact1.setVersion("1.0.0-SNAPSHOT");
        module.addArtifact(artifact1);

        final DbArtifact dependency = new DbArtifact();
        dependency.setArtifactId("dependency");
        dependency.setVersion("1.0.0-1");
        module.addDependency(dependency.getGavc(), Scope.COMPILE);

        final DbModule subModule = new DbModule();
        subModule.setName("sub-module");
        subModule.setVersion("1.0.0-SNAPSHOT");
        module.addSubmodule(subModule);

        final DbArtifact artifact2 = new DbArtifact();
        artifact2.setArtifactId("artifact2");
        artifact2.setVersion("1.0.0-SNAPSHOT");
        subModule.addArtifact(artifact2);

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getModule(module.getId())).thenReturn(module);
        when(repositoryHandler.getModule(subModule.getId())).thenReturn(subModule);
        when(repositoryHandler.getArtifact(artifact1.getGavc())).thenReturn(artifact1);
        when(repositoryHandler.getArtifact(artifact2.getGavc())).thenReturn(artifact2);
        when(repositoryHandler.getArtifact(dependency.getGavc())).thenReturn(dependency);

        final ModuleHandler handler = new ModuleHandler(repositoryHandler);
        handler.deleteModule(module.getId());

        verify(repositoryHandler, times(1)).deleteModule(module.getId());
        verify(repositoryHandler, never()).deleteModule(subModule.getId());
        verify(repositoryHandler, times(1)).deleteArtifact(artifact1.getGavc());
        verify(repositoryHandler, times(1)).deleteArtifact(artifact2.getGavc());
        verify(repositoryHandler, never()).deleteArtifact(dependency.getGavc());

    }


    @Test
    public void deleteAModuleThatDoesNotExist(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ModuleHandler handler = new ModuleHandler(repositoryHandler);

        WebApplicationException exception = null;

        try{
            handler.deleteModule("doesNotExist");
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(javax.ws.rs.core.Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
    }

    @Test
    public void promoteAModule(){
        final DbModule module = new DbModule();
        module.setName("module");
        module.setVersion("1.0.0-SNAPSHOT");

        final DbArtifact artifact1 = new DbArtifact();
        artifact1.setArtifactId("artifact1");
        artifact1.setVersion("1.0.0-SNAPSHOT");
        module.addArtifact(artifact1);

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getModule(module.getId())).thenReturn(module);
        when(repositoryHandler.getArtifact(artifact1.getGavc())).thenReturn(artifact1);

        final ModuleHandler handler = new ModuleHandler(repositoryHandler);
        handler.promoteModule(module.getId());

        verify(repositoryHandler, times(1)).promoteModule(module);
    }


    @Test
    public void promoteAModuleThatDoesNotExist(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ModuleHandler handler = new ModuleHandler(repositoryHandler);

        WebApplicationException exception = null;

        try{
            handler.promoteModule("doesNotExist");
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(javax.ws.rs.core.Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
    }

    @Test
    public void getModuleLicenses(){
        final DbModule module = new DbModule();
        module.setName("module");
        module.setVersion("1.0.0-SNAPSHOT");

        final DbArtifact artifact1 = new DbArtifact();
        artifact1.setArtifactId("artifact1");
        artifact1.setVersion("1.0.0-SNAPSHOT");
        module.addArtifact(artifact1);

        final DbLicense license = new DbLicense();
        license.setName("test");
        license.setLongName("License for Grapes tests");
        artifact1.addLicense(license);

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getModule(module.getId())).thenReturn(module);
        when(repositoryHandler.getArtifact(artifact1.getGavc())).thenReturn(artifact1);
        when(repositoryHandler.getMatchingLicenses(license.getName())).thenReturn(asSet(license));

        final ModuleHandler handler = new ModuleHandler(repositoryHandler);
        final List<DbLicense> licenses = handler.getModuleLicenses(module.getId());

        assertNotNull(licenses);
        assertEquals(1, licenses.size());
        assertEquals(license, licenses.get(0));
    }

    @Test
    public void getModule(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ModuleHandler handler = new ModuleHandler(repositoryHandler);
        final FiltersHolder filters = mock(FiltersHolder.class);

        handler.getModules(filters);

        verify(repositoryHandler, times(1)).getModules(filters);
    }

    private <T> Set<T> asSet(T... entries) {
        Set<T> set = new HashSet<>();
        set.addAll(Arrays.asList(entries));
        return set;
    }
}
