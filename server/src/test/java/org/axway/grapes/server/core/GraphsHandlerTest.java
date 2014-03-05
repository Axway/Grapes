package org.axway.grapes.server.core;


import org.axway.grapes.commons.datamodel.Scope;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.core.graphs.AbstractGraph;
import org.axway.grapes.server.core.graphs.GraphDependency;
import org.axway.grapes.server.core.graphs.GraphElement;
import org.axway.grapes.server.core.graphs.TreeNode;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.axway.grapes.server.materials.TestingRepositoryHandler;
import org.axway.grapes.server.materials.cases.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GraphsHandlerTest {

    private TestingRepositoryHandler repoHandler;
    private GraphsHandler graphHandler;
    private FiltersHolder filters;

    @Before
    public void init(){

        filters = new FiltersHolder(GrapesTestUtils.getTestCorporateGroupIds());
        filters.getScopeHandler().setScopeComp(true);
        filters.getScopeHandler().setScopePro(true);
        filters.getScopeHandler().setScopeRun(true);
        filters.getScopeHandler().setScopeTest(true);
        filters.getDecorator().setShowThirdparty(true);
        filters.getDepthHandler().setFullRecursive(true);

        repoHandler = new TestingRepositoryHandler();
        graphHandler = new GraphsHandler(repoHandler, filters);
    }

    @Test
    public void getGraphOfModuleWithOneArtifactWithoutDependency(){
        repoHandler.loadTestCase(new TC01_ModuleWithOneArtifactWithoutDependency());

        final String moduleId = DbModule.generateID(TC01_ModuleWithOneArtifactWithoutDependency.MODULE_NAME, TC01_ModuleWithOneArtifactWithoutDependency.MODULE_VERSION);
        AbstractGraph graph = graphHandler.getModuleGraph(moduleId);

        assertNotNull(graph);
        assertEquals(1, graph.getElements().size());
        assertEquals(TC01_ModuleWithOneArtifactWithoutDependency.MODULE_NAME, graph.getElements().get(0).getValue());
        assertEquals(TC01_ModuleWithOneArtifactWithoutDependency.MODULE_VERSION, graph.getElements().get(0).getVersion());
        assertEquals(true, graph.getElements().get(0).isRoot());
        assertEquals(0, graph.getDependencies().size());

    }

    @Test
    public void getGraphOfModuleWithOneArtifactAndDependencyOfEachType(){
        repoHandler.loadTestCase(new TC02_ModuleWithOneArtifactAndDependencyOfEachType());

        final String moduleId = DbModule.generateID(TC02_ModuleWithOneArtifactAndDependencyOfEachType.MODULE_NAME, TC02_ModuleWithOneArtifactAndDependencyOfEachType.MODULE_VERSION);
        AbstractGraph graph = graph = graphHandler.getModuleGraph(moduleId);

        assertNotNull(graph);
        assertEquals(5, graph.getElements().size());
        assertEquals(4, graph.getDependencies().size());

        GraphDependency compDependency = null, runDependency = null, proDependency = null, testDependency = null;
        for(GraphDependency dependency: graph.getDependencies()){
            if(dependency.getType().equals(Scope.COMPILE.toString())){
                compDependency = dependency;
            }
            else if(dependency.getType().equals(Scope.PROVIDED.toString())){
                proDependency = dependency;
            }
            else if(dependency.getType().equals(Scope.RUNTIME.toString())){
                runDependency = dependency;
            }
            else if(dependency.getType().equals(Scope.TEST.toString())){
                testDependency = dependency;
            }
        }

        assertNotNull(compDependency);
        assertEquals(TC02_ModuleWithOneArtifactAndDependencyOfEachType.MODULE_NAME, compDependency.getSource());
        assertEquals(TC02_ModuleWithOneArtifactAndDependencyOfEachType.COMPILE_DEPENDENCY_MODULE, compDependency.getTarget());

        assertNotNull(proDependency);
        assertNotNull(runDependency);
        assertNotNull(testDependency);

    }

    @Test
    public void getGraphOfModuleWithOneSubmoduleAndDependencyOfEachType(){
        repoHandler.loadTestCase(new TC03_ModuleWithOneSubmoduleAndDependencyOfEachType());

        final String moduleId = DbModule.generateID(TC03_ModuleWithOneSubmoduleAndDependencyOfEachType.MODULE_NAME, TC03_ModuleWithOneSubmoduleAndDependencyOfEachType.MODULE_VERSION);
        AbstractGraph graph = graphHandler.getModuleGraph(moduleId);

        assertNotNull(graph);
        assertEquals(5, graph.getElements().size());
        assertEquals(4, graph.getDependencies().size());

        GraphElement subModule = null;
        for(GraphElement element: graph.getElements()){
            if(element.getValue().equals(TC03_ModuleWithOneSubmoduleAndDependencyOfEachType.SUBMODULE_NAME)){
                subModule = element;
            }
        }

        assertNull(subModule);
    }

    @Test
    public void getGraphOfModuleWithOneArtifactAndThirdParty(){
        repoHandler.loadTestCase(new TC05_ModuleWithOneArtifactAndThirdParty());

        final String moduleId = DbModule.generateID(TC05_ModuleWithOneArtifactAndThirdParty.MODULE_NAME, TC05_ModuleWithOneArtifactAndThirdParty.MODULE_VERSION);
        AbstractGraph graph = graphHandler.getModuleGraph(moduleId);

        assertNotNull(graph);
        assertEquals(5, graph.getElements().size());
        assertEquals(4, graph.getDependencies().size());

        filters.getDecorator().setShowThirdparty(false);
        graph = graphHandler.getModuleGraph(moduleId);

        assertNotNull(graph);
        assertEquals(1, graph.getElements().size());
        assertEquals(0, graph.getDependencies().size());
    }

    @Test
    public void getGraphOfModuleWithOneArtifactWithTransitiveDependencies(){
        repoHandler.loadTestCase(new TC06_ModuleWithOneArtifactWithTransitiveDependencies());

        final String moduleId = DbModule.generateID(TC06_ModuleWithOneArtifactWithTransitiveDependencies.MODULE_NAME, TC06_ModuleWithOneArtifactWithTransitiveDependencies.MODULE_VERSION);
        AbstractGraph graph = graphHandler.getModuleGraph(moduleId);

        assertNotNull(graph);
        assertEquals(3, graph.getElements().size());
        assertEquals(2, graph.getDependencies().size());

        filters.getDepthHandler().setFullRecursive(false);
        graph = graphHandler.getModuleGraph(moduleId);

        assertNotNull(graph);
        assertEquals(2, graph.getElements().size());
        assertEquals(1, graph.getDependencies().size());
    }

    @Test
    public void getGraphOfModuleWithOneArtifactWithLoopDependencies(){
        repoHandler.loadTestCase(new TC07_ModuleWithOneArtifactWithLoopDependencies());

        final String moduleId = DbModule.generateID(TC07_ModuleWithOneArtifactWithLoopDependencies.MODULE_NAME, TC07_ModuleWithOneArtifactWithLoopDependencies.MODULE_VERSION);
        AbstractGraph graph = graphHandler.getModuleGraph(moduleId);

        assertNotNull(graph);
        assertEquals(2, graph.getElements().size());
        assertEquals(2, graph.getDependencies().size());
    }

    @Test
    public void getTreeOfModuleWithOneArtifactWithoutDependency(){
        repoHandler.loadTestCase(new TC01_ModuleWithOneArtifactWithoutDependency());

        final String moduleId = DbModule.generateID(TC01_ModuleWithOneArtifactWithoutDependency.MODULE_NAME, TC01_ModuleWithOneArtifactWithoutDependency.MODULE_VERSION);
        TreeNode tree = graphHandler.getModuleTree(moduleId);

        assertNotNull(tree);
        assertEquals(TC01_ModuleWithOneArtifactWithoutDependency.MODULE_NAME, tree.getName());
        assertEquals(0, tree.getChildren().size());
    }

    @Test
    public void getTreeOfModuleWithOneSubmodule(){
        repoHandler.loadTestCase(new TC03_ModuleWithOneSubmoduleAndDependencyOfEachType());

        final String moduleId = DbModule.generateID(TC03_ModuleWithOneSubmoduleAndDependencyOfEachType.MODULE_NAME, TC03_ModuleWithOneSubmoduleAndDependencyOfEachType.MODULE_VERSION);
        TreeNode tree = graphHandler.getModuleTree(moduleId);

        assertNotNull(tree);
        assertEquals(1, tree.getChildren().size());

        TreeNode subModule = tree.getChildren().get(0);
        assertEquals(TC03_ModuleWithOneSubmoduleAndDependencyOfEachType.SUBMODULE_NAME, subModule.getName());
        assertEquals(0, subModule.getChildren().size());

    }


}
