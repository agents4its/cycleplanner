package cz.agents.cycleplanner.visualisation;

import java.awt.Color;
import java.util.Iterator;
import java.util.regex.Pattern;

import cz.agents.agentpolis.tools.geovisio.layer.visparameter.VisParameterMapper;
import cz.agents.agentpolis.tools.geovisio.layer.visparameter.VisParameters;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.elements.Node;
import eu.superhub.wp5.plannercore.structures.base.TimeDependentEdge;

public class CycleGraphVisParameterMapper implements VisParameterMapper {

	private static final Pattern EXCLUDED_TAGS = Pattern
			.compile("way::bicycle::no|way::access::customers|way::access::delivery|way::access::private|way::access::no");

	@Override
	public VisParameters getVisParameter(Object object) {
		Color color = null;
		double styleParameter = 0;
		if (object instanceof Node) {
			color = getColor((Node) object);
			styleParameter = 7;
		}
		if (object instanceof TimeDependentEdge) {
			color = getColor((TimeDependentEdge) object);
			styleParameter = 1;
		}

		return new VisParameters(color, styleParameter);
	}

	private Color getColor(Node node) {
		if (node.getClass() == CycleNode.class) {
			CycleNode cycleNode = (CycleNode) node;
			if (cycleNode.getTagsJoinedKeyAndValue() != null) {
				for (Iterator<String> it = cycleNode.getTagsJoinedKeyAndValue()
						.iterator(); it.hasNext();) {
					String tag = it.next();

					if (EXCLUDED_TAGS.matcher(tag).matches()) {
						return Color.CYAN;

					} else if (tag.equals("node::junction::out")) {
						return Color.ORANGE;
					} else if (tag.equals("node::junction::in")) {
						return Color.MAGENTA;
					}
				}
			}
		}
		return Color.BLACK;
	}

	private Color getColor(TimeDependentEdge edge) {
		if (edge.getClass() == CycleEdge.class) {
			CycleEdge cycleEdge = (CycleEdge) edge;
			if (cycleEdge.getOSMtags() != null) {
				for (Iterator<String> it = cycleEdge.getOSMtags().iterator(); it
						.hasNext();) {
					
					String tag = it.next();
					
					if (EXCLUDED_TAGS.matcher(tag).matches()) {
						return Color.BLACK;
					}
				}
			}
		}
				
		return Color.RED;
	}

}
