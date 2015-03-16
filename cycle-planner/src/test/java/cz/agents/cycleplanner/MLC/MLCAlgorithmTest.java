package cz.agents.cycleplanner.MLC;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import cz.agents.cycleplanner.MLC.alg.AbstractMultiLabelCorrectingAlgorithm;
import cz.agents.cycleplanner.MLC.alg.MLC;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.routingService.City;
import cz.agents.cycleplanner.routingService.RoutingService;
import eu.superhub.wp5.graphcommon.graph.Graph;

public class MLCAlgorithmTest {

	RoutingService routingService;

	MLCCostFunction<CycleNode, CycleEdge> costFunction;

	List<MLCTestQuery> queryTestSetPragueSmall = Lists.newArrayList(
			new MLCTestQuery(26395540L, 1140079495L), //[origin=Node [id=26395540], destination=Node [id=1140079495], directDistance=802]
			new MLCTestQuery(1549446934L, 1140079569L), //[origin=Node [id=1549446934], destination=Node [id=1140079569], directDistance=1108]
			new MLCTestQuery(597577515L, 1838277800L), //[origin=Node [id=597577515], destination=Node [id=1838277800], directDistance=392]
			new MLCTestQuery(1140088588L, 1240137318L), //[origin=Node [id=1140088588], destination=Node [id=1240137318], directDistance=783]
			new MLCTestQuery(1838277865L, 1544913359L)); //[origin=Node [id=1838277865], destination=Node [id=1544913359], directDistance=676]

	@Before
	public void setUp() throws Exception {
		double averageSpeedKMpH = 13.68;
		costFunction = new MLCCycleCostFunction(averageSpeedKMpH);
		routingService = RoutingService.INSTANCE;
	}

	@Test
	public void MLC() {
		int index = 0;
		int[] paretoSetSizes = new int[] { 142, 544, 4, 235, 94 };
		Graph<CycleNode, CycleEdge> graph = routingService.getCycleGraph(City.PRAGUE_SMALL);

		if (paretoSetSizes.length != queryTestSetPragueSmall.size()) {
			fail("Length of array of pareto set sizes does not correspond to query test set size!");
		}

		for (MLCTestQuery query : queryTestSetPragueSmall) {
			CycleNode origin = graph.getNodeByNodeId(query.getOrigin());
			CycleNode destination = graph.getNodeByNodeId(query.getDestination());
			AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge> mlcAlgorithm = new MLC<CycleNode, CycleEdge>(
					graph, origin, destination, costFunction);

			mlcAlgorithm.call();

			int paretoSetSize = mlcAlgorithm.getParetoSetSize();

			assertEquals(paretoSetSizes[index++], paretoSetSize);
		}
	}

	

}
