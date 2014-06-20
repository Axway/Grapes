package org.axway.grapes.server.core;


import org.axway.grapes.commons.datamodel.Dependency;
import org.axway.grapes.commons.datamodel.Scope;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.*;

public class DependencyHandlerTest {

    @Test
    public void getModuleDependenciesOnAModuleThatDoesNotExist(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final DependencyHandler dependencyHandler = new DependencyHandler(repositoryHandler);

        WebApplicationException exception = null;
        try{
            dependencyHandler.getModuleDependencies("doesNotExist", mock(FiltersHolder.class));
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());

    }

    @Test
    public void getModuleDependenciesOnAModuleThatDoesNotHaveAny(){
        final DbModule module = new DbModule();
        module.setName("module");
        module.setVersion("1.0.0");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getModule(module.getId())).thenReturn(module);

        final DependencyHandler dependencyHandler = new DependencyHandler(repositoryHandler);

        final List<Dependency> dependencies = dependencyHandler.getModuleDependencies(module.getId(), mock(FiltersHolder.class));

        assertNotNull(dependencies);
        assertEquals(0, dependencies.size());

    }

    @Test
    public void getModuleDependenciesReturnsDirectModuleDependenciesWhatEverTheScope(){
        final DbModule module = new DbModule();
        module.setName("module");
        module.setVersion("1.0.0");

        final DbArtifact dependency1 = new DbArtifact();
        dependency1.setGroupId("org.axway.grapes.test");
        dependency1.setArtifactId("dependency1");
        dependency1.setVersion("123");
        module.addDependency(dependency1.getGavc(), Scope.COMPILE);

        final DbArtifact dependency2 = new DbArtifact();
        dependency2.setGroupId("org.axway.grapes.test");
        dependency2.setArtifactId("dependency2");
        dependency2.setVersion("456");
        module.addDependency(dependency2.getGavc(), Scope.TEST);

        final DbArtifact dependency3 = new DbArtifact();
        dependency3.setGroupId("org.axway.grapes.test");
        dependency3.setArtifactId("dependency3");
        dependency3.setVersion("789");
        module.addDependency(dependency3.getGavc(), Scope.RUNTIME);

        final DbArtifact dependency4 = new DbArtifact();
        dependency4.setGroupId("org.axway.grapes.test");
        dependency4.setArtifactId("dependency4");
        dependency4.setVersion("101");
        module.addDependency(dependency4.getGavc(), Scope.PROVIDED);

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getModule(module.getId())).thenReturn(module);
        when(repositoryHandler.getArtifact(dependency1.getGavc())).thenReturn(dependency1);
        when(repositoryHandler.getArtifact(dependency2.getGavc())).thenReturn(dependency2);
        when(repositoryHandler.getArtifact(dependency3.getGavc())).thenReturn(dependency3);
        when(repositoryHandler.getArtifact(dependency4.getGavc())).thenReturn(dependency4);

        final FiltersHolder filters = new FiltersHolder();
        filters.getScopeHandler().setScopeComp(true);
        filters.getScopeHandler().setScopePro(true);
        filters.getScopeHandler().setScopeRun(true);
        filters.getScopeHandler().setScopeTest(true);
        filters.getDecorator().setShowThirdparty(true);

        final DependencyHandler dependencyHandler = new DependencyHandler(repositoryHandler);

        final List<Dependency> dependencies = dependencyHandler.getModuleDependencies(module.getId(), filters);

        assertNotNull(dependencies);
        assertEquals(4, dependencies.size());
        verify(repositoryHandler, times(1)).getArtifact(dependency1.getGavc());
        verify(repositoryHandler, times(1)).getArtifact(dependency2.getGavc());
        verify(repositoryHandler, times(1)).getArtifact(dependency3.getGavc());
        verify(repositoryHandler, times(1)).getArtifact(dependency4.getGavc());
    }

    @Test
    public void getModuleDependenciesReturnsAlsoSubModuleDependenciesWhatEverTheScope(){
        final DbModule module = new DbModule();
        module.setName("module");
        module.setVersion("1.0.0");

        final DbModule subModule = new DbModule();
        subModule.setName("subModule");
        subModule.setVersion("1.0.0");
        module.addSubmodule(subModule);

        final DbArtifact dependency1 = new DbArtifact();
        dependency1.setGroupId("org.axway.grapes.test");
        dependency1.setArtifactId("dependency1");
        dependency1.setVersion("123");
        subModule.addDependency(dependency1.getGavc(), Scope.COMPILE);

        final DbArtifact dependency2 = new DbArtifact();
        dependency2.setGroupId("org.axway.grapes.test");
        dependency2.setArtifactId("dependency2");
        dependency2.setVersion("456");
        subModule.addDependency(dependency2.getGavc(), Scope.TEST);

        final DbArtifact dependency3 = new DbArtifact();
        dependency3.setGroupId("org.axway.grapes.test");
        dependency3.setArtifactId("dependency3");
        dependency3.setVersion("789");
        subModule.addDependency(dependency3.getGavc(), Scope.RUNTIME);

        final DbArtifact dependency4 = new DbArtifact();
        dependency4.setGroupId("org.axway.grapes.test");
        dependency4.setArtifactId("dependency4");
        dependency4.setVersion("101");
        subModule.addDependency(dependency4.getGavc(), Scope.PROVIDED);

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getModule(module.getId())).thenReturn(module);
        when(repositoryHandler.getArtifact(dependency1.getGavc())).thenReturn(dependency1);
        when(repositoryHandler.getArtifact(dependency2.getGavc())).thenReturn(dependency2);
        when(repositoryHandler.getArtifact(dependency3.getGavc())).thenReturn(dependency3);
        when(repositoryHandler.getArtifact(dependency4.getGavc())).thenReturn(dependency4);

        final FiltersHolder filters = new FiltersHolder();
        filters.getScopeHandler().setScopeComp(true);
        filters.getScopeHandler().setScopePro(true);
        filters.getScopeHandler().setScopeRun(true);
        filters.getScopeHandler().setScopeTest(true);
        filters.getDecorator().setShowThirdparty(true);

        final DependencyHandler dependencyHandler = new DependencyHandler(repositoryHandler);

        final List<Dependency> dependencies = dependencyHandler.getModuleDependencies(module.getId(), filters);

        assertNotNull(dependencies);
        assertEquals(4, dependencies.size());
    }

    @Test
    public void getModuleDependenciesWithADepth(){
        final DbModule module1 = new DbModule();
        module1.setName("module1");
        module1.setVersion("1.0.0");

        final DbArtifact dependency1 = new DbArtifact();
        dependency1.setGroupId("org.axway.grapes.test.module2");
        dependency1.setArtifactId("dependency1");
        dependency1.setVersion("1.1.0");
        module1.addDependency(dependency1.getGavc(), Scope.COMPILE);

        final DbModule module2 = new DbModule();
        module2.setName("module2");
        module2.setVersion("1.1.0");

        final DbArtifact dependency2 = new DbArtifact();
        dependency2.setGroupId("org.axway.grapes.test.module3");
        dependency2.setArtifactId("dependency2");
        dependency2.setVersion("2.1.0");
        module2.addDependency(dependency2.getGavc(), Scope.COMPILE);

        final DbModule module3 = new DbModule();
        module3.setName("module3");
        module3.setVersion("2.1.0");

        final DbArtifact dependency3 = new DbArtifact();
        dependency3.setGroupId("org.axway.grapes.test");
        dependency3.setArtifactId("dependency3");
        dependency3.setVersion("123");
        module3.addDependency(dependency3.getGavc(), Scope.COMPILE);

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getModule(module1.getId())).thenReturn(module1);
        when(repositoryHandler.getModule(module2.getId())).thenReturn(module2);
        when(repositoryHandler.getModule(module3.getId())).thenReturn(module3);
        when(repositoryHandler.getArtifact(dependency1.getGavc())).thenReturn(dependency1);
        when(repositoryHandler.getArtifact(dependency2.getGavc())).thenReturn(dependency2);
        when(repositoryHandler.getArtifact(dependency3.getGavc())).thenReturn(dependency3);
        when(repositoryHandler.getRootModuleOf(dependency1.getGavc())).thenReturn(module2);
        when(repositoryHandler.getRootModuleOf(dependency2.getGavc())).thenReturn(module3);


        final FiltersHolder filters = new FiltersHolder();
        filters.getScopeHandler().setScopeComp(true);
        filters.getScopeHandler().setScopePro(true);
        filters.getScopeHandler().setScopeRun(true);
        filters.getScopeHandler().setScopeTest(true);
        filters.getDecorator().setShowThirdparty(true);
        filters.getDepthHandler().setDepth(1);

        final DependencyHandler dependencyHandler = new DependencyHandler(repositoryHandler);

        List<Dependency> dependencies = dependencyHandler.getModuleDependencies(module1.getId(), filters);
        assertEquals(1, dependencies.size());

        filters.getDepthHandler().setDepth(2);
        dependencies = dependencyHandler.getModuleDependencies(module1.getId(), filters);
        assertEquals(2, dependencies.size());

        filters.getDepthHandler().setDepth(3);
        dependencies = dependencyHandler.getModuleDependencies(module1.getId(), filters);
        assertEquals(3, dependencies.size());
    }

    @Test
    public void getModuleDependenciesWithFullRecursiveParam(){
        final DbModule module1 = new DbModule();
        module1.setName("module1");
        module1.setVersion("1.0.0");

        final DbArtifact dependency1 = new DbArtifact();
        dependency1.setGroupId("org.axway.grapes.test.module2");
        dependency1.setArtifactId("dependency1");
        dependency1.setVersion("1.1.0");
        module1.addDependency(dependency1.getGavc(), Scope.COMPILE);

        final DbModule module2 = new DbModule();
        module2.setName("module2");
        module2.setVersion("1.1.0");

        final DbArtifact dependency2 = new DbArtifact();
        dependency2.setGroupId("org.axway.grapes.test.module3");
        dependency2.setArtifactId("dependency2");
        dependency2.setVersion("2.1.0");
        module2.addDependency(dependency2.getGavc(), Scope.COMPILE);

        final DbModule module3 = new DbModule();
        module3.setName("module3");
        module3.setVersion("2.1.0");

        final DbArtifact dependency3 = new DbArtifact();
        dependency3.setGroupId("org.axway.grapes.test");
        dependency3.setArtifactId("dependency3");
        dependency3.setVersion("123");
        module3.addDependency(dependency3.getGavc(), Scope.COMPILE);

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getModule(module1.getId())).thenReturn(module1);
        when(repositoryHandler.getModule(module2.getId())).thenReturn(module2);
        when(repositoryHandler.getModule(module3.getId())).thenReturn(module3);
        when(repositoryHandler.getArtifact(dependency1.getGavc())).thenReturn(dependency1);
        when(repositoryHandler.getArtifact(dependency2.getGavc())).thenReturn(dependency2);
        when(repositoryHandler.getArtifact(dependency3.getGavc())).thenReturn(dependency3);
        when(repositoryHandler.getRootModuleOf(dependency1.getGavc())).thenReturn(module2);
        when(repositoryHandler.getRootModuleOf(dependency2.getGavc())).thenReturn(module3);


        final FiltersHolder filters = new FiltersHolder();
        filters.getScopeHandler().setScopeComp(true);
        filters.getScopeHandler().setScopePro(true);
        filters.getScopeHandler().setScopeRun(true);
        filters.getScopeHandler().setScopeTest(true);
        filters.getDecorator().setShowThirdparty(true);
        filters.getDepthHandler().setFullRecursive(true);

        final DependencyHandler dependencyHandler = new DependencyHandler(repositoryHandler);

        List<Dependency> dependencies = dependencyHandler.getModuleDependencies(module1.getId(), filters);
        assertEquals(3, dependencies.size());

        filters.getDepthHandler().setFullRecursive(false);
       dependencies = dependencyHandler.getModuleDependencies(module1.getId(), filters);
        assertEquals(1, dependencies.size());
    }

    @Test
    public void getModuleDependenciesCountDependenciesOnceEvenIfThereAreLoops(){
        final DbModule module1 = new DbModule();
        module1.setName("module1");
        module1.setVersion("1.0.0");

        final DbArtifact dependency1 = new DbArtifact();
        dependency1.setGroupId("org.axway.grapes.test.module2");
        dependency1.setArtifactId("dependency1");
        dependency1.setVersion("1.1.0");
        module1.addDependency(dependency1.getGavc(), Scope.COMPILE);

        final DbModule module2 = new DbModule();
        module2.setName("module2");
        module2.setVersion("1.1.0");

        final DbArtifact dependency2 = new DbArtifact();
        dependency2.setGroupId("org.axway.grapes.test.module3");
        dependency2.setArtifactId("dependency2");
        dependency2.setVersion("2.1.0");
        module2.addDependency(dependency2.getGavc(), Scope.COMPILE);

        final DbModule module3 = new DbModule();
        module3.setName("module3");
        module3.setVersion("2.1.0");

        final DbArtifact dependency3 = new DbArtifact();
        dependency3.setGroupId("org.axway.grapes.test.module1");
        dependency3.setArtifactId("dependency3");
        dependency3.setVersion("1.0.0");
        module3.addDependency(dependency3.getGavc(), Scope.COMPILE);

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getModule(module1.getId())).thenReturn(module1);
        when(repositoryHandler.getModule(module2.getId())).thenReturn(module2);
        when(repositoryHandler.getModule(module3.getId())).thenReturn(module3);
        when(repositoryHandler.getArtifact(dependency1.getGavc())).thenReturn(dependency1);
        when(repositoryHandler.getArtifact(dependency2.getGavc())).thenReturn(dependency2);
        when(repositoryHandler.getArtifact(dependency3.getGavc())).thenReturn(dependency3);
        when(repositoryHandler.getRootModuleOf(dependency1.getGavc())).thenReturn(module2);
        when(repositoryHandler.getRootModuleOf(dependency2.getGavc())).thenReturn(module3);
        when(repositoryHandler.getRootModuleOf(dependency3.getGavc())).thenReturn(module1);


        final FiltersHolder filters = new FiltersHolder();
        filters.getScopeHandler().setScopeComp(true);
        filters.getScopeHandler().setScopePro(true);
        filters.getScopeHandler().setScopeRun(true);
        filters.getScopeHandler().setScopeTest(true);
        filters.getDecorator().setShowThirdparty(true);
        filters.getDepthHandler().setFullRecursive(true);

        final DependencyHandler dependencyHandler = new DependencyHandler(repositoryHandler);

        final List<Dependency> dependencies = dependencyHandler.getModuleDependencies(module1.getId(), filters);
        assertEquals(3, dependencies.size());
    }

    @Test
    public void getModuleDependenciesWithFiltersOnScopes(){
        final DbModule module = new DbModule();
        module.setName("module");
        module.setVersion("1.0.0");

        final DbArtifact dependency1 = new DbArtifact();
        dependency1.setGroupId("org.axway.grapes.test");
        dependency1.setArtifactId("dependency1");
        dependency1.setVersion("123");
        module.addDependency(dependency1.getGavc(), Scope.COMPILE);

        final DbArtifact dependency2 = new DbArtifact();
        dependency2.setGroupId("org.axway.grapes.test");
        dependency2.setArtifactId("dependency2");
        dependency2.setVersion("456");
        module.addDependency(dependency2.getGavc(), Scope.PROVIDED);

        final DbArtifact dependency3 = new DbArtifact();
        dependency3.setGroupId("org.axway.grapes.test");
        dependency3.setArtifactId("dependency3");
        dependency3.setVersion("789");
        module.addDependency(dependency3.getGavc(), Scope.RUNTIME);

        final DbArtifact dependency4 = new DbArtifact();
        dependency4.setGroupId("org.axway.grapes.test");
        dependency4.setArtifactId("dependency4");
        dependency4.setVersion("101");
        module.addDependency(dependency4.getGavc(), Scope.TEST);

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getModule(module.getId())).thenReturn(module);
        when(repositoryHandler.getArtifact(dependency1.getGavc())).thenReturn(dependency1);
        when(repositoryHandler.getArtifact(dependency2.getGavc())).thenReturn(dependency2);
        when(repositoryHandler.getArtifact(dependency3.getGavc())).thenReturn(dependency3);
        when(repositoryHandler.getArtifact(dependency4.getGavc())).thenReturn(dependency4);

        final FiltersHolder filters = new FiltersHolder();
        filters.getScopeHandler().setScopeComp(true);
        filters.getScopeHandler().setScopePro(false);
        filters.getScopeHandler().setScopeRun(false);
        filters.getScopeHandler().setScopeTest(false);
        filters.getDecorator().setShowThirdparty(true);

        final DependencyHandler dependencyHandler = new DependencyHandler(repositoryHandler);

        List<Dependency> dependencies = dependencyHandler.getModuleDependencies(module.getId(), filters);
        assertEquals(1, dependencies.size());
        assertEquals(dependency1.getGavc(), dependencies.get(0).getTarget().getGavc());


        filters.getScopeHandler().setScopeComp(false);
        filters.getScopeHandler().setScopePro(true);
        filters.getScopeHandler().setScopeRun(false);
        filters.getScopeHandler().setScopeTest(false);
        filters.getDecorator().setShowThirdparty(true);

        dependencies = dependencyHandler.getModuleDependencies(module.getId(), filters);
        assertEquals(1, dependencies.size());
        assertEquals(dependency2.getGavc(), dependencies.get(0).getTarget().getGavc());

        filters.getScopeHandler().setScopeComp(false);
        filters.getScopeHandler().setScopePro(false);
        filters.getScopeHandler().setScopeRun(true);
        filters.getScopeHandler().setScopeTest(false);
        filters.getDecorator().setShowThirdparty(true);

        dependencies = dependencyHandler.getModuleDependencies(module.getId(), filters);
        assertEquals(1, dependencies.size());
        assertEquals(dependency3.getGavc(), dependencies.get(0).getTarget().getGavc());

        filters.getScopeHandler().setScopeComp(false);
        filters.getScopeHandler().setScopePro(false);
        filters.getScopeHandler().setScopeRun(false);
        filters.getScopeHandler().setScopeTest(true);
        filters.getDecorator().setShowThirdparty(true);

        dependencies = dependencyHandler.getModuleDependencies(module.getId(), filters);
        assertEquals(1, dependencies.size());
        assertEquals(dependency4.getGavc(), dependencies.get(0).getTarget().getGavc());
    }

}
