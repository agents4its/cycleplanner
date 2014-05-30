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
package cvut.fel.nemetma1.dataStructures;

import cycle.planner.evaluate.evaluator.EvaluationDetails;
import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;

/**
 * Represents an edge in a cycle route network graph.
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class CycleEdge extends Edge {

    private float rises;
    private float drops;
    CycleNode fromNode;
    CycleNode toNode;
    private Set<String> tagsJoinedEntityKeyAndValue;
    private EvaluationDetails evaluationDetails;
/**
 * creates a CycleEdge defined by fromNode and toNode. 
 * @param fromNode starting node of the edge
 * @param toNode end node of the edge
 * @param lengthInMetres length of the edge in the map
 * @param rises cumulative elevation gain = sum of ascends along the edge
 * @param drops cumulative elevation loss = sum of descends along the edge
 */
    public CycleEdge(CycleNode fromNode, CycleNode toNode, double lengthInMetres, float rises, float drops) {
        super(fromNode.getId(), toNode.getId(), lengthInMetres);
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.rises = rises;
        this.drops = drops;
    }
/**
 * creates a CycleEdge defined by fromNode and toNode. 
 * @param fromNode starting node of the edge
 * @param toNode end node of the edge
 * @param lengthInMetres length of the edge in the map
 * @param rises cumulative elevation gain = sum of ascends along the edge
 * @param drops cumulative elevation loss = sum of descends along the edge
 * @param wayTags OSM tags from the way of which this edge is part of
 * @param relationTags OSM tags from the relation of which this edge is part of
 */
    public CycleEdge(CycleNode fromNode, CycleNode toNode, double lengthInMetres, float rises, float drops, Set<Tag> wayTags, Set<Tag> relationTags) {
        super(fromNode.getId(), toNode.getId(), lengthInMetres);
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.rises = rises;
        this.drops = drops;
        if (wayTags.size() > 0 || fromNode.getTagsJoinedKeyAndValue() != null || relationTags.size() > 0) {
            this.tagsJoinedEntityKeyAndValue = new HashSet<>();
        }
        for (Iterator<Tag> it = wayTags.iterator(); it.hasNext();) {
            Tag tag = it.next();
            this.tagsJoinedEntityKeyAndValue.add("way::" + tag.getKey().concat("::").concat(tag.getValue()));
        }
        for (Iterator<Tag> it = relationTags.iterator(); it.hasNext();) {
            Tag tag = it.next();
            this.tagsJoinedEntityKeyAndValue.add("relation::" + tag.getKey().concat("::").concat(tag.getValue()));
        }
        if (fromNode.getTagsJoinedKeyAndValue() != null) {
            this.tagsJoinedEntityKeyAndValue.addAll(fromNode.getTagsJoinedKeyAndValue());
        }
    }
    /**
     * removes OSM tags in this edge and replaces the collection by null
     */
    public void destroyTags(){
        if(this.tagsJoinedEntityKeyAndValue!=null){
    this.tagsJoinedEntityKeyAndValue.clear();
    this.tagsJoinedEntityKeyAndValue=null;}
    };
/**
 * @return the start node of this edge
 */
    public CycleNode getFromNode() {
        return fromNode;
    }
/**
 * @return OSM tags of the start node of this edge
 */
    public Set<String> getFromNodeTags() {
        return fromNode.getTagsJoinedKeyAndValue();
    }
/**
 * @return OSM tags of the end node of this edge
 */
    public Set<String> getToNodeTags() {
        return toNode.getTagsJoinedKeyAndValue();
    }
/**
 * @return the end node of this edge
 */
    public CycleNode getToNode() {
        return toNode;
    }
/**
 * returns associated OSM tags as Strings in format entity::key::value, where entity can be relation, way or node
 * @return Set of associated OSM tags
 */
    public Set<String> getOSMtags() {
        return tagsJoinedEntityKeyAndValue;
    }
/**
 * @return cumulative elevation gain of this edge
 */
    public float getRises() {
        return rises;
    }
/**
 * 
 * @param rises cumulative elevation gain of this edge
 */
    public void setRises(float rises) {
        this.rises = rises;
    }
/**
 * 
 * @return cumulative elevation loss of this edge
 */
    public float getDrops() {
    	if (drops == 0) return drops;
        return -drops;
    }
/**
 * 
 * @param drops cumulative elevation loss of this edge
 */
    public void setDrops(float drops) {
        this.drops = drops;
    }
/**
 * returns evaluation details for the specified aspect, if they have been pre-calculated for this edge and saved. If not returns null.
 * @param aspect
 * @return EvaluationDetails or null
 */
//    public EvaluationDetails getEvaluationDetails(Aspect aspect) {
//        if (evaluationDetails == null) {
//            return null;
//        }
//        return evaluationDetails.get(aspect.getClass());
//    }
    public EvaluationDetails getEvaluationDetails() {
    	return evaluationDetails;
    }
/**
 * sets evaluation details for this edge and aspect
 * @param aspect 
 * @param evaluationDetails
 */
//    public void setEvaluationDetails(Aspect aspect, EvaluationDetails evaluation) {
//        if (evaluationDetails == null) {
//            evaluationDetails = new HashMap<>();
//        }
//        evaluationDetails.put(aspect.getClass(), evaluation);
//    }
	public void setEvaluationDetails(EvaluationDetails evaluationDetails) {
		
		this.evaluationDetails = evaluationDetails;
	}
}
