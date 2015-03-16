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
package cz.agents.cycleplanner.nearestNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.openstreetmap.osm.data.coordinates.LatLon;

import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.graphWrapper.AdditionalGraphElements;
import cz.agents.cycleplanner.indexing.EdgeIndex;
import cz.agents.cycleplanner.util.GeoCalculationsHelper;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.plannerdataimporter.util.EPSGProjection;
import eu.superhub.wp5.wp5common.location.GPSLocation;
import eu.superhub.wp5.wp5common.location.Location;

/**
 * Uses Edge index to find the closest edge and calculate the closest point on that edge.
 * 
 * Only tested for Prague,Czech republic (simpler math is used for speedup), would not work on the southern hemisphere
 * or below 0 degree of longitude
 * 
 * @author Marcel
 */
public class NearestEdgePointServiceWithEdgeIndex {

	Graph<CycleNode, CycleEdge> graph;
	ArrayList<CycleEdge> edges;
	ArrayList<CycleNode> nodes;
	double boundingBoxLimitInM;
	EdgeIndex<CycleNode, CycleEdge> edgeIndex;
	private EPSGProjection projection;
	
	private static long wayId = -1l;

	public NearestEdgePointServiceWithEdgeIndex(Graph<CycleNode, CycleEdge> graph, double boundingBoxLimitInM,
			EdgeIndex<CycleNode, CycleEdge> edgeIndex) {
		this.graph = graph;
		edges = new ArrayList<>(graph.getAllEdges());
		nodes = new ArrayList<>(graph.getAllNodes());
		this.boundingBoxLimitInM = boundingBoxLimitInM;
		this.edgeIndex = edgeIndex;

		// TODO remove, only here due to test
		try {
			projection = new EPSGProjection(2065);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * finds the closest point on the graph, point can be located in a node of a graph but as well the point can lye on
	 * the edge. if the point lies on the edge, an artificial node is added to addElements,
	 * 
	 * @param location
	 *            location from which to search for the nearest point on graph
	 * @param addElements
	 *            here additional nodes and edges will be added
	 * @return CycleNode closest to the specified location, can be from a graph but can be created and added to
	 *         addElements
	 */
	public CycleNode getNearestPoint(Location location, AdditionalGraphElements addElements) {
		// TODO use projected coordinates
		double referenceLat = location.getLatitude();
		double referenceLon = location.getLongitude();
		final double longMultip = GeoCalculationsHelper.lengthOfLongitudeDegree(referenceLat);
		final double latMultip = GeoCalculationsHelper.lengthOfLatitudeDegree(referenceLat);
		HashSet<CycleEdge> edgesFromIndex = new HashSet<>(edgeIndex.getEdgesIn4Windows(referenceLon, referenceLat));
		CycleEdge edge = findNearestEdge(edgesFromIndex, referenceLat, referenceLon);

		if (edge != null) {
			CycleNode nodeV = edge.getFromNode();
			CycleNode nodeW = edge.getToNode();

			Point p = new Point(0, 0);
			Point v = new Point((referenceLat - nodeV.getLatitude()) * latMultip, (referenceLon - nodeV.getLongitude())
					* longMultip);
			Point w = new Point((referenceLat - nodeW.getLatitude()) * latMultip, (referenceLon - nodeW.getLongitude())
					* longMultip);
			Point c = closestPointOnSegment(p, v, w);
			if (c == v) {
				return nodeV;
			}
			if (c == w) {
				return nodeW;
			} else {
				double newLat = referenceLat - c.x / latMultip;
				double newLon = referenceLon - c.y / longMultip;
				
				CycleNode nodeC = new CycleNode(addElements.getNextFreeID(),
						projection.getProjectedGPSLocation(new GPSLocation(newLat, newLon, (nodeW.getElevation() + nodeV.getElevation()) / 2)), ""
						);
				ArrayList<CycleEdge> cycleEdges = new ArrayList<>();
				// One-way
				addElements.addEdge(createEdge(nodeV, nodeC, edge));
				addElements.addEdge(createEdge(nodeC, nodeW, edge));
				cycleEdges.add(edge);
				// Return-way if exists
				CycleEdge e2 = graph.getEdge(nodeW.getId(), nodeV.getId());
				if (e2 != null) {
					addElements.addEdge(createEdge(nodeW, nodeC, e2));
					addElements.addEdge(createEdge(nodeC, nodeV, e2));
					cycleEdges.add(e2);

				}

				addElements.addNodeAndCreateConnections(nodeC, cycleEdges, graph);

				return nodeC;
			}
		} else {
			// System.out.println("getNearestPoint() -> No edge found");
			return null;
		}

	}

	private CycleEdge createEdge(CycleNode nodeFrom, CycleNode nodeTo, CycleEdge edge) {
		double comulativeLength = LatLon.distanceInMeters(new LatLon(nodeFrom.getLatitude(), nodeFrom.getLongitude()),
				new LatLon(nodeTo.getLatitude(), nodeTo.getLongitude()));
		
		CycleEdge newedge = new CycleEdge(nodeFrom, nodeTo, comulativeLength, wayId--);
		return newedge;
	}

	private CycleEdge findNearestEdge(Collection<CycleEdge> edgesFromIndex, double referenceLat, double referenceLon) {
		// TODO use projected coordinates
		double safePerimeter = edgeIndex.getPerimeterInMForLatitude(referenceLat);
		double safePerimeterSquared = safePerimeter * safePerimeter;
		// System.out.println("safeperimeter " + safePerimeter);
		final double longMultip = GeoCalculationsHelper.lengthOfLongitudeDegree(referenceLat);
		final double latMultip = GeoCalculationsHelper.lengthOfLatitudeDegree(referenceLat);
		CycleEdge edge = null;
		double minDistance = -1;
		for (Iterator<CycleEdge> it = edgesFromIndex.iterator(); it.hasNext();) {
			CycleEdge e = it.next();
			CycleNode nodeV = e.getFromNode();
			CycleNode nodeW = e.getToNode();

			Point p = new Point(0, 0);
			Point v = new Point((referenceLat - nodeV.getLatitude()) * latMultip, (referenceLon - nodeV.getLongitude())
					* longMultip);
			Point w = new Point((referenceLat - nodeW.getLatitude()) * latMultip, (referenceLon - nodeW.getLongitude())
					* longMultip);
			double distancequared = distToSegmentSquared(p, v, w);
			if ((distancequared < minDistance || minDistance == -1) && distancequared < safePerimeterSquared) {
				minDistance = distancequared;
				edge = e;
			}
		}
		return edge;
	}

	private class Point {

		public double x;
		public double y;

		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}

	private double sqr(double x) {
		return x * x;
	}

	private double dist2(Point v, Point w) {
		return sqr(v.x - w.x) + sqr(v.y - w.y);
	}

	/**
	 * distance from point to line segment squared
	 * 
	 * @param p
	 *            point from which distance is measured
	 * @param v
	 *            end point of the line segment
	 * @param w
	 *            end point of the line segment
	 * @return distance from point to line segment squared
	 */
	private double distToSegmentSquared(Point p, Point v, Point w) {
		double l2 = dist2(v, w);
		if (l2 == 0) {
			return dist2(p, v);
		}
		double t = ((p.x - v.x) * (w.x - v.x) + (p.y - v.y) * (w.y - v.y)) / l2;
		if (t < 0) {
			return dist2(p, v);
		}
		if (t > 1) {
			return dist2(p, w);
		}
		return dist2(p, new Point(v.x + t * (w.x - v.x), v.y + t * (w.y - v.y)));
	}

	/**
	 * point on the line segment v-w closest to point p.
	 * 
	 * @param p
	 *            point from which distance is measured
	 * @param v
	 *            end point of the line segment
	 * @param w
	 *            end point of the line segment
	 * @return point on the line segment closest to point p.
	 */
	private Point closestPointOnSegment(Point p, Point v, Point w) {
		double l2 = dist2(v, w);
		if (l2 == 0) {
			return v;
		}
		double t = ((p.x - v.x) * (w.x - v.x) + (p.y - v.y) * (w.y - v.y)) / l2;
		if (t < 0) {
			return v;
		}
		if (t > 1) {
			return w;
		}
		return new Point(v.x + t * (w.x - v.x), v.y + t * (w.y - v.y));

	}
}
