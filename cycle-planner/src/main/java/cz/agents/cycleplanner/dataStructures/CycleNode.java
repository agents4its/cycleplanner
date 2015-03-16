package cz.agents.cycleplanner.dataStructures;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.openstreetmap.osmosis.core.domain.v0_6.Tag;

import eu.superhub.wp5.graphcommon.graph.elements.Node;
import eu.superhub.wp5.wp5common.location.GPSLocation;


/**
 * Represents a node in the cycling route network graph.
 *
 * @author Marcel Német <marcel.nemet@gmail.com>
 * @author Pavol Zilecky <pavol.zilecky@agents.fel.cvut.cz>
 */
public class CycleNode extends Node {

    private Set<String> tagsJoinedKeyAndValue;

    /**
     * creates a node with specified parameters
     *
     * @param id
     * @param gpsLocation location i the map
     * @param description description
     * @param elevation altitude of the node
     * @param nodeTags OSM tags of this node
     */
    @SuppressWarnings("rawtypes")
    public CycleNode(long id, GPSLocation gpsLocation, String description, Set nodeTags) {
        super(id, gpsLocation, description);
        if ( tagsJoinedKeyAndValue == null) {
            this.tagsJoinedKeyAndValue = new HashSet<>();
        }
        
        for (Iterator it = nodeTags.iterator(); it.hasNext();) {
        	Object o = it.next();
        	if (o instanceof String) {
				String tag = (String) o;
				this.tagsJoinedKeyAndValue.add(tag);				
			} else if (o instanceof Tag) {
				Tag tag = (Tag) o;
				this.tagsJoinedKeyAndValue.add("node::" + tag.getKey().concat("::").concat(tag.getValue()));
			}          
            
        }
    }
    

    /**
     * creates a node with specified parameters
     *
     * @param id
     * @param gpsLocation location i the map
     * @param description description
     * @param elevation altitude of the node
     */
    public CycleNode(long id, GPSLocation gpsLocation, String description) {
        super(id, gpsLocation, description);
    }

    /**
     * Returns Set of Strings in format "node::key::value" or null if there are no tags.
     *
     * @return set of Strings in format "node::key::value"
     */
    public Set<String> getTagsJoinedKeyAndValue() {
        return tagsJoinedKeyAndValue;
    }

    /**
     * clears all tags for this node
     */
    public void destroyTags() {
        if (this.tagsJoinedKeyAndValue != null) {
            this.tagsJoinedKeyAndValue.clear();
            this.tagsJoinedKeyAndValue = null;
        }
    }

    @Override
    public int hashCode() {
        return Long.valueOf(this.getId()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CycleNode other = (CycleNode) obj;
        if (this.getId() != other.getId()) {
            return false;
        }
        return true;
    }
}
