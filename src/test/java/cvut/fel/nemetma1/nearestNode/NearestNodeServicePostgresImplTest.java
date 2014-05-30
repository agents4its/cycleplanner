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
package cvut.fel.nemetma1.nearestNode;

import cvut.fel.nemetma1.dataStructures.CycleEdge;
import cvut.fel.nemetma1.dataStructures.CycleNode;
import cvut.fel.nemetma1.graphWrapper.AdditionalGraphElements;
import cvut.fel.nemetma1.indexing.EdgeIndex;
import cvut.fel.nemetma1.routingService.ResourceToFile;

import cvut.fel.nemetma1.graphCreator.HighwayGraphProvider;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Node;
import eu.superhub.wp5.wp5common.GPSLocation;
import eu.superhub.wp5.wp5common.Location;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Marcel
 */
public class NearestNodeServicePostgresImplTest {

	static NearestNodeJavaApprox<CycleNode, CycleEdge> nearestNodeJavaApprox;
	static NearestEdgePointServiceWithEdgeIndex nearestEdgePointService;
	static Connection connection;
	static Location location = new GPSLocation(50.08731266926073,
			14.396270079100262);

	public NearestNodeServicePostgresImplTest() {
	}

	public static void main(String[] args) {
		setUpClass();
		testGetNearestNode_nearestNodeJavaApprox();
		testGetNearestNode_nearestEdgePointService();
	}

	public static void setUpClass() {
		try {
			File osm = ResourceToFile
					.getFileFromResource("/praha_medium_completeways_srtm-2013-20-04.osm");
			File g = ResourceToFile
					.getFileFromResource("/praha_medium_completeways_srtm-2013-20-04.osm.oneways.tags.javaobject");
			HighwayGraphProvider graphProvider = new HighwayGraphProvider(osm,
					g);
			graphProvider.setDestroyTags(true);
			// graphProvider.recreateGraphFromOSM();
			Graph<CycleNode, CycleEdge> graph = graphProvider.getGraph();
			System.out
					.println("graph.getAllEdges().iterator().next().getTagsJoinedKeyAndValue()"
							+ graph.getAllEdges().iterator().next()
									.getOSMtags());
			nearestNodeJavaApprox = new NearestNodeJavaApprox<>(graph);
			EdgeIndex<CycleNode, CycleEdge> edgeIndex = new EdgeIndex(graph,
					0.005, 0.005);
			nearestEdgePointService = new NearestEdgePointServiceWithEdgeIndex(
					graph, 500, edgeIndex);
			// List<RoadGraphKmlCreator.GraphProperty> graphProperties = new
			// ArrayList<>();
			// graphProperties.add(new
			// RoadGraphKmlCreator.GraphProperty("cycleWithHighwaysGraph",
			// Color.green, 2, 1));
			// System.out.println("creating kml");
			// RoadGraphKmlCreator.createKml(graph, graphProperties, f);
			// System.out.println("kml created");
		} catch (FileNotFoundException ex) {
			Logger.getLogger(NearestNodeServicePostgresImplTest.class.getName())
					.log(Level.SEVERE, null, ex);
		} catch (Exception ex) {
			Logger.getLogger(NearestNodeServicePostgresImplTest.class.getName())
					.log(Level.SEVERE, null, ex);
		}
	}

	public static void testGetNearestNode_nearestNodeJavaApprox() {
		System.out.println("getNearestNode");
		Node result = nearestNodeJavaApprox.getNearestNode(location);
		System.out.println(result);
	}

	public static void testGetNearestNode_nearestEdgePointService() {
		System.out.println("getNearestNode");
		AdditionalGraphElements ad = new AdditionalGraphElements();
		Node result = nearestEdgePointService.getNearestPoint(location, ad);
		System.out.println(ad.getAdditionalEdges());
		System.out.println(ad.getAdditionalNodes());
		System.out.println(result);
	}
}