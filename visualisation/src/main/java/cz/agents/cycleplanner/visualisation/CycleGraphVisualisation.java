package cz.agents.cycleplanner.visualisation;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

import cz.agents.agentpolis.tools.geovisio.database.table.column.Column;
import cz.agents.agentpolis.tools.geovisio.database.table.column.ColumnType;
import cz.agents.agentpolis.tools.geovisio.layer.BoundingBox;
import cz.agents.agentpolis.tools.geovisio.layer.GeometryRecord;
import cz.agents.agentpolis.tools.geovisio.layer.Layer;
import cz.agents.agentpolis.tools.geovisio.layer.LayerSettings;
import cz.agents.agentpolis.tools.geovisio.layer.visparameter.VisParameterMapper;
import cz.agents.agentpolis.tools.geovisio.layer.visparameter.VisParameters;
import cz.agents.agentpolis.tools.geovisio.settings.NameSettings;
import cz.agents.agentpolis.tools.geovisio.visualisation.Visualisation;
import cz.agents.agentpolis.tools.geovisio.visualisation.VisualisationSettings;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.GraphBuilder;
import eu.superhub.wp5.graphcommon.graph.elements.Node;

public class CycleGraphVisualisation extends Visualisation {

	private static final Logger log = Logger.getLogger(CycleGraphVisualisation.class);

	private static final Pattern PERMITED_EDGES = Pattern
			.compile("way::highway::living_street|way::highway::primary|way::highway::primary_link|way::highway::residential|way::highway::secondary|way::highway::secondary_link|way::highway::tertiary|way::highway::tertiary_link|way::highway::unclassified|way::highway::road");

	private List<Layer<LineString>> responseLayers = new ArrayList<>();
	private List<Layer<Point>> visitedNodesLayers = new ArrayList<>();
	private List<Layer<Point>> nodeLayers = new ArrayList<>();;
	private List<Layer<LineString>> edgeLayers = new ArrayList<>();;

	public CycleGraphVisualisation(String visualisationName, int inputDataSrid, int dbDataSrid,
			VisualisationSettings settings, BoundingBox boundingBox, VisParameterMapper visParameterMapper,
			boolean deleteIfAlreadyExist) throws MalformedURLException, NoSuchAuthorityCodeException, FactoryException,
			TransformException, ClassNotFoundException, IllegalArgumentException, SQLException, TransformerException {

		super(visualisationName, inputDataSrid, dbDataSrid, settings, boundingBox, visParameterMapper,
				deleteIfAlreadyExist);
	}

	/**
	 * Method simplified sets of edges and nodes and then call method
	 * visualizeGraph().
	 * 
	 * @param nodes
	 * @param edges
	 * @param layerNamesPrefix
	 * @throws SQLException
	 * @throws TransformException
	 */
	public void visualizeJunctionsGraph(Collection<CycleNode> nodes, Collection<CycleEdge> edges,
			String layerNamesPrefix) throws SQLException, TransformException {

		Collection<CycleEdge> simplifiedEdges = simplifyEdges(edges);

		GraphBuilder<CycleNode, CycleEdge> builder = new GraphBuilder<>();
		builder.addNodes(nodes);
		builder.addEdges(simplifiedEdges);
		Graph<CycleNode, CycleEdge> simplifiedGraph = builder.createGraph();

		log.info("Number of nodes: " + simplifiedGraph.getAllNodes().size() + " Number of edges: "
				+ simplifiedGraph.getAllEdges().size());

		Collection<CycleNode> simplifiedNodes = simplifyNodes(nodes, simplifiedGraph);

		visualizeGraph(simplifiedNodes, simplifiedEdges, layerNamesPrefix);
	}

	/**
	 * Create two layers named {@code layerNamesPrefix+"base_nodes/base_edges"}.
	 * It only setup the layers, the data are NOT uploaded to the database. To
	 * save the data to the database call {@code saveToDatabase()} or
	 * {@code saveAndCreateIndexesAndClose()}.
	 * 
	 * @param nodes
	 * @param edges
	 * @param layerNamesPrefix
	 * @throws SQLException
	 * @throws TransformException
	 */
	public void visualizeGraph(Collection<CycleNode> nodes, Collection<CycleEdge> edges, String layerNamesPrefix)
			throws SQLException, TransformException {

		List<Column> nodeColumns = new ArrayList<Column>();
		nodeColumns.add(new Column(ColumnType.DOUBLE, "latitude"));
		nodeColumns.add(new Column(ColumnType.DOUBLE, "longitude"));
		nodeColumns.add(new Column(ColumnType.DOUBLE, "elevation"));
		nodeColumns.add(new Column(ColumnType.STRING, "tags"));

		LayerSettings nodeLayerSettings = new LayerSettings(ColumnType.POINT, layerNamesPrefix.concat("base_nodes"),
				NameSettings.POINT_PARAMETER_STYLE_NAME);
		Layer<Point> nodeLayer = createAndPublishLayerIncludingTable(nodeColumns, nodeLayerSettings);

		List<Column> edgeColumns = new ArrayList<Column>();
		edgeColumns.add(new Column(ColumnType.STRING, "edge_type"));
		edgeColumns.add(new Column(ColumnType.LONG, "node_from"));
		edgeColumns.add(new Column(ColumnType.LONG, "node_to"));
		edgeColumns.add(new Column(ColumnType.STRING, "tags"));
		edgeColumns.add(new Column(ColumnType.INT, "length"));
		edgeColumns.add(new Column(ColumnType.DOUBLE, "drops"));
		edgeColumns.add(new Column(ColumnType.DOUBLE, "rises"));
		edgeColumns.add(new Column(ColumnType.DOUBLE, "travel_time_precomputation"));
		edgeColumns.add(new Column(ColumnType.DOUBLE, "travel_time_slowdown"));
		edgeColumns.add(new Column(ColumnType.DOUBLE, "comfort_multiplier"));
		edgeColumns.add(new Column(ColumnType.DOUBLE, "quietness_multiplier"));
		edgeColumns.add(new Column(ColumnType.DOUBLE, "flatness_multiplier"));
		// edgeColumns.add(new Column(ColumnType.DOUBLE,
		// "junction_prolongation_constant"));

		LayerSettings edgeLayerSettings = new LayerSettings(ColumnType.LINE, layerNamesPrefix.concat("base_edges"),
				NameSettings.LINE_PARAMETER_STYLE_NAME);
		Layer<LineString> edgeLayer = createAndPublishLayerIncludingTable(edgeColumns, edgeLayerSettings);

		GeometryFactory geometryFactory = getGeomFactory();
		VisParameterMapper visParameterMapper = getVisParameterMapper();

		for (CycleNode node : nodes) {
			Coordinate c = new Coordinate(node.getLongitude(), node.getLatitude());
			Point p = geometryFactory.createPoint(c);
			Map<String, Object> map = new HashMap<String, Object>();

			String tags = "";

			if (node.getTagsJoinedKeyAndValue() != null) {
				tags = node.getTagsJoinedKeyAndValue().toString();
			}

			map.put("latitude", node.getLatitude());
			map.put("longitude", node.getLongitude());
			map.put("elevation", node.getElevation());
			map.put("tags", tags);

			VisParameters vp = visParameterMapper.getVisParameter(node);
			addGeometryToLayer(nodeLayer, node.getId(), p, null, null, vp, map);
		}
		nodeLayers.add(nodeLayer);
		log.info(nodes.size() + " nodes added to queue");

		for (CycleEdge edge : edges) {

			Node from = edge.getFromNode();
			Node to = edge.getToNode();

			Coordinate p = new Coordinate(from.getLongitude(), from.getLatitude());
			Coordinate p2 = new Coordinate(to.getLongitude(), to.getLatitude());

			LineString line = geometryFactory.createLineString(new Coordinate[] { p, p2 });

			Map<String, Object> map = new HashMap<String, Object>();

			String tags = "";

			if (edge.getOSMtags() != null) {
				tags = edge.getOSMtags().toString();
			}

			//double travelTime = TravelTimeCriterion.evaluateWithSpeed(edge, 1 / 3.8);

			map.put("edge_type", edge.getClass().getSimpleName());
			map.put("node_from", from.getId());
			map.put("node_to", to.getId());
			map.put("tags", tags);
			map.put("length", (int) edge.getLengthInMetres());
			map.put("drops", edge.getDrops());
			map.put("rises", edge.getRises());
			map.put("travel_time_precomputation", edge.getEvaluationDetails().getTravelTimePrecomputation());
			map.put("travel_time_slowdown", edge.getEvaluationDetails().getTravelTimeSlowdownConstant());
			map.put("comfort_multiplier", edge.getEvaluationDetails().getComfortMultiplier());
			map.put("quietness_multiplier", edge.getEvaluationDetails().getQuietnessMultiplier());
			map.put("flatness_multiplier", edge.getEvaluationDetails().getFlatnessMultiplier());
			// map.put("junction_prolongation_constant",
			// edge.getEvaluationDetails().getJunctionProlongationConstant());

			VisParameters vp = visParameterMapper.getVisParameter(edge);
			addGeometryToLayer(edgeLayer, line, null, null, vp, map);
		}
		edgeLayers.add(edgeLayer);
		log.info(edges.size() + " edges added to queue");
	}

	public void saveToDatabase() throws SQLException {

		long start = System.currentTimeMillis();
		// response layers
		if (responseLayers.size() > 0) {
			log.info("Saving response layers to database started");
			for (Layer<LineString> layer : responseLayers) {
				layer.saveToDatabase();
				log.info("Response layer saved: " + layer.getName());
			}
			log.info("Saving response layers to database finished");
		}

		// visited nodes layers
		if (visitedNodesLayers.size() > 0) {
			log.info("Saving visited nodes layers to database started");
			for (Layer<Point> layer : visitedNodesLayers) {
				layer.saveToDatabase();
				log.info("Visited nodes layer saved: " + layer.getName());
			}
			log.info("Saving visited nodes layers to database finished");
		}

		// base nodes
		if (nodeLayers.size() > 0) {
			log.info("Saving nodes to database started");
			for (Layer<Point> nodeLayer : nodeLayers) {
				nodeLayer.saveToDatabase();
				log.info("Visited nodes layer saved: " + nodeLayer.getName());
			}
			log.info(" saving nodes to database finished");
		}

		// base edges
		if (edgeLayers.size() > 0) {
			log.info(" saving edges to database started");
			for (Layer<LineString> edgeLayer : edgeLayers) {
				edgeLayer.saveToDatabase();
				log.info("Visited nodes layer saved: " + edgeLayer.getName());
			}
			log.info(" saving edges to database finished");
		}
		log.info(String.format("All data saved in %.1f seconds", (double) (System.currentTimeMillis() - start) / 1000.0));
	}

	public void createIndexes() throws SQLException {
		for (Layer<LineString> layer : responseLayers) {
			layer.createIndexes();
		}
		for (Layer<Point> layer : visitedNodesLayers) {
			layer.createIndexes();
		}
		for (Layer<Point> layer : nodeLayers) {
			layer.createIndexes();
		}
		for (Layer<LineString> layer : edgeLayers) {
			layer.createIndexes();
		}
	}

	public void closeConnectionToDatabase() throws SQLException {
		closeConnectionToDb();
	}

	public void saveAndCreateIndexesAndClose() throws SQLException {
		saveToDatabase();
		createIndexes();
		closeConnectionToDatabase();
	}

	private <T extends Geometry> void addGeometryToLayer(Layer<T> layer, long id, T geometry, Timestamp fromTime,
			Timestamp toTime, VisParameters vp, Map<String, Object> description) throws TransformException {
		geometry = (T) getProjectionTransformer().transform(geometry);
		layer.addRecord(new GeometryRecord<T>(id, geometry, fromTime, toTime, vp, description));
	}

	private <T extends Geometry> void addGeometryToLayer(Layer<T> layer, T geometry, Timestamp fromTime,
			Timestamp toTime, VisParameters vp, Map<String, Object> description) throws TransformException {
		geometry = (T) getProjectionTransformer().transform(geometry);
		layer.addRecord(new GeometryRecord<T>(geometry, fromTime, toTime, vp, description));
	}

	private Collection<CycleNode> simplifyNodes(Collection<CycleNode> nodes, Graph<?, ?> graph) {

		Collection<CycleNode> simple = new ArrayList<>();

		for (CycleNode node : nodes) {
			if (!disableNode((CycleNode) node, graph)) {
				simple.add(node);
			}
		}
		return simple;
	}

	private Collection<CycleEdge> simplifyEdges(Collection<CycleEdge> edges) {
		Collection<CycleEdge> simple = new ArrayList<>();
		for (CycleEdge edge : edges) {
			if (!disableEdge((CycleEdge) edge)) {
				simple.add(edge);
			}
		}
		return simple;
	}

	private boolean disableNode(CycleNode node, Graph<?, ?> graph) {
		Set<Long> neighbors = graph.getAllNeighbors(node.getId());

		return neighbors.size() <= 2;
	}

	private boolean disableEdge(CycleEdge edge) {
		if (edge.getOSMtags() != null) {
			for (String tag : edge.getOSMtags()) {
				if (PERMITED_EDGES.matcher(tag).matches())
					return false;
			}
		}

		return true;
	}

}
