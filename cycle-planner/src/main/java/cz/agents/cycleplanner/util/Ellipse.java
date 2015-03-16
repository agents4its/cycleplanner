package cz.agents.cycleplanner.util;

import org.apache.log4j.Logger;

import eu.superhub.wp5.graphcommon.graph.utils.EdgeUtil;
import eu.superhub.wp5.wp5common.location.GPSLocation;

public class Ellipse {

	private static final double MINIMAL_DISTANCE_FROM_FOCUS_TO_ELLIPSE = 500;

	private static Logger log = Logger.getLogger(Ellipse.class);

	private final GPSLocation focus1;
	private final GPSLocation focus2;
	private final double ellipseWidth;

	/**
	 * TODO javadoc
	 */
	private final double a;

	/**
	 * TODO javadoc
	 */
	private final double b;

	public Ellipse(GPSLocation focus1, GPSLocation focus2, double aOverB) {

		this.focus1 = focus1;
		this.focus2 = focus2;

		double distanceFromFocusToCenter = EdgeUtil.computeDirectDistanceInM(focus1, focus2) * 0.5;
		double aDerivedFromEquation = Math.sqrt((distanceFromFocusToCenter * distanceFromFocusToCenter)
				/ (1 - (1 / (aOverB * aOverB))));

		if (aDerivedFromEquation - distanceFromFocusToCenter <= MINIMAL_DISTANCE_FROM_FOCUS_TO_ELLIPSE) {
			this.a = distanceFromFocusToCenter + MINIMAL_DISTANCE_FROM_FOCUS_TO_ELLIPSE;
		} else {
			this.a = aDerivedFromEquation;
		}
		this.b = this.a / aOverB;
		this.ellipseWidth = 2 * this.a;

		log.debug("a/b during inicialization process: " + aOverB);
		log.debug("Width of ellipse: " + this.ellipseWidth);
	}

	public boolean isInside(GPSLocation node) {
		double distanceToFocus1 = EdgeUtil.computeDirectDistanceInM(node, focus1);
		double distanceToFocus2 = EdgeUtil.computeDirectDistanceInM(node, focus2);

		return (distanceToFocus1 + distanceToFocus2) <= ellipseWidth;
	}

	public GPSLocation getFocus1() {
		return focus1;
	}

	/**
	 * 
	 * TODO javadoc
	 * 
	 * @return
	 */
	public GPSLocation getFocus2() {
		return focus2;
	}

	/**
	 * 
	 * TODO javadoc
	 * 
	 * @return
	 */
	public double getEllipseWidth() {
		return ellipseWidth;
	}

	/**
	 * 
	 * TODO javadoc
	 * 
	 * @return
	 */
	public double getA() {
		return a;
	}

	/**
	 * 
	 * TODO javadoc
	 * 
	 * @return
	 */
	public double getB() {
		return b;
	}
}
