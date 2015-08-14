package org.axway.grapes.core.handler;

import org.axway.grapes.core.service.DependencyService;

import javax.inject.Inject;

public class DependencyHandlerIT {
    @Inject
    DependencyService dependencyService;
/*
    @Test
    public void getModuleDependenciesOnAModuleThatDoesNotExist(){
       
        

        
            dependencyService.getModuleDependencies("doesNotExist", mock(FiltersHolder.class));
        
    }

    @Test
    public void getModuleDependenciesOnAModuleThatDoesNotHaveAny(){
        final Module module = new Module();
        module.setName("module");
        module.setVersion("1.0.0");

       
        when(dependencyService.getModule(module.getId())).thenReturn(module);

        

        final List<Dependency> dependencies = dependencyService.getModuleDependencies(module.getId(), mock(FiltersHolder.class));

        assertNotNull(dependencies);
        assertEquals(0, dependencies.size());

    }

    @Test
    public void getModuleDependenciesReturnsDirectModuleDependenciesWhatEverTheScope(){
        final Module module = new Module();
        module.setName("module");
        module.setVersion("1.0.0");

        final Artifact dependency1 = new Artifact();
        dependency1.setGroupId("org.axway.grapes.test");
        dependency1.setArtifactId("dependency1");
        dependency1.setVersion("123");
        module.addDependency(dependency1.getGavc(), Scope.COMPILE);

        final Artifact dependency2 = new Artifact();
        dependency2.setGroupId("org.axway.grapes.test");
        dependency2.setArtifactId("dependency2");
        dependency2.setVersion("456");
        module.addDependency(dependency2.getGavc(), Scope.TEST);

        final Artifact dependency3 = new Artifact();
        dependency3.setGroupId("org.axway.grapes.test");
        dependency3.setArtifactId("dependency3");
        dependency3.setVersion("789");
        module.addDependency(dependency3.getGavc(), Scope.RUNTIME);

        final Artifact dependency4 = new Artifact();
        dependency4.setGroupId("org.axway.grapes.test");
        dependency4.setArtifactId("dependency4");
        dependency4.setVersion("101");
        module.addDependency(dependency4.getGavc(), Scope.PROVIDED);

       
        when(dependencyService.getModule(module.getId())).thenReturn(module);
        when(dependencyService.getArtifact(dependency1.getGavc())).thenReturn(dependency1);
        when(dependencyService.getArtifact(dependency2.getGavc())).thenReturn(dependency2);
        when(dependencyService.getArtifact(dependency3.getGavc())).thenReturn(dependency3);
        when(dependencyService.getArtifact(dependency4.getGavc())).thenReturn(dependency4);

        final FiltersHolder filters = new FiltersHolder();
        filters.getScopeHandler().setScopeComp(true);
        filters.getScopeHandler().setScopePro(true);
        filters.getScopeHandler().setScopeRun(true);
        filters.getScopeHandler().setScopeTest(true);
        filters.getDecorator().setShowThirdparty(true);

        

        final List<Dependency> dependencies = dependencyService.getModuleDependencies(module.getId(), filters);

        assertNotNull(dependencies);
        assertEquals(4, dependencies.size());
        verify(dependencyService, times(1)).getArtifact(dependency1.getGavc());
        verify(dependencyService, times(1)).getArtifact(dependency2.getGavc());
        verify(dependencyService, times(1)).getArtifact(dependency3.getGavc());
        verify(dependencyService, times(1)).getArtifact(dependency4.getGavc());
    }

    @Test
    public void getModuleDependenciesReturnsAlsoSubModuleDependenciesWhatEverTheScope(){
        final Module module = new Module();
        module.setName("module");
        module.setVersion("1.0.0");

        final Module subModule = new Module();
        subModule.setName("subModule");
        subModule.setVersion("1.0.0");
        module.addSubmodule(subModule);

        final Artifact dependency1 = new Artifact();
        dependency1.setGroupId("org.axway.grapes.test");
        dependency1.setArtifactId("dependency1");
        dependency1.setVersion("123");
        subModule.addDependency(dependency1.getGavc(), Scope.COMPILE);

        final Artifact dependency2 = new Artifact();
        dependency2.setGroupId("org.axway.grapes.test");
        dependency2.setArtifactId("dependency2");
        dependency2.setVersion("456");
        subModule.addDependency(dependency2.getGavc(), Scope.TEST);

        final Artifact dependency3 = new Artifact();
        dependency3.setGroupId("org.axway.grapes.test");
        dependency3.setArtifactId("dependency3");
        dependency3.setVersion("789");
        subModule.addDependency(dependency3.getGavc(), Scope.RUNTIME);

        final Artifact dependency4 = new Artifact();
        dependency4.setGroupId("org.axway.grapes.test");
        dependency4.setArtifactId("dependency4");
        dependency4.setVersion("101");
        subModule.addDependency(dependency4.getGavc(), Scope.PROVIDED);

       
        when(dependencyService.getModule(module.getId())).thenReturn(module);
        when(dependencyService.getArtifact(dependency1.getGavc())).thenReturn(dependency1);
        when(dependencyService.getArtifact(dependency2.getGavc())).thenReturn(dependency2);
        when(dependencyService.getArtifact(dependency3.getGavc())).thenReturn(dependency3);
        when(dependencyService.getArtifact(dependency4.getGavc())).thenReturn(dependency4);

        final FiltersHolder filters = new FiltersHolder();
        filters.getScopeHandler().setScopeComp(true);
        filters.getScopeHandler().setScopePro(true);
        filters.getScopeHandler().setScopeRun(true);
        filters.getScopeHandler().setScopeTest(true);
        filters.getDecorator().setShowThirdparty(true);

        

        final List<Dependency> dependencies = dependencyService.getModuleDependencies(module.getId(), filters);

        assertNotNull(dependencies);
        assertEquals(4, dependencies.size());
    }

    @Test
    public void getModuleDependenciesWithADepth(){
        final Module module1 = new Module();
        module1.setName("module1");
        module1.setVersion("1.0.0");

        final Artifact dependency1 = new Artifact();
        dependency1.setGroupId("org.axway.grapes.test.module2");
        dependency1.setArtifactId("dependency1");
        dependency1.setVersion("1.1.0");
        module1.addDependency(dependency1.getGavc(), Scope.COMPILE);

        final Module module2 = new Module();
        module2.setName("module2");
        module2.setVersion("1.1.0");

        final Artifact dependency2 = new Artifact();
        dependency2.setGroupId("org.axway.grapes.test.module3");
        dependency2.setArtifactId("dependency2");
        dependency2.setVersion("2.1.0");
        module2.addDependency(dependency2.getGavc(), Scope.COMPILE);

        final Module module3 = new Module();
        module3.setName("module3");
        module3.setVersion("2.1.0");

        final Artifact dependency3 = new Artifact();
        dependency3.setGroupId("org.axway.grapes.test");
        dependency3.setArtifactId("dependency3");
        dependency3.setVersion("123");
        module3.addDependency(dependency3.getGavc(), Scope.COMPILE);

       
        when(dependencyService.getModule(module1.getId())).thenReturn(module1);
        when(dependencyService.getModule(module2.getId())).thenReturn(module2);
        when(dependencyService.getModule(module3.getId())).thenReturn(module3);
        when(dependencyService.getArtifact(dependency1.getGavc())).thenReturn(dependency1);
        when(dependencyService.getArtifact(dependency2.getGavc())).thenReturn(dependency2);
        when(dependencyService.getArtifact(dependency3.getGavc())).thenReturn(dependency3);
        when(dependencyService.getRootModuleOf(dependency1.getGavc())).thenReturn(module2);
        when(dependencyService.getRootModuleOf(dependency2.getGavc())).thenReturn(module3);


        final FiltersHolder filters = new FiltersHolder();
        filters.getScopeHandler().setScopeComp(true);
        filters.getScopeHandler().setScopePro(true);
        filters.getScopeHandler().setScopeRun(true);
        filters.getScopeHandler().setScopeTest(true);
        filters.getDecorator().setShowThirdparty(true);
        filters.getDepthHandler().setDepth(1);

        

        List<Dependency> dependencies = dependencyService.getModuleDependencies(module1.getId(), filters);
        assertEquals(1, dependencies.size());

        filters.getDepthHandler().setDepth(2);
        dependencies = dependencyService.getModuleDependencies(module1.getId(), filters);
        assertEquals(2, dependencies.size());

        filters.getDepthHandler().setDepth(3);
        dependencies = dependencyService.getModuleDependencies(module1.getId(), filters);
        assertEquals(3, dependencies.size());
    }

    @Test
    public void getModuleDependenciesWithFullRecursiveParam(){
        final Module module1 = new Module();
        module1.setName("module1");
        module1.setVersion("1.0.0");

        final Artifact dependency1 = new Artifact();
        dependency1.setGroupId("org.axway.grapes.test.module2");
        dependency1.setArtifactId("dependency1");
        dependency1.setVersion("1.1.0");
        module1.addDependency(dependency1.getGavc(), Scope.COMPILE);

        final Module module2 = new Module();
        module2.setName("module2");
        module2.setVersion("1.1.0");

        final Artifact dependency2 = new Artifact();
        dependency2.setGroupId("org.axway.grapes.test.module3");
        dependency2.setArtifactId("dependency2");
        dependency2.setVersion("2.1.0");
        module2.addDependency(dependency2.getGavc(), Scope.COMPILE);

        final Module module3 = new Module();
        module3.setName("module3");
        module3.setVersion("2.1.0");

        final Artifact dependency3 = new Artifact();
        dependency3.setGroupId("org.axway.grapes.test");
        dependency3.setArtifactId("dependency3");
        dependency3.setVersion("123");
        module3.addDependency(dependency3.getGavc(), Scope.COMPILE);

       
        when(dependencyService.getModule(module1.getId())).thenReturn(module1);
        when(dependencyService.getModule(module2.getId())).thenReturn(module2);
        when(dependencyService.getModule(module3.getId())).thenReturn(module3);
        when(dependencyService.getArtifact(dependency1.getGavc())).thenReturn(dependency1);
        when(dependencyService.getArtifact(dependency2.getGavc())).thenReturn(dependency2);
        when(dependencyService.getArtifact(dependency3.getGavc())).thenReturn(dependency3);
        when(dependencyService.getRootModuleOf(dependency1.getGavc())).thenReturn(module2);
        when(dependencyService.getRootModuleOf(dependency2.getGavc())).thenReturn(module3);


        final FiltersHolder filters = new FiltersHolder();
        filters.getScopeHandler().setScopeComp(true);
        filters.getScopeHandler().setScopePro(true);
        filters.getScopeHandler().setScopeRun(true);
        filters.getScopeHandler().setScopeTest(true);
        filters.getDecorator().setShowThirdparty(true);
        filters.getDepthHandler().setFullRecursive(true);

        

        List<Dependency> dependencies = dependencyService.getModuleDependencies(module1.getId(), filters);
        assertEquals(3, dependencies.size());

        filters.getDepthHandler().setFullRecursive(false);
       dependencies = dependencyService.getModuleDependencies(module1.getId(), filters);
        assertEquals(1, dependencies.size());
    }

    @Test
    public void getModuleDependenciesCountDependenciesOnceEvenIfThereAreLoops(){
        final Module module1 = new Module();
        module1.setName("module1");
        module1.setVersion("1.0.0");

        final Artifact dependency1 = new Artifact();
        dependency1.setGroupId("org.axway.grapes.test.module2");
        dependency1.setArtifactId("dependency1");
        dependency1.setVersion("1.1.0");
        module1.addDependency(dependency1.getGavc(), Scope.COMPILE);

        final Module module2 = new Module();
        module2.setName("module2");
        module2.setVersion("1.1.0");

        final Artifact dependency2 = new Artifact();
        dependency2.setGroupId("org.axway.grapes.test.module3");
        dependency2.setArtifactId("dependency2");
        dependency2.setVersion("2.1.0");
        module2.addDependency(dependency2.getGavc(), Scope.COMPILE);

        final Module module3 = new Module();
        module3.setName("module3");
        module3.setVersion("2.1.0");

        final Artifact dependency3 = new Artifact();
        dependency3.setGroupId("org.axway.grapes.test.module1");
        dependency3.setArtifactId("dependency3");
        dependency3.setVersion("1.0.0");
        module3.addDependency(dependency3.getGavc(), Scope.COMPILE);

       
        when(dependencyService.getModule(module1.getId())).thenReturn(module1);
        when(dependencyService.getModule(module2.getId())).thenReturn(module2);
        when(dependencyService.getModule(module3.getId())).thenReturn(module3);
        when(dependencyService.getArtifact(dependency1.getGavc())).thenReturn(dependency1);
        when(dependencyService.getArtifact(dependency2.getGavc())).thenReturn(dependency2);
        when(dependencyService.getArtifact(dependency3.getGavc())).thenReturn(dependency3);
        when(dependencyService.getRootModuleOf(dependency1.getGavc())).thenReturn(module2);
        when(dependencyService.getRootModuleOf(dependency2.getGavc())).thenReturn(module3);
        when(dependencyService.getRootModuleOf(dependency3.getGavc())).thenReturn(module1);


        final FiltersHolder filters = new FiltersHolder();
        filters.getScopeHandler().setScopeComp(true);
        filters.getScopeHandler().setScopePro(true);
        filters.getScopeHandler().setScopeRun(true);
        filters.getScopeHandler().setScopeTest(true);
        filters.getDecorator().setShowThirdparty(true);
        filters.getDepthHandler().setFullRecursive(true);

        

        final List<Dependency> dependencies = dependencyService.getModuleDependencies(module1.getId(), filters);
        assertEquals(3, dependencies.size());
    }

    @Test
    public void getModuleDependenciesWithFiltersOnScopes(){
        final Module module = new Module();
        module.setName("module");
        module.setVersion("1.0.0");

        final Artifact dependency1 = new Artifact();
        dependency1.setGroupId("org.axway.grapes.test");
        dependency1.setArtifactId("dependency1");
        dependency1.setVersion("123");
        module.addDependency(dependency1.getGavc(), Scope.COMPILE);

        final Artifact dependency2 = new Artifact();
        dependency2.setGroupId("org.axway.grapes.test");
        dependency2.setArtifactId("dependency2");
        dependency2.setVersion("456");
        module.addDependency(dependency2.getGavc(), Scope.PROVIDED);

        final Artifact dependency3 = new Artifact();
        dependency3.setGroupId("org.axway.grapes.test");
        dependency3.setArtifactId("dependency3");
        dependency3.setVersion("789");
        module.addDependency(dependency3.getGavc(), Scope.RUNTIME);

        final Artifact dependency4 = new Artifact();
        dependency4.setGroupId("org.axway.grapes.test");
        dependency4.setArtifactId("dependency4");
        dependency4.setVersion("101");
        module.addDependency(dependency4.getGavc(), Scope.TEST);

       
        when(dependencyService.getModule(module.getId())).thenReturn(module);
        when(dependencyService.getArtifact(dependency1.getGavc())).thenReturn(dependency1);
        when(dependencyService.getArtifact(dependency2.getGavc())).thenReturn(dependency2);
        when(dependencyService.getArtifact(dependency3.getGavc())).thenReturn(dependency3);
        when(dependencyService.getArtifact(dependency4.getGavc())).thenReturn(dependency4);

        final FiltersHolder filters = new FiltersHolder();
        filters.getScopeHandler().setScopeComp(true);
        filters.getScopeHandler().setScopePro(false);
        filters.getScopeHandler().setScopeRun(false);
        filters.getScopeHandler().setScopeTest(false);
        filters.getDecorator().setShowThirdparty(true);

        

        List<Dependency> dependencies = dependencyService.getModuleDependencies(module.getId(), filters);
        assertEquals(1, dependencies.size());
        assertEquals(dependency1.getGavc(), dependencies.get(0).getTarget().getGavc());


        filters.getScopeHandler().setScopeComp(false);
        filters.getScopeHandler().setScopePro(true);
        filters.getScopeHandler().setScopeRun(false);
        filters.getScopeHandler().setScopeTest(false);
        filters.getDecorator().setShowThirdparty(true);

        dependencies = dependencyService.getModuleDependencies(module.getId(), filters);
        assertEquals(1, dependencies.size());
        assertEquals(dependency2.getGavc(), dependencies.get(0).getTarget().getGavc());

        filters.getScopeHandler().setScopeComp(false);
        filters.getScopeHandler().setScopePro(false);
        filters.getScopeHandler().setScopeRun(true);
        filters.getScopeHandler().setScopeTest(false);
        filters.getDecorator().setShowThirdparty(true);

        dependencies = dependencyService.getModuleDependencies(module.getId(), filters);
        assertEquals(1, dependencies.size());
        assertEquals(dependency3.getGavc(), dependencies.get(0).getTarget().getGavc());

        filters.getScopeHandler().setScopeComp(false);
        filters.getScopeHandler().setScopePro(false);
        filters.getScopeHandler().setScopeRun(false);
        filters.getScopeHandler().setScopeTest(true);
        filters.getDecorator().setShowThirdparty(true);

        dependencies = dependencyService.getModuleDependencies(module.getId(), filters);
        assertEquals(1, dependencies.size());
        assertEquals(dependency4.getGavc(), dependencies.get(0).getTarget().getGavc());
    }
*/
}
