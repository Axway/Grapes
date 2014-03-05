package org.axway.grapes.server.core;


import org.axway.grapes.commons.datamodel.Scope;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.core.reports.DependencyReport;
import org.axway.grapes.server.core.version.IncomparableException;
import org.axway.grapes.server.core.version.NotHandledVersionException;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbDependency;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DependenciesHandlerTest {

    private final DbModule dbModule,dbSubmodule, dbModule2, dbSubmodule2;
    private final DbArtifact artifact, artifact2, artifact3,artifact4, artifact5, artifact6, artifact7, artifact8, artifact9;
    private final RepositoryHandler repositoryHandler;

    public DependenciesHandlerTest() throws UnknownHostException {

        dbModule = new DbModule();
        dbModule.setName("root");
        dbModule.setVersion("1.0.0-SNAPSHOT");
        artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("1.0.0-SNAPSHOT");
        dbModule.addArtifact(artifact);

        artifact2 = new DbArtifact();
        artifact2.setGroupId("groupId2");
        artifact2.setArtifactId("artifactId2");
        artifact2.setVersion("1.0.0-11");
        dbModule.addDependency(artifact2.getGavc(), Scope.COMPILE);

        dbSubmodule = new DbModule();
        dbSubmodule.setName("sub");
        dbSubmodule.setVersion("1.0.0-SNAPSHOT");
        artifact3 = new DbArtifact();
        artifact3.setGroupId("groupId3");
        artifact3.setArtifactId("artifactId3");
        artifact3.setVersion("1.0.0-SNAPSHOT");
        dbSubmodule.addArtifact(artifact3);

        artifact4 = new DbArtifact();
        artifact4.setGroupId("groupId4");
        artifact4.setArtifactId("artifactId4");
        artifact4.setVersion("1.0.0-4");
        dbSubmodule.addDependency(artifact4.getGavc(), Scope.TEST);

        artifact5 = new DbArtifact();
        artifact5.setGroupId("groupId5");
        artifact5.setArtifactId("artifactId5");
        artifact5.setVersion("1.0.0-5");
        dbSubmodule.addDependency(artifact5.getGavc(), Scope.RUNTIME);

        artifact6 = new DbArtifact();
        artifact6.setGroupId(GrapesTestUtils.CORPORATE_GROUPID_4TEST + ".dephandler");
        artifact6.setArtifactId("artifactId6");
        artifact6.setVersion("1.0.0-6");
        dbSubmodule.addDependency(artifact6.getGavc(), Scope.PROVIDED);
        dbModule.addSubmodule(dbSubmodule);

        dbModule2 = new DbModule();
        dbModule2.setName("root2");
        dbModule2.setVersion("1.2.0-SNAPSHOT");
        artifact7 = new DbArtifact();
        artifact7.setGroupId(GrapesTestUtils.CORPORATE_GROUPID_4TEST + ".dephandler");
        artifact7.setArtifactId("artifactId7");
        artifact7.setVersion("1.0.0-8");
        dbModule2.addArtifact(artifact7);
        dbSubmodule.addDependency(artifact7.getGavc(), Scope.PROVIDED);
        dbModule.addDependency(artifact7.getGavc(), Scope.PROVIDED);

        dbSubmodule2 = new DbModule();
        dbSubmodule2.setName("root2:sub2");
        dbSubmodule2.setVersion("1.2.0-SNAPSHOT");
        dbModule2.addSubmodule(dbSubmodule2);

        artifact9 = new DbArtifact();
        artifact9.setGroupId(GrapesTestUtils.CORPORATE_GROUPID_4TEST + ".dephandler");
        artifact9.setArtifactId("artifactId9");
        artifact9.setVersion("1.2.0-SNAPSHOT");
        dbSubmodule2.addArtifact(artifact9);
        dbModule.addDependency(artifact9.getGavc(), Scope.COMPILE);

        artifact8 = new DbArtifact();
        artifact8.setGroupId("groupId8");
        artifact8.setArtifactId("artifactId8");
        artifact8.setVersion("1.0.0-8");
        dbModule2.addDependency(artifact8.getGavc(), Scope.COMPILE);

        
        repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);
        when(repositoryHandler.getModule(dbModule2.getId())).thenReturn(dbModule2);
        when(repositoryHandler.getModule(dbSubmodule.getId())).thenReturn(dbSubmodule);
        when(repositoryHandler.getRootModuleOf(artifact7.getGavc())).thenReturn(dbModule2);
        when(repositoryHandler.getModuleOf(artifact7.getGavc())).thenReturn(dbModule2);
        when(repositoryHandler.getRootModuleOf(artifact9.getGavc())).thenReturn(dbModule2);
        when(repositoryHandler.getModuleOf(artifact9.getGavc())).thenReturn(dbSubmodule2);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        when(repositoryHandler.getArtifact(artifact2.getGavc())).thenReturn(artifact2);
        when(repositoryHandler.getArtifact(artifact3.getGavc())).thenReturn(artifact3);
        when(repositoryHandler.getArtifact(artifact4.getGavc())).thenReturn(artifact4);
        when(repositoryHandler.getArtifact(artifact5.getGavc())).thenReturn(artifact5);
        when(repositoryHandler.getArtifact(artifact6.getGavc())).thenReturn(artifact6);
        when(repositoryHandler.getArtifact(artifact7.getGavc())).thenReturn(artifact7);
        when(repositoryHandler.getArtifact(artifact8.getGavc())).thenReturn(artifact8);

    }

    @Test
    public void getAllModuleDependenciesDepth1() {
        final FiltersHolder filters = new FiltersHolder(GrapesTestUtils.getTestCorporateGroupIds());
        filters.getScopeHandler().setScopeRun(true);
        filters.getScopeHandler().setScopeTest(true);
        filters.getDecorator().setShowThirdparty(true);

        final DependenciesHandler dependenciesHandler = new DependenciesHandler(repositoryHandler, filters);

        final List<DbDependency> dependencies = dependenciesHandler.getDependencies(dbModule.getId());
        assertNotNull(dependencies);
        assertEquals(7, dependencies.size());

    }

    @Test
    public void getModuleThirdPartyDepth1() {
        final FiltersHolder filters = new FiltersHolder(GrapesTestUtils.getTestCorporateGroupIds());
        filters.getScopeHandler().setScopeRun(true);
        filters.getScopeHandler().setScopeTest(true);
        filters.getDecorator().setShowThirdparty(true);
        filters.getCorporateFilter().setIsCorporate(false);

        final DependenciesHandler dependenciesHandler = new DependenciesHandler(repositoryHandler, filters);

        final List<DbDependency> dependencies = dependenciesHandler.getDependencies(dbModule.getId());
        assertNotNull(dependencies);
        assertEquals(3, dependencies.size());
    }

    @Test
    public void getModuleAxwayDependenciesDepth1() {
        final FiltersHolder filters = new FiltersHolder(GrapesTestUtils.getTestCorporateGroupIds());
        filters.getScopeHandler().setScopeRun(true);
        filters.getScopeHandler().setScopeTest(true);
        filters.getDecorator().setShowThirdparty(false);
        filters.getCorporateFilter().setIsCorporate(true);

        final DependenciesHandler dependenciesHandler = new DependenciesHandler(repositoryHandler, filters);

        final List<DbDependency> dependencies = dependenciesHandler.getDependencies(dbModule.getId());
        assertNotNull(dependencies);
        assertEquals(4, dependencies.size());
    }

    @Test
    public void getModuleAllDependenciesFullRecursive() {
        final FiltersHolder filters = new FiltersHolder(GrapesTestUtils.getTestCorporateGroupIds());
        filters.getDepthHandler().setFullRecursive(true);
        filters.getScopeHandler().setScopeRun(true);
        filters.getScopeHandler().setScopeTest(true);
        filters.getDecorator().setShowThirdparty(true);
        filters.getDepthHandler().setFullRecursive(true);

        final DependenciesHandler dependenciesHandler = new DependenciesHandler(repositoryHandler, filters);

        final List<DbDependency> dependencies = dependenciesHandler.getDependencies(dbModule.getId());
        assertNotNull(dependencies);
        assertEquals(8, dependencies.size());
    }

    @Test
    public void getModuleAllThirdPartyFullRecursive() {
        final FiltersHolder filters = new FiltersHolder(GrapesTestUtils.getTestCorporateGroupIds());
        filters.getDepthHandler().setFullRecursive(true);
        filters.getScopeHandler().setScopeRun(true);
        filters.getScopeHandler().setScopeTest(true);
        filters.getDecorator().setShowThirdparty(true);
        filters.getDepthHandler().setFullRecursive(true);
        filters.getCorporateFilter().setIsCorporate(false);

        final DependenciesHandler dependenciesHandler = new DependenciesHandler(repositoryHandler, filters);

        final List<DbDependency> dependencies = dependenciesHandler.getDependencies(dbModule.getId());
        assertNotNull(dependencies);
        assertEquals(4, dependencies.size());
    }

    @Test
    public void getReportWithAllScopesDepth1() throws IncomparableException, NotHandledVersionException {
        final FiltersHolder filters = new FiltersHolder(GrapesTestUtils.getTestCorporateGroupIds());
        filters.getScopeHandler().setScopeRun(true);
        filters.getScopeHandler().setScopeTest(true);
        filters.getDecorator().setShowThirdparty(true);

        final DependenciesHandler dependenciesHandler = new DependenciesHandler(repositoryHandler, filters);

        final DependencyReport report = dependenciesHandler.getReport(dbModule.getId());
        assertNotNull(report);
        assertEquals(6, report.getDependencies().size());

    }
    @Test
    public void getReportWithThirdPartyDepth1() throws UnknownHostException, IncomparableException, NotHandledVersionException {
        final FiltersHolder filters = new FiltersHolder(GrapesTestUtils.getTestCorporateGroupIds());
        filters.getScopeHandler().setScopeRun(true);
        filters.getScopeHandler().setScopeTest(true);
        filters.getDecorator().setShowThirdparty(true);
        filters.getCorporateFilter().setIsCorporate(false);

        final DependenciesHandler dependenciesHandler = new DependenciesHandler(repositoryHandler, filters);

        final DependencyReport report = dependenciesHandler.getReport(dbModule.getId());
        assertNotNull(report);
        assertEquals(3, report.getDependencies().size());
    }

    @Test
    public void getReportWithAxwayDependenciesDepth1() throws IncomparableException, NotHandledVersionException {
        final FiltersHolder filters = new FiltersHolder(GrapesTestUtils.getTestCorporateGroupIds());
        filters.getScopeHandler().setScopeRun(true);
        filters.getScopeHandler().setScopeTest(true);
        filters.getDecorator().setShowThirdparty(false);
        filters.getCorporateFilter().setIsCorporate(true);

        final DependenciesHandler dependenciesHandler = new DependenciesHandler(repositoryHandler, filters);

        final DependencyReport report = dependenciesHandler.getReport(dbModule.getId());
        assertNotNull(report);
        assertEquals(3, report.getDependencies().size());
    }

    @Test
    public void getReportModuleWithAllDependenciesFullRecursive() throws IncomparableException, NotHandledVersionException {
        final FiltersHolder filters = new FiltersHolder(GrapesTestUtils.getTestCorporateGroupIds());
        filters.getDepthHandler().setFullRecursive(true);
        filters.getScopeHandler().setScopeRun(true);
        filters.getScopeHandler().setScopeTest(true);
        filters.getDecorator().setShowThirdparty(true);
        filters.getDepthHandler().setFullRecursive(true);

        final DependenciesHandler dependenciesHandler = new DependenciesHandler(repositoryHandler, filters);

        final DependencyReport report = dependenciesHandler.getReport(dbModule.getId());
        assertNotNull(report);
        assertEquals(7, report.getDependencies().size());
    }

    @Test
    public void getDependencyModules(){
        final FiltersHolder filters = new FiltersHolder(GrapesTestUtils.getTestCorporateGroupIds());
        final DependenciesHandler dependenciesHandler = new DependenciesHandler(repositoryHandler, filters);
        final List<DbModule> modules = dependenciesHandler.getModuleDependencies(dbModule.getId(), false);

        assertEquals(2, modules.size());
        assertTrue(modules.contains(dbModule2));
        assertTrue(modules.contains(dbSubmodule2));
    }

    @Test
    public void getRootDependencyModules(){
        final FiltersHolder filters = new FiltersHolder(GrapesTestUtils.getTestCorporateGroupIds());
        final DependenciesHandler dependenciesHandler = new DependenciesHandler(repositoryHandler, filters);
        final List<DbModule> modules = dependenciesHandler.getModuleDependencies(dbModule.getId(), true);

        assertEquals(1, modules.size());
        assertEquals(dbModule2, modules.get(0));
    }

    @Test
    public void getReportWithADoNotUseThirdparty() throws UnknownHostException, IncomparableException, NotHandledVersionException {
        final FiltersHolder filters = new FiltersHolder(GrapesTestUtils.getTestCorporateGroupIds());
        filters.getDecorator().setShowThirdparty(true);

        artifact8.setDoNotUse(true);
        DependenciesHandler dependenciesHandler = new DependenciesHandler(repositoryHandler, filters);

        DependencyReport report = dependenciesHandler.getReport(dbModule2.getId());
        assertNotNull(report);
        assertEquals(1, report.shouldNotBeUsed(artifact8.getGavc()));

        artifact8.setDoNotUse(false);
        dependenciesHandler = new DependenciesHandler(repositoryHandler, filters);
        report = dependenciesHandler.getReport(dbModule2.getId());

        assertNotNull(report);
        assertEquals(0, report.shouldNotBeUsed(artifact8.getGavc()));
    }

}
