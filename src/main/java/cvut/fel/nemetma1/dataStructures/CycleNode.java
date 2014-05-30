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

import eu.superhub.wp5.graphcommon.graph.elements.Node;
import eu.superhub.wp5.wp5common.GPSLocation;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;

/**
 * Represents a node in the cycling route network graph.
 *
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class CycleNode extends Node {

    private float elevation;
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
    public CycleNode(long id, GPSLocation gpsLocation, String description, float elevation, Set<Tag> nodeTags) {
        super(id, gpsLocation, description);
        this.elevation = elevation;
        if (nodeTags.size() > 0 && tagsJoinedKeyAndValue == null) {
            this.tagsJoinedKeyAndValue = new HashSet<>();
        }
        for (Iterator<Tag> it = nodeTags.iterator(); it.hasNext();) {
            Tag tag = it.next();
            this.tagsJoinedKeyAndValue.add("node::" + tag.getKey().concat("::").concat(tag.getValue()));
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
    public CycleNode(long id, GPSLocation gpsLocation, String description, float elevation) {
        super(id, gpsLocation, description);
        this.elevation = elevation;
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
/**
 * @return elevation(altitude) of the node
 */
    public float getElevation() {
        return elevation;
    }
/**
 * @param elevation elevation(altitude) of the node
 */
    public void setElevation(float elevation) {
        this.elevation = elevation;
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
