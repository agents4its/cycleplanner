/*
Copyright 2013 Marcel Német

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package cvut.fel.nemetma1.graphCreator;

import cvut.fel.nemetma1.connectivity.StronglyConnectedComponentsGenerator;
import cvut.fel.nemetma1.dataStructures.CycleEdge;
import cvut.fel.nemetma1.dataStructures.CycleNode;
import cvut.fel.nemetma1.evaluate.aspects.Aspect;
import cvut.fel.nemetma1.routingService.RoutingService;
import cycle.planner.evaluate.evaluator.Evaluator;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.GraphBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jgrapht.DirectedGraph;

/**
 * Defines
 *
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public abstract class GraphProviderAbstract implements GraphProvider {

    protected Graph<CycleNode, CycleEdge> cycleGraph;
    protected File graphOSMFile;
    protected File graphObjectFile;
    protected boolean isEnabledSaving;
//    protected List<Aspect> aspects;
    boolean destroyTags = false;

    /**
     * When creating graph, if destroyTags is set to true, at the end of creating the graph all the tags from edges and nodes are removed. This is
     * handy after edges had been evaluated. then, tags might not be necessary anymore. by destroying tags, memory requirements drop.
     *
     * @param destroyTags true if destroys have to be destroyed after before returning graph, otherwise false
     */
    public void setDestroyTags(boolean destroyTags) {
        this.destroyTags = destroyTags;
    }

    /**
     * Creates graph from an osm file or loads already created graph from exported object. If graphObjectFile is not null, tries to load an exported
     * graph file
     *
     * @param graphOSMFile OSM file from which graph is created
     * @param graphObjectFile exported graph
     */
    public GraphProviderAbstract(File graphOSMFile, File graphObjectFile) {
        this.graphObjectFile = graphObjectFile;
        this.graphOSMFile = graphOSMFile;
        if (graphOSMFile != null) {
            isEnabledSaving = true;
        }
    }

    /**
     * Creates graph from an OSM file or loads already created graph from exported object. If graphObjectFile is not null, tries to load an exported
     * graph file If aspects are specified, the edges will have EvaluationDetails for each aspect calculated in the process of creating graph.
     *
     * @param graphOSMFile OSM file from which graph is created
     * @param graphObjectFile exported graph
     * @param aspects aspects
     */
//    public GraphProviderAbstract(File graphOSMFile, File graphObjectFile, List<Aspect> aspects) {
//        this.graphObjectFile = graphObjectFile;
//        this.graphOSMFile = graphOSMFile;
//        this.aspects = aspects;
//        isEnabledSaving = true;
//    }

    /**
     * Creates graph from an OSM file.
     *
     * @param graphOSMFile OSM file from which graph is created
     */
    public GraphProviderAbstract(File graphOSMFile) {
        this.graphOSMFile = graphOSMFile;
        isEnabledSaving = false;
    }

    protected abstract Graph<CycleNode, CycleEdge> createGraph();

    /**
     * @return a strongly connected directed graph
     */
    @Override
    public Graph<CycleNode, CycleEdge> getGraph() {
        if (cycleGraph == null) {
            if (isEnabledSaving) {
                cycleGraph = loadGraphObjectFromFile(graphObjectFile);
                if (cycleGraph == null) {
                    cycleGraph = createGraph();
                    saveGraphObjectToFile(graphObjectFile, cycleGraph);
                }
            } else {
                cycleGraph = createGraph();
            }
        }
        System.out.println("Returning graph with " + cycleGraph.getAllNodes().size() + " nodes and " + cycleGraph.getAllEdges().size() + " edges");
        return cycleGraph;
    }

    /**
     * selects only the strongly connected component with the most vertices from a graph
     *
     * @param cycleway graph
     * @return strongly connected graph
     */
    protected Graph<CycleNode, CycleEdge> getLargestStronglyConnectedComponent(Graph<CycleNode, CycleEdge> cycleway) {
        System.out.println("creating largest strongly connected component");
        List<DirectedGraph<CycleNode, CycleEdge>> cc = new StronglyConnectedComponentsGenerator<CycleNode, CycleEdge>().getcomponents(cycleway);
        System.out.println(cc.size() + " strongly connected components components found in graph ");
        Iterator<DirectedGraph<CycleNode, CycleEdge>> iii = cc.iterator();
        int i = 0;
        int largestsize = 0;
        DirectedGraph<CycleNode, CycleEdge> largest = null;
        while (iii.hasNext()) {
            DirectedGraph<CycleNode, CycleEdge> graph = iii.next();
            if (graph.vertexSet().size() > largestsize) {
                largestsize = graph.vertexSet().size();
                largest = graph;
            }
        }
        if (largest == null) {
            System.out.println("largest component not found");
            return null;
        }
        System.out.println("largest component has " + largest.vertexSet().size() + " nodes and " + largest.edgeSet().size() + " edges.");
        System.out.println("");
        GraphBuilder<CycleNode, CycleEdge> gb = new GraphBuilder<>();
        gb.addNodes(largest.vertexSet());
        gb.addEdges(largest.edgeSet());

        System.out.println("recreating graph from elements of largest connected component");
        Graph<CycleNode, CycleEdge> g2 = gb.createGraph();
        return g2;
    }

    /**
     * tries to load a graph from a file that contains an exported java object of graph
     *
     * @param g file that contains exported graph
     * @return graph object loaded from a file
     */
    protected Graph<CycleNode, CycleEdge> loadGraphObjectFromFile(File g) {
        System.out.println("loading graph from " + g.getAbsolutePath());
        if (g.exists()) {
            try {
                FileInputStream fis;
                fis = new FileInputStream(g);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Graph<CycleNode, CycleEdge> loadedGraph = (Graph<CycleNode, CycleEdge>) ois.readObject();
                System.out.println("graph loaded");
                return loadedGraph;
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(RoutingService.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("graph not loaded");
                return null;
            }
        } else {
            System.out.println("graph not loaded");
            return null;
        }
    }

    /**
     * recreates the graph from an OSM file
     */
    @Override
    public void recreateGraphFromOSM() {
        System.out.println("recreating graph");
        cycleGraph = createGraph();
        if (isEnabledSaving) {
            saveGraphObjectToFile(graphObjectFile, cycleGraph);
        }
    }

    /**
     * saves a graph to a file
     *
     * @param g file to save to
     * @param graph graph to save
     */
    public void saveGraphObjectToFile(File g, Graph<CycleNode, CycleEdge> graph) {
        System.out.println("saving graph to " + g.getAbsolutePath());
        try {
            FileOutputStream fos = new FileOutputStream(new File("graph"));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(graph);
        } catch (IOException ex) {
            Logger.getLogger(RoutingService.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("graph saved");
    }

    /**
     * evaluates the edges and saves EvaluationDetails for each aspect into the edge
     *
     * @param graph graph to evaluate
     */
//    protected void evaluateEdges(Graph<CycleNode, CycleEdge> graph) {
//        System.out.println("aspects" + aspects);
//        if (aspects != null && graph != null) {
//            for (Iterator<CycleEdge> it = graph.getAllEdges().iterator(); it.hasNext();) {
//                CycleEdge edge = it.next();
//                for (Iterator<Aspect> itA = aspects.iterator(); itA.hasNext();) {
//                    Aspect aspect = itA.next();
//                    edge.setEvaluationDetails(aspect, aspect.createEvaluationDetails(edge));
//                }
//                edge.setEvaluationDetails(Evaluator.createEvaluationDetails(edge));
//            }
//        }
//    }
    protected void evaluateEdges(Graph<CycleNode, CycleEdge> graph) {
    	for (Iterator<CycleEdge> it = graph.getAllEdges().iterator(); it.hasNext();) {
    		CycleEdge edge = it.next();
    		edge.setEvaluationDetails(Evaluator.createEvaluationDetails(edge));
    	}
    }
/**
 * removes OSM tags from all edges and nodes in the graph (by this graph can be reduced in size)
 * @param graph 
 */
    public void destroyTags(Graph<CycleNode, CycleEdge> graph) {
        if (graph != null) {
            System.out.println("destroying tags");
            for (CycleNode cycleNode : graph.getAllNodes()) {
                cycleNode.destroyTags();
            }
            for (CycleEdge cycleEdge : graph.getAllEdges()) {
                cycleEdge.destroyTags();
            }
            System.out.println("tags destroyed");
        } else {
            System.out.println("can not perform destroy tags operation, cyclegraph is null");
        }
    }
}
