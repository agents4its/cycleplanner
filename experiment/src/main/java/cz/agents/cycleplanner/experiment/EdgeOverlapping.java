package cz.agents.cycleplanner.experiment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.EdgeId;
import eu.superhub.wp5.graphcommon.graph.elements.Node;

public class EdgeOverlapping {
	
	private static Logger log = Logger.getLogger(EdgeOverlapping.class);

	private Map<EdgeId, CycleEdge> edges;
	private Map<EdgeId, Double> widths;
	private Map<EdgeId, String> colours;

	private CycleNode origin = null;
	private CycleNode destination = null;

	public EdgeOverlapping() {
	}
	
	public void neviemAkoPomenovat(CycleNode origin, CycleNode destination, Collection<Collection<CycleEdge>> paretoSet, File file) {

		this.origin = origin;
		this.destination = destination;
		
		edges = new HashMap<EdgeId, CycleEdge>();
		widths = new HashMap<EdgeId, Double>();
		colours = new HashMap<EdgeId, String>();
		
		for (Collection<CycleEdge> journey : paretoSet) {
			
			for (CycleEdge edge : journey) {
				
				EdgeId edgeId = edge.getEdgeId();
				
				edges.put(edgeId, edge);
				
				if (widths.containsKey(edgeId)) {
					widths.put(edgeId, widths.get(edgeId) + 1);
				} else {
					widths.put(edgeId, 1d);
				}
			}			
		}
		
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;

		for (EdgeId id : widths.keySet()) {
			double value = widths.get(id);
			max = Math.max(value, max);
			min = Math.min(value, min);
		}

		for (EdgeId id : widths.keySet()) {
			// spocitam tak hrubku aby pre najmenej prekryvanu hranu bola
			// hrubka nula a pre najviac zase 12 (najvacsia zvolena hrubka
			// ciary)
			double width = widths.get(id);
			widths.put(id, ((width - min) / (max - min)) * 12);

			// spocitam odtien cervenej na stupncic od 150-255, teda
			// najmenej prekryvana bude mat najtmavsiu farbu a najviac
			// prekryvana najbledsiu

			int colour = (int) (150 + ((width - min) / (max - min)) * 105);
			colours.put(id, "#" + Integer.toHexString(colour) + "0000");
			// log.debug("Colour: #" + Integer.toHexString(colour) +
			// "0000");
		}
		
		save(file);
	}

	private void save(File file) {
		log.info("Saving...");
		
		try {
			
			PrintStream c = new PrintStream(file);
			
			c.print("{ \"markers\":{\"origin\":[");
			c.print(origin.getLongitude());
			c.print(", ");
			c.print(origin.getLatitude());
			c.print("], \"destination\":[");
			c.print(destination.getLongitude());
			c.print(", ");
			c.print(destination.getLatitude());
			c.print("]}, \"edges\":[");
			
			int j = 0;
			
			for (EdgeId id : widths.keySet()) {
				Node from = edges.get(id).getFromNode();
				Node to = edges.get(id).getToNode();
				
				c.print("{\"from\":[");
				c.print(from.getLongitude());
				c.print(", ");
				c.print(from.getLatitude());
				c.print("], \"to\":[");
				c.print(to.getLongitude());
				c.print(", ");
				c.print(to.getLatitude());
				c.print("], \"overlaps\":");
				c.print(widths.get(id));
				c.print(", \"colour\":\"");
				c.print(colours.get(id));
				c.print("\"}");

				if (++j < widths.size()) {
					c.print(", ");
				}
			}
			c.print("]}");
			c.flush();
			c.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
