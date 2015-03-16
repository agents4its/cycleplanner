package cz.agents.cycleplanner.creators;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.GraphBuilder;
import eu.superhub.wp5.wp5common.location.GPSLocation;

public class GraphAnomalyDetectorTest {

	private Graph<CycleNode, CycleEdge> graph;

	@Before
	public void setUp() throws Exception {
		GraphBuilder<CycleNode, CycleEdge> builder = new GraphBuilder<>();

		CycleNode a = new CycleNode(1, new GPSLocation(3d, 3d, 3d, 3d, 0d), "");
		builder.addNode(a);
		CycleNode a2 = new CycleNode(12, new GPSLocation(3d, 3d, 3d, 3d, 0d), "");
		builder.addNode(a2);
		CycleNode b = new CycleNode(2, new GPSLocation(3d, 1d, 3d, 1d, 0d), "");
		builder.addNode(b);
		CycleNode c = new CycleNode(3, new GPSLocation(5d, 3d, 5d, 3d, 0d), "");
		builder.addNode(c);
		CycleNode d = new CycleNode(4, new GPSLocation(3d, 5d, 3d, 5d, 0d), "");
		builder.addNode(d);
		CycleNode d2 = new CycleNode(42, new GPSLocation(3d, 5d, 3d, 5d, 0d), "");
		builder.addNode(d2);

		CycleEdge e1 = new CycleEdge(a, b, 2);
		builder.addEdge(e1);
		CycleEdge e2 = new CycleEdge(a, a, 0);
		builder.addEdge(e2);
		CycleEdge e3 = new CycleEdge(a, c, 2);
		builder.addEdge(e3);
		CycleEdge e4 = new CycleEdge(a, d, 2);
		builder.addEdge(e4);
		CycleEdge e5 = new CycleEdge(d, d2, 0);
		builder.addEdge(e5);
		CycleEdge e6 = new CycleEdge(a, a2, 0);
		builder.addEdge(e6);

		graph = builder.createGraph();
	}

	@Test
	public void test() {
		GraphAnomalyDetector detector = new GraphAnomalyDetector(graph);
		graph = detector.detect();

		Assert.assertEquals(4, graph.getAllNodes().size());
		Assert.assertEquals(3, graph.getAllEdges().size());
	}

}
