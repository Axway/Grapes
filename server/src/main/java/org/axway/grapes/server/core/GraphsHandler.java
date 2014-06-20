package org.axway.grapes.server.core;

import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.server.core.graphs.AbstractGraph;
import org.axway.grapes.server.core.graphs.ModuleGraph;
import org.axway.grapes.server.core.graphs.TreeNode;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.core.options.filters.CorporateFilter;
import org.axway.grapes.server.db.DataUtils;
import org.axway.grapes.server.db.ModelMapper;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbDependency;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.axway.grapes.server.db.datamodel.DbOrganization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Graphs Handler
 *
 * <p>Handles the graphs builds.</p>
 *
 * @author jdcoffre
 */
public class GraphsHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GraphsHandler.class);

    private final RepositoryHandler repoHandler;
    private final FiltersHolder filters;

    public GraphsHandler(final RepositoryHandler repoHandler, final FiltersHolder filters) {
        this.repoHandler = repoHandler;
        this.filters = filters;
    }


    /**
     * Generate a module graph regarding the filters
     *
     * @param moduleId String
     * @return AbstractGraph
     */
    public AbstractGraph getModuleGraph(final String moduleId) {
        final ModuleHandler moduleHandler = new ModuleHandler(repoHandler);
        final DbModule module = moduleHandler.getModule(moduleId);
        final DbOrganization organization = moduleHandler.getOrganization(module);

        filters.setCorporateFilter(new CorporateFilter(organization));

        final AbstractGraph graph = new ModuleGraph();
        addModuleToGraph(module, graph, 0);

        return graph;
    }

    /**
     * Manage the artifact add to the Module AbstractGraph
     *
     * @param graph
     * @param depth
     */
    private void addModuleToGraph(final DbModule module, final AbstractGraph graph, final int depth) {
        if (graph.isTreated(graph.getId(module))) {
            return;
        }

        final String moduleElementId = graph.getId(module);
        graph.addElement(moduleElementId, module.getVersion(), depth == 0);

        if (filters.getDepthHandler().shouldGoDeeper(depth)) {
            for (DbDependency dep : DataUtils.getAllDbDependencies(module)) {
                if(filters.shouldBeInReport(dep)){
                    addDependencyToGraph(dep, graph, depth + 1, moduleElementId);
                }
            }
        }
    }

    /**
     * Add a dependency to the graph
     *
     * @param dependency
     * @param graph
     * @param depth
     * @param parentId
     */
    private void addDependencyToGraph(final DbDependency dependency, final AbstractGraph graph, final int depth, final String parentId) {
        // In that case of Axway artifact we will add a module to the graph
        if (filters.getCorporateFilter().filter(dependency)) {
            final DbModule dbTarget = repoHandler.getModuleOf(dependency.getTarget());

            // if there is no module, add the artifact to the graph
            if(dbTarget == null){
                LOG.error("Got missing reference: " + dependency.getTarget());
                final DbArtifact dbArtifact = DataUtils.createDbArtifact(dependency.getTarget());
                final String targetElementId = graph.getId(dbArtifact);
                graph.addElement(targetElementId, dbArtifact.getVersion(), false);
                graph.addDependency(parentId, targetElementId, dependency.getScope());
                return;
            }

            // Add the element to the graph
            addModuleToGraph(dbTarget, graph, depth + 1);

            //Add the dependency to the graph
            final String moduleElementId = graph.getId(dbTarget);
            graph.addDependency(parentId, moduleElementId, dependency.getScope());
        }
        // In case a third-party we will add an artifact
        else {
            final DbArtifact dbTarget = repoHandler.getArtifact(dependency.getTarget());
            if(dbTarget == null){
                LOG.error("Got missing artifact: " + dependency.getTarget());
                return;
            }

            if(!graph.isTreated(graph.getId(dbTarget))){
                final ModelMapper modelMapper = new ModelMapper(repoHandler);
                final Artifact target = modelMapper.getArtifact(dbTarget);
                final String targetElementId = graph.getId(target);
                graph.addElement(targetElementId, target.getVersion(), false);
                graph.addDependency(parentId, targetElementId, dependency.getScope());
            }
        }
    }

    /**
     * Generate a groupId tree regarding the filters
     *
     * @param moduleId
     * @return TreeNode
     */
    public TreeNode getModuleTree(final String moduleId) {
        final ModuleHandler moduleHandler = new ModuleHandler(repoHandler);
        final DbModule module = moduleHandler.getModule(moduleId);

        final TreeNode tree = new TreeNode();
        tree.setName(module.getName());

        // Add submodules
        for (DbModule submodule : module.getSubmodules()) {
            addModuleToTree(submodule, tree);
        }

        return tree;
    }

    /**
     * Add a module to a module tree
     *
     * @param module
     * @param tree
     */
    private void addModuleToTree(final DbModule module, final TreeNode tree) {
        final TreeNode subTree = new TreeNode();
        subTree.setName(module.getName());
        tree.addChild(subTree);

        // Add SubsubModules
        for (DbModule subsubmodule : module.getSubmodules()) {
            addModuleToTree(subsubmodule, subTree);
        }
    }
}
