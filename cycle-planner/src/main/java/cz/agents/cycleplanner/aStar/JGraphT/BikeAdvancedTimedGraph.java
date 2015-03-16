package cz.agents.cycleplanner.aStar.JGraphT;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.EdgeFactory;
import org.joda.time.ReadableInstant;

import cz.agents.cycleplanner.aStar.Profile;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import eu.superhub.wp5.graphcommon.graph.elements.Node;
import eu.superhub.wp5.plannercore.structures.evaluators.additionalkeysevaluators.AdditionalKeyEvaluator;
import eu.superhub.wp5.plannercore.structures.search.SimpleDirectedGraph;
import eu.superhub.wp5.plannercore.structures.timedstructures.TimedEdge;
import eu.superhub.wp5.plannercore.structures.timedstructures.TimedNode;
import eu.superhub.wp5.wp5common.modes.JourneyPlanTemplate;
import eu.superhub.wp5.wp5common.modes.ModeOfTransport;

/**
 * Created by Jan Nykl(CVUT) on 10.03.14.
 */
public class BikeAdvancedTimedGraph implements SimpleDirectedGraph<TimedNode,TimedEdge> {

    private Graph<CycleNode,CycleEdge> graph;
    private AdditionalKeyEvaluator<TimedNode> ake;
    private JourneyPlanTemplate journeyPlanTemplate;

    /**
     * Cruising speed in kilometers per hour
     */
    private final double defaultCruisingSpeed;


    public BikeAdvancedTimedGraph(Graph<? extends Node, ? extends Edge> graph, AdditionalKeyEvaluator<TimedNode> ake, double cruisingSpeedInKmph) {
        this.defaultCruisingSpeed = cruisingSpeedInKmph;
        this.graph = (Graph<CycleNode, CycleEdge>) graph;
        this.ake = ake;

        this.journeyPlanTemplate = JourneyPlanTemplate.BIKE_ONLY;
    }

    public BikeAdvancedTimedGraph(Graph<CycleNode,CycleEdge> graph, Profile profile, double cruisingSpeedInKmph) {
        this.defaultCruisingSpeed = cruisingSpeedInKmph;
        this.graph = graph;
        this.ake = new CriterionAKE(profile, cruisingSpeedInKmph);

        this.journeyPlanTemplate = JourneyPlanTemplate.BIKE_ONLY;
    }

    @Override
    public Set<TimedNode> successorsOf(TimedNode vertex) {
        Set<TimedNode> successors = new HashSet<>();

        for (CycleEdge edge : graph.getNodeOutcomingEdges(vertex.getId())) {

            if (edge.checkFeasibility(journeyPlanTemplate)) {

                CycleNode successorNode = graph.getNodeByNodeId(edge.getToNodeId());

                ReadableInstant arrivalTimeInTargetNode = edge.findNearestArrivalTimeAtTerminalNode(vertex.getArrivalTime(), defaultCruisingSpeed);
                long duration = arrivalTimeInTargetNode.getMillis() - vertex.getArrivalTime().getMillis();

                double distanceReachedInMeters = vertex.getDistanceReachedInMeters() + edge.getLengthInMetres();

                TimedNode successor = new TimedNode(successorNode, arrivalTimeInTargetNode, vertex.getElapsedTimeInSeconds() + ((double) duration / 1000), distanceReachedInMeters, ake.computeAdditionalKey(vertex,successorNode,edge), ModeOfTransport.BIKE, vertex, edge);
                successors.add(successor);
            }
        }
        return successors;
    }

    @Override
    public TimedNode predecessorOf(TimedNode vertex) {
        return (TimedNode) vertex.getPredecessor();
    }


    @Override
    public int inDegreeOf(TimedNode vertex) {
        return 0;
    }

    @Override
    public Set<TimedEdge> incomingEdgesOf(TimedNode vertex) {
        return null;
    }

    @Override
    public int outDegreeOf(TimedNode vertex) {
        return 0;
    }

    @Override
    public Set<TimedEdge> outgoingEdgesOf(TimedNode vertex) {

        Set<TimedEdge> edges = new HashSet<>();

        for (TimedNode successor : this.successorsOf(vertex)) {
            edges.add(new TimedEdge(vertex, successor));
        }
        return edges;

    }

    @Override
    public boolean containsVertex(TimedNode timedNode) {
        return graph.containsNodeByNodeId(timedNode.getId());
    }

    /**
     * REST NOT NEEDED FOR DIJKSTRA'S ALGORITHM AND A*
     */


    @Override
    public Set<TimedEdge> edgeSet() {
        return null;
    }

    @Override
    public Set<TimedEdge> edgesOf(TimedNode vertex) {
        return null;
    }

    @Override
    public boolean removeAllEdges(Collection<? extends TimedEdge> edges) {
        return false;
    }

    @Override
    public Set<TimedEdge> removeAllEdges(TimedNode sourceVertex, TimedNode targetVertex) {
        return null;
    }

    @Override
    public boolean removeAllVertices(Collection<? extends TimedNode> vertices) {
        return false;
    }

    @Override
    public TimedEdge removeEdge(TimedNode sourceVertex, TimedNode targetVertex) {
        return null;
    }

    @Override
    public boolean removeEdge(TimedEdge timedEdge) {
        return false;
    }

    @Override
    public boolean removeVertex(TimedNode timedNode) {
        return false;
    }

    @Override
    public Set<TimedNode> vertexSet() {
        return null;
    }

    @Override
    public TimedNode getEdgeSource(TimedEdge e) {

        return (TimedNode) e.getSourceNode();
    }

    @Override
    public TimedNode getEdgeTarget(TimedEdge e) {

        return (TimedNode) e.getTargetNode();
    }

    @Override
    public Set<TimedEdge> getAllEdges(TimedNode sourceVertex, TimedNode targetVertex) {
        return null;
    }

    @Override
    public TimedEdge getEdge(TimedNode sourceVertex, TimedNode targetVertex) {
        return new TimedEdge(sourceVertex, targetVertex);
    }

    @Override
    public EdgeFactory<TimedNode, TimedEdge> getEdgeFactory() {
        return null;
    }

    @Override
    public TimedEdge addEdge(TimedNode sourceVertex, TimedNode targetVertex) {
        return null;
    }

    @Override
    public boolean addEdge(TimedNode sourceVertex, TimedNode targetVertex, TimedEdge timedEdge) {
        return false;
    }

    @Override
    public boolean addVertex(TimedNode timedNode) {
        return false;
    }

    @Override
    public boolean containsEdge(TimedNode sourceVertex, TimedNode targetVertex) {
        return false;
    }

    @Override
    public boolean containsEdge(TimedEdge timedEdge) {
        return false;
    }

    @Override
    public double getEdgeWeight(TimedEdge e) {

        return ((double) e.getDurationInMillis() / 1000);
    }

}
