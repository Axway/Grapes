/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.axway.grapes.server.core.graphs;

import org.axway.grapes.commons.datamodel.Scope;
import org.junit.Test;

import static org.junit.Assert.*;

public class ModuleGraphTest {
    
    @Test
    public void checkElementManagement(){
        ModuleGraph graph = new ModuleGraph();
        graph.addElement("test", "1.0.0-SNAPSHOT", true);
        graph.addElement("test2", "1.0.0-SNAPSHOT", false);
        assertEquals(2, graph.getElements().size());
        
        graph.addElement("test", "1.0.0-SNAPSHOT", true);
        assertEquals(2, graph.getElements().size());
        
        assertTrue(graph.isTreated("test"));
        assertFalse(graph.isTreated("test3"));
    }
    
    @Test
    public void checkDependencyManagement(){
        ModuleGraph graph = new ModuleGraph();
        graph.addElement("test", "1.0.0-SNAPSHOT", true);
        graph.addElement("test2", "1.0.0-SNAPSHOT", true);
        graph.addDependency("test", "test2", Scope.TEST);
        assertEquals(1, graph.getDependencies().size());
        assertEquals("test", graph.getDependencies().get(0).getSource());
        assertEquals("test2", graph.getDependencies().get(0).getTarget());
        assertEquals(Scope.TEST.toString(), graph.getDependencies().get(0).getType());
        
    }
}
