package cz.agents.cycleplanner.MLC;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import cz.agents.cycleplanner.MLC.alg.AbstractMultiLabelCorrectingAlgorithm;
import cz.agents.cycleplanner.MLC.alg.MLCBuckets;
import cz.agents.cycleplanner.MLC.alg.MLCCostPruning;
import cz.agents.cycleplanner.MLC.alg.MLCEllipse;
import cz.agents.cycleplanner.MLC.alg.MLCEpsilonDominance;
import cz.agents.cycleplanner.MLC.alg.MLCRatioPruning;
import cz.agents.cycleplanner.MLC.alg.mixEllipseOthers.MLCEllipseBuckets;
import cz.agents.cycleplanner.MLC.alg.mixEllipseOthers.MLCEllipseCostPruning;
import cz.agents.cycleplanner.MLC.alg.mixEllipseOthers.MLCEllipseEpsilonDominance;
import cz.agents.cycleplanner.MLC.alg.mixEllipseOthers.MLCEllipseRatioPruning;
import cz.agents.cycleplanner.MLC.alg.mixEllipseRatioPruningOthers.MLCEllipseRatioPruningBuckets;
import cz.agents.cycleplanner.MLC.alg.mixEllipseRatioPruningOthers.MLCEllipseRatioPruningCostPruning;
import cz.agents.cycleplanner.MLC.alg.mixEllipseRatioPruningOthers.MLCEllipseRatioPruningEpsilonDominance;
import cz.agents.cycleplanner.MLC.alg.mixRatioPruningOthers.MLCRatioPruningBuckets;
import cz.agents.cycleplanner.MLC.alg.mixRatioPruningOthers.MLCRatioPruningCostPruning;
import cz.agents.cycleplanner.MLC.alg.mixRatioPruningOthers.MLCRatioPruningEpsilonDominance;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.routingService.City;
import cz.agents.cycleplanner.routingService.RoutingService;
import eu.superhub.wp5.graphcommon.graph.Graph;

// TODO change to small graph (PRAGUE_SMALL)
public class MLCAlgorithmSpeedUpTest {

	RoutingService routingService;

	MLCCostFunction<CycleNode, CycleEdge> costFunction;

	List<MLCTestQuery> queryTestSetPragueMediumA = Lists.newArrayList(
			new MLCTestQuery(2409213417L, 1140140895L), 
			new MLCTestQuery(1140079547L, 25658462L), 
			new MLCTestQuery(1544957962L, 26517325L), 
			new MLCTestQuery(255617360L, 304756833L), 
			new MLCTestQuery(1314502818L, 1549446898L));


	@Before
	public void setUp() throws Exception {
		double averageSpeedKMpH = 13.68;
		costFunction = new MLCCycleCostFunction(averageSpeedKMpH);
		routingService = RoutingService.INSTANCE;
	}

	@Test
	public void MLCBuckets() {
		int index = 0;
		int[] paretoSetSizes = new int[] { 6, 53, 9, 38, 8 };
		Graph<CycleNode, CycleEdge> graph = routingService.getCycleGraph(City.PRAGUE_MEDIUM_A);

		if (paretoSetSizes.length != queryTestSetPragueMediumA.size()) {
			fail("Length of array of pareto set sizes does not correspond to query test set size!");
		}

		for (MLCTestQuery query : queryTestSetPragueMediumA) {
			CycleNode origin = graph.getNodeByNodeId(query.getOrigin());
			CycleNode destination = graph.getNodeByNodeId(query.getDestination());
			AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge> mlcAlgorithm = new MLCBuckets<CycleNode, CycleEdge>(
					graph, origin, destination, costFunction);

			mlcAlgorithm.call();

			int paretoSetSize = mlcAlgorithm.getParetoSetSize();

			assertEquals(paretoSetSizes[index++], paretoSetSize);
		}

	}

	@Test
	public void MLCCostPruning() {
		int index = 0;
		int[] paretoSetSizes = new int[] { 71, 76, 17, 121, 71 };
		Graph<CycleNode, CycleEdge> graph = routingService.getCycleGraph(City.PRAGUE_MEDIUM_A);

		if (paretoSetSizes.length != queryTestSetPragueMediumA.size()) {
			fail("Length of array of pareto set sizes does not correspond to query test set size!");
		}

		for (MLCTestQuery query : queryTestSetPragueMediumA) {
			CycleNode origin = graph.getNodeByNodeId(query.getOrigin());
			CycleNode destination = graph.getNodeByNodeId(query.getDestination());
			AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge> mlcAlgorithm = new MLCCostPruning<CycleNode, CycleEdge>(
					graph, origin, destination, costFunction);

			mlcAlgorithm.call();

			int paretoSetSize = mlcAlgorithm.getParetoSetSize();

			assertEquals(paretoSetSizes[index++], paretoSetSize);
		}
	}

	@Test
	public void MLCEllipse() {
		int index = 0;
		int[] paretoSetSizes = new int[] { 89, 3341, 75, 2084, 157 };
		Graph<CycleNode, CycleEdge> graph = routingService.getCycleGraph(City.PRAGUE_MEDIUM_A);

		if (paretoSetSizes.length != queryTestSetPragueMediumA.size()) {
			fail("Length of array of pareto set sizes does not correspond to query test set size!");
		}

		for (MLCTestQuery query : queryTestSetPragueMediumA) {
			CycleNode origin = graph.getNodeByNodeId(query.getOrigin());
			CycleNode destination = graph.getNodeByNodeId(query.getDestination());
			AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge> mlcAlgorithm = new MLCEllipse<CycleNode, CycleEdge>(
					graph, origin, destination, costFunction);

			mlcAlgorithm.call();

			int paretoSetSize = mlcAlgorithm.getParetoSetSize();

			assertEquals(paretoSetSizes[index++], paretoSetSize);
		}
	}

	@Test
	public void MLCEpsilonDominance() {
		int index = 0;
		int[] paretoSetSizes = new int[] { 4, 49, 7, 46, 10 };
		Graph<CycleNode, CycleEdge> graph = routingService.getCycleGraph(City.PRAGUE_MEDIUM_A);

		if (paretoSetSizes.length != queryTestSetPragueMediumA.size()) {
			fail("Length of array of pareto set sizes does not correspond to query test set size!");
		}

		for (MLCTestQuery query : queryTestSetPragueMediumA) {
			CycleNode origin = graph.getNodeByNodeId(query.getOrigin());
			CycleNode destination = graph.getNodeByNodeId(query.getDestination());
			AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge> mlcAlgorithm = new MLCEpsilonDominance<CycleNode, CycleEdge>(
					graph, origin, destination, costFunction);

			mlcAlgorithm.call();

			int paretoSetSize = mlcAlgorithm.getParetoSetSize();

			assertEquals(paretoSetSizes[index++], paretoSetSize);
		}
	}

	@Test
	public void MLCJaccardPruning() {
		fail("Not yet implemented");
	}

	@Test
	public void MLCRatioPruning() {
		int index = 0;
		int[] paretoSetSizes = new int[] { 52, 2787, 74, 839, 35 };
		Graph<CycleNode, CycleEdge> graph = routingService.getCycleGraph(City.PRAGUE_MEDIUM_A);

		if (paretoSetSizes.length != queryTestSetPragueMediumA.size()) {
			fail("Length of array of pareto set sizes does not correspond to query test set size!");
		}

		for (MLCTestQuery query : queryTestSetPragueMediumA) {
			CycleNode origin = graph.getNodeByNodeId(query.getOrigin());
			CycleNode destination = graph.getNodeByNodeId(query.getDestination());
			AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge> mlcAlgorithm = new MLCRatioPruning<CycleNode, CycleEdge>(
					graph, origin, destination, costFunction);

			mlcAlgorithm.call();

			int paretoSetSize = mlcAlgorithm.getParetoSetSize();

			assertEquals(paretoSetSizes[index++], paretoSetSize);
		}
	}

	@Test
	public void MLCEllipseBuckets() {
		int index = 0;
		int[] paretoSetSizes = new int[] { 6, 53, 9, 38, 8 };
		Graph<CycleNode, CycleEdge> graph = routingService.getCycleGraph(City.PRAGUE_MEDIUM_A);

		if (paretoSetSizes.length != queryTestSetPragueMediumA.size()) {
			fail("Length of array of pareto set sizes does not correspond to query test set size!");
		}

		for (MLCTestQuery query : queryTestSetPragueMediumA) {
			CycleNode origin = graph.getNodeByNodeId(query.getOrigin());
			CycleNode destination = graph.getNodeByNodeId(query.getDestination());
			AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge> mlcAlgorithm = new MLCEllipseBuckets<CycleNode, CycleEdge>(
					graph, origin, destination, costFunction);

			mlcAlgorithm.call();

			int paretoSetSize = mlcAlgorithm.getParetoSetSize();

			assertEquals(paretoSetSizes[index++], paretoSetSize);
		}
	}

	@Test
	public void MLCEllipseCostPruning() {
		int index = 0;
		int[] paretoSetSizes = new int[] { 71, 76, 17, 121, 71 };
		Graph<CycleNode, CycleEdge> graph = routingService.getCycleGraph(City.PRAGUE_MEDIUM_A);

		if (paretoSetSizes.length != queryTestSetPragueMediumA.size()) {
			fail("Length of array of pareto set sizes does not correspond to query test set size!");
		}

		for (MLCTestQuery query : queryTestSetPragueMediumA) {
			CycleNode origin = graph.getNodeByNodeId(query.getOrigin());
			CycleNode destination = graph.getNodeByNodeId(query.getDestination());
			AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge> mlcAlgorithm = new MLCEllipseCostPruning<CycleNode, CycleEdge>(
					graph, origin, destination, costFunction);

			mlcAlgorithm.call();

			int paretoSetSize = mlcAlgorithm.getParetoSetSize();

			assertEquals(paretoSetSizes[index++], paretoSetSize);
		}
	}

	@Test
	public void MLCEllipseEpsilonDominance() {
		int index = 0;
		int[] paretoSetSizes = new int[] { 4, 49, 7, 46, 10 };
		Graph<CycleNode, CycleEdge> graph = routingService.getCycleGraph(City.PRAGUE_MEDIUM_A);

		if (paretoSetSizes.length != queryTestSetPragueMediumA.size()) {
			fail("Length of array of pareto set sizes does not correspond to query test set size!");
		}

		for (MLCTestQuery query : queryTestSetPragueMediumA) {
			CycleNode origin = graph.getNodeByNodeId(query.getOrigin());
			CycleNode destination = graph.getNodeByNodeId(query.getDestination());
			AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge> mlcAlgorithm = new MLCEllipseEpsilonDominance<CycleNode, CycleEdge>(
					graph, origin, destination, costFunction);

			mlcAlgorithm.call();

			int paretoSetSize = mlcAlgorithm.getParetoSetSize();

			assertEquals(paretoSetSizes[index++], paretoSetSize);
		}
	}

	@Test
	public void MLCEllipseRatioPruning() {
		int index = 0;
		int[] paretoSetSizes = new int[] { 52, 2787, 74, 839, 35 };
		Graph<CycleNode, CycleEdge> graph = routingService.getCycleGraph(City.PRAGUE_MEDIUM_A);

		if (paretoSetSizes.length != queryTestSetPragueMediumA.size()) {
			fail("Length of array of pareto set sizes does not correspond to query test set size!");
		}

		for (MLCTestQuery query : queryTestSetPragueMediumA) {
			CycleNode origin = graph.getNodeByNodeId(query.getOrigin());
			CycleNode destination = graph.getNodeByNodeId(query.getDestination());
			AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge> mlcAlgorithm = new MLCEllipseRatioPruning<CycleNode, CycleEdge>(
					graph, origin, destination, costFunction);

			mlcAlgorithm.call();

			int paretoSetSize = mlcAlgorithm.getParetoSetSize();

			assertEquals(paretoSetSizes[index++], paretoSetSize);
		}
	}

	@Test
	public void MLCEllipseRatioPruningBuckets() {
		int index = 0;
		int[] paretoSetSizes = new int[] { 5, 50, 9, 24, 3 };
		Graph<CycleNode, CycleEdge> graph = routingService.getCycleGraph(City.PRAGUE_MEDIUM_A);

		if (paretoSetSizes.length != queryTestSetPragueMediumA.size()) {
			fail("Length of array of pareto set sizes does not correspond to query test set size!");
		}

		for (MLCTestQuery query : queryTestSetPragueMediumA) {
			CycleNode origin = graph.getNodeByNodeId(query.getOrigin());
			CycleNode destination = graph.getNodeByNodeId(query.getDestination());
			AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge> mlcAlgorithm = new MLCEllipseRatioPruningBuckets<CycleNode, CycleEdge>(
					graph, origin, destination, costFunction);

			mlcAlgorithm.call();

			int paretoSetSize = mlcAlgorithm.getParetoSetSize();

			assertEquals(paretoSetSizes[index++], paretoSetSize);
		}
	}

	@Test
	public void MLCEllipseRatioPruningCostPruning() {
		int index = 0;
		int[] paretoSetSizes = new int[] { 40, 76, 17, 121, 24 };
		Graph<CycleNode, CycleEdge> graph = routingService.getCycleGraph(City.PRAGUE_MEDIUM_A);

		if (paretoSetSizes.length != queryTestSetPragueMediumA.size()) {
			fail("Length of array of pareto set sizes does not correspond to query test set size!");
		}

		for (MLCTestQuery query : queryTestSetPragueMediumA) {
			CycleNode origin = graph.getNodeByNodeId(query.getOrigin());
			CycleNode destination = graph.getNodeByNodeId(query.getDestination());
			AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge> mlcAlgorithm = new MLCEllipseRatioPruningCostPruning<CycleNode, CycleEdge>(
					graph, origin, destination, costFunction);

			mlcAlgorithm.call();

			int paretoSetSize = mlcAlgorithm.getParetoSetSize();

			assertEquals(paretoSetSizes[index++], paretoSetSize);
		}
	}

	@Test
	public void MLCEllipseRatioPruningEpsilonDominance() {
		int index = 0;
		int[] paretoSetSizes = new int[] { 3, 44, 7, 28, 6 };
		Graph<CycleNode, CycleEdge> graph = routingService.getCycleGraph(City.PRAGUE_MEDIUM_A);

		if (paretoSetSizes.length != queryTestSetPragueMediumA.size()) {
			fail("Length of array of pareto set sizes does not correspond to query test set size!");
		}

		for (MLCTestQuery query : queryTestSetPragueMediumA) {
			CycleNode origin = graph.getNodeByNodeId(query.getOrigin());
			CycleNode destination = graph.getNodeByNodeId(query.getDestination());
			AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge> mlcAlgorithm = new MLCEllipseRatioPruningEpsilonDominance<CycleNode, CycleEdge>(
					graph, origin, destination, costFunction);

			mlcAlgorithm.call();

			int paretoSetSize = mlcAlgorithm.getParetoSetSize();

			assertEquals(paretoSetSizes[index++], paretoSetSize);
		}
	}

	@Test
	public void MLCRatioPruningBuckets() {
		int index = 0;
		int[] paretoSetSizes = new int[] { 5, 50, 9, 24, 3 };
		Graph<CycleNode, CycleEdge> graph = routingService.getCycleGraph(City.PRAGUE_MEDIUM_A);

		if (paretoSetSizes.length != queryTestSetPragueMediumA.size()) {
			fail("Length of array of pareto set sizes does not correspond to query test set size!");
		}

		for (MLCTestQuery query : queryTestSetPragueMediumA) {
			CycleNode origin = graph.getNodeByNodeId(query.getOrigin());
			CycleNode destination = graph.getNodeByNodeId(query.getDestination());
			AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge> mlcAlgorithm = new MLCRatioPruningBuckets<CycleNode, CycleEdge>(
					graph, origin, destination, costFunction);

			mlcAlgorithm.call();

			int paretoSetSize = mlcAlgorithm.getParetoSetSize();

			assertEquals(paretoSetSizes[index++], paretoSetSize);
		}
	}

	@Test
	public void MLCRatioPruningCostPruning() {
		int index = 0;
		int[] paretoSetSizes = new int[] { 40, 76, 17, 121, 24 };
		Graph<CycleNode, CycleEdge> graph = routingService.getCycleGraph(City.PRAGUE_MEDIUM_A);

		if (paretoSetSizes.length != queryTestSetPragueMediumA.size()) {
			fail("Length of array of pareto set sizes does not correspond to query test set size!");
		}

		for (MLCTestQuery query : queryTestSetPragueMediumA) {
			CycleNode origin = graph.getNodeByNodeId(query.getOrigin());
			CycleNode destination = graph.getNodeByNodeId(query.getDestination());
			AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge> mlcAlgorithm = new MLCRatioPruningCostPruning<CycleNode, CycleEdge>(
					graph, origin, destination, costFunction);

			mlcAlgorithm.call();

			int paretoSetSize = mlcAlgorithm.getParetoSetSize();

			assertEquals(paretoSetSizes[index++], paretoSetSize);
		}
	}

	@Test
	public void MLCRatioPruningEpsilonDominance() {
		int index = 0;
		int[] paretoSetSizes = new int[] { 3, 44, 7, 28, 6 };
		Graph<CycleNode, CycleEdge> graph = routingService.getCycleGraph(City.PRAGUE_MEDIUM_A);

		if (paretoSetSizes.length != queryTestSetPragueMediumA.size()) {
			fail("Length of array of pareto set sizes does not correspond to query test set size!");
		}

		for (MLCTestQuery query : queryTestSetPragueMediumA) {
			CycleNode origin = graph.getNodeByNodeId(query.getOrigin());
			CycleNode destination = graph.getNodeByNodeId(query.getDestination());
			AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge> mlcAlgorithm = new MLCRatioPruningEpsilonDominance<CycleNode, CycleEdge>(
					graph, origin, destination, costFunction);

			mlcAlgorithm.call();

			int paretoSetSize = mlcAlgorithm.getParetoSetSize();

			assertEquals(paretoSetSizes[index++], paretoSetSize);
		}
	}

}
