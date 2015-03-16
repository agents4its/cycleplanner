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
package cz.agents.cycleplanner.routingService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.log4j.Logger;

import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.Graph;

/**
 * TODO javadoc
 * 
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class GraphProvider {

	private static final Logger log = Logger.getLogger(GraphProvider.class);

	private Graph<CycleNode, CycleEdge> cycleGraph;
	private File graphObjectFile;
	boolean destroyTags = false;

	/**
	 * When creating graph, if destroyTags is set to true, at the end of
	 * creating the graph all the tags from edges and nodes are removed. This is
	 * handy after edges had been evaluated. then, tags might not be necessary
	 * anymore. by destroying tags, memory requirements drop.
	 * 
	 * @param destroyTags
	 *            true if destroys have to be destroyed after before returning
	 *            graph, otherwise false
	 */
	public void setDestroyTags(boolean destroyTags) {
		this.destroyTags = destroyTags;
	}

	/**
	 * Creates graph from an osm file or loads already created graph from
	 * exported object. If graphObjectFile is not null, tries to load an
	 * exported graph file
	 * 
	 * @param graphOSMFile
	 *            OSM file from which graph is created
	 * @param graphObjectFile
	 *            exported graph
	 */
	public GraphProvider(File graphObjectFile) {
		this.graphObjectFile = graphObjectFile;
	}


	/**
	 * @return a strongly connected directed graph
	 */
	public Graph<CycleNode, CycleEdge> getGraph() {

		if (cycleGraph == null) {
			cycleGraph = loadGraphObjectFromFile(graphObjectFile);
		}

		return cycleGraph;
	}


	/**
	 * tries to load a graph from a file that contains an exported java object
	 * of graph
	 * 
	 * @param g
	 *            file that contains exported graph
	 * @return graph object loaded from a file
	 */
	@SuppressWarnings("unchecked")
	private Graph<CycleNode, CycleEdge> loadGraphObjectFromFile(File g) {
		log.info("Loading graph from " + g.getAbsolutePath());

		if (g.exists()) {
			try {
				FileInputStream fis;
				fis = new FileInputStream(g);
				ObjectInputStream ois = new ObjectInputStream(fis);

				Graph<CycleNode, CycleEdge> loadedGraph = (Graph<CycleNode, CycleEdge>) ois
						.readObject();

				log.info("Graph loaded.");
				ois.close();
				return loadedGraph;
			} catch (IOException | ClassNotFoundException ex) {
				log.error("Graph not loaded: \n" + ex.getMessage());
				return null;
			}
		} else {
			log.error("Graph not loaded.");
			return null;
		}
	}

	/**
	 * removes OSM tags from all edges and nodes in the graph (by this graph can
	 * be reduced in size)
	 * 
	 * @param graph
	 */
	public void destroyTags(Graph<CycleNode, CycleEdge> graph) {
		if (graph != null) {
			log.info("Destroying tags.");
			for (CycleNode cycleNode : graph.getAllNodes()) {
				cycleNode.destroyTags();
			}
			for (CycleEdge cycleEdge : graph.getAllEdges()) {
				cycleEdge.destroyTags();
			}
			log.info("Tags destroyed");
		} else {
			log.info("Could not perform destroy tags operation, cyclegraph is null.");
		}
	}
}
