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
package cvut.fel.nemetma1.aStar.heuristicFunction;

import java.util.ArrayList;
import java.util.List;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;

import aima.core.search.framework.HeuristicFunction;
import cvut.fel.nemetma1.dataStructures.CycleNode;
import cvut.fel.nemetma1.evaluate.aspects.Aspect;
import cvut.fel.nemetma1.graphWrapper.GraphWrapper;
import cvut.fel.nemetma1.nearestNode.GeoCalculationsHelper;
import eu.superhub.wp5.graphcommon.graph.elements.Node;

/**
 * Heuristic function which takes aspects into account to maintain admissibility.
 * 
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class GraphStraightLineHeuristicWithMultiplier implements HeuristicFunction {

	GraphWrapper graph;
	CycleNode goal;
	double latLength;
	double lonLength;
	double maxSpeedMultiplier = 0;
	double avgSpeed;

	/**
	 * 
	 * @param graph
	 *            graph for searching
	 * @param goal
	 *            goal node
	 * @param aspects
	 *            aspects for evaluating
	 * @param weights
	 *            weights of aspects
	 * @param avgSpeed
	 *            average cruising speed of a user
	 * @throws TransformException
	 * @throws FactoryException
	 * @throws NoSuchAuthorityCodeException
	 */
	public GraphStraightLineHeuristicWithMultiplier(GraphWrapper graph, CycleNode goal, List<Aspect> aspects,
			List<Double> weights, double avgSpeed) {

		this.graph = graph;
		this.goal = goal;

		this.latLength = GeoCalculationsHelper.lengthOfLatitudeDegree(goal.getLatitude());
		this.lonLength = GeoCalculationsHelper.lengthOfLongitudeDegree(goal.getLatitude());

		double sum = 0;
		for (double d : weights) {
			sum = sum + d;
		}
		List<Double> weightsNormalized = new ArrayList<>();
		for (double d : weights) {
			weightsNormalized.add(d / sum);
		}

		int i = 0;
		for (Aspect a : aspects) {
			maxSpeedMultiplier += a.getMaximumMultiplier() * weightsNormalized.get(i);
			i++;
		}
		this.avgSpeed = avgSpeed;
		System.out.println("maxspeedmultiplier" + maxSpeedMultiplier);
	}

	/**
	 * returns estimate of travel time from current node to a goal node. Takes aspects into account to maintain
	 * admissibility.
	 * 
	 * @param state
	 * @return estimate of travel time from current node to a goal node
	 */
	@Override
	public double h(Object state) {
		if (state instanceof Node) {
			CycleNode fromNodeOsm = (CycleNode) state;

			// EdgeUtil.computeDirectDistanceInM(fromNodeOsm, goal);

			// @todo
			// priblizne, na velke vzdialenosti nepresne na male vzdialenosti
			// staci, rychle
			double comulativeLength = GeoCalculationsHelper.distanceE2(fromNodeOsm.getLongitude() * lonLength,
					fromNodeOsm.getLatitude() * latLength, goal.getLongitude() * lonLength, goal.getLatitude()
							* latLength);
			// System.out.println("heuristic "+comulativeLength
			// /(avgSpeed*maxSpeedMultiplier));
			return comulativeLength / (avgSpeed * maxSpeedMultiplier);
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Heuristic function represents estimated travel time from current node to a goal node. Takes aspects into account
	 * to maintain admissibility.
	 * 
	 * A direct direction is computed using projected value. If GPSLocation does not contain projected coordinates, the
	 * distance will be calculated using LatLon.distanceInMeters() function.
	 * 
	 * @param state
	 * @return estimate of travel time from current node to a goal node
	 */
	// @Override
	// public double h(Object state) {
	// if (state instanceof Node) {
	// CycleNode fromNodeOsm = (CycleNode) state;
	//
	// double cumulativeLength;
	// if ((fromNodeOsm.hasProjectedCoordinates()) && (goal.hasProjectedCoordinates())) {
	// // cumulativeLength = GeoCalculationsHelper.distanceE2(fromNodeOsm.getProjectedLatitude(),
	// fromNodeOsm.getProjectedLongitude(), goal.getProjectedLatitude(), goal.getProjectedLongitude());
	// // double x = fromNodeOsm.getProjectedLatitude() - goal.getProjectedLatitude();
	// // double y = fromNodeOsm.getProjectedLongitude() - goal.getProjectedLongitude();
	// // cumulativeLength = Math.sqrt(x*x + y*y);
	// cumulativeLength = FastMath.hypot(fromNodeOsm.getProjectedLatitude() - goal.getProjectedLatitude(),
	// fromNodeOsm.getProjectedLongitude() - goal.getProjectedLongitude());
	// } else {
	// System.out.println("Using LotLan: "+fromNodeOsm.toString()+" "+goal);
	// cumulativeLength = LatLon.distanceInMeters(fromNodeOsm.getLatitude(), fromNodeOsm.getLongitude(),
	// goal.getLatitude(), goal.getLongitude());
	// }
	//
	// return cumulativeLength / (avgSpeed * maxSpeedMultiplier);
	// } else {
	// throw new IllegalArgumentException();
	// }
	// }
}
