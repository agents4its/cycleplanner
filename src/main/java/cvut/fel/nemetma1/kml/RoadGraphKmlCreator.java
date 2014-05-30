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

package cvut.fel.nemetma1.kml;

import cvut.fel.nemetma1.dataStructures.CycleEdge;
import cvut.fel.nemetma1.dataStructures.CycleNode;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Link;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Style;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import eu.superhub.wp5.wp5common.GPSLocation;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RoadGraphKmlCreator {
/**
 * creates a KML file visualising a graph
 * @param graph graph to visualise
 * @param graphProperties 
 * @param resultFile file to write the KML string
 * @throws FileNotFoundException 
 */
    public static void createKml(Graph<CycleNode, CycleEdge> graph, List<GraphProperty> graphProperties, File resultFile) throws FileNotFoundException {


        Kml out = new Kml();
        Document document = out.createAndSetDocument();

        Folder overlay = document.createAndAddFolder();
        overlay.withName("OSM overlay");
        Link link = new Link();
        link.withHref("http://mt.mgmaps.com/kml/maps.php");
        link.withHttpQuery("mt=osm_m&amp;v=1.7&amp;dx=0&amp;dy=0&amp;trans=0");
        overlay.createAndAddNetworkLink().withLink(link);


        for (GraphProperty graphProperty : graphProperties) {

            Folder folder = document.createAndAddFolder();
            folder.setName(graphProperty.name);


            Color graphColor = graphProperty.graphColor;
            Collection<CycleEdge> edges = graph.getAllEdges();

            List<List<Coordinate>> coordinates = new ArrayList<>();

            for (Edge edge : edges) {
                List<Coordinate> coordinatesInner = new ArrayList<>();

                GPSLocation fromNode = graph.getNodeByNodeId(edge.getFromNodeId()).getGpsLocation();
                GPSLocation toNode = graph.getNodeByNodeId(edge.getToNodeId()).getGpsLocation();

                coordinatesInner.add(new Coordinate(fromNode.getLongitude(), fromNode.getLatitude()));
                coordinatesInner.add(new Coordinate(toNode.getLongitude(), toNode.getLatitude()));

                coordinates.add(coordinatesInner);
            }

            for (List<Coordinate> coordinateInner : coordinates) {

                Placemark p = new Placemark();

                p.createAndSetLineString().withTessellate(true).withCoordinates(coordinateInner);


                Style style = p.createAndAddStyle();
                style.createAndSetLineStyle().withWidth(2.0).withColor(colorToKmlColor(graphColor, graphProperty.transparencyInPercent));

                folder.addToFeature(p);

            }

        }

        out.marshal(resultFile);

    }


    public static String colorToKmlColor(Color color, double transparencyInPercent) {
        return String.format("#%02x%02x%02x%02x", ((int) (color.getAlpha() * transparencyInPercent)), color.getBlue(), color.getGreen(), color.getRed());
    }

    public static class GraphProperty {

        public final String name;
        public final Color graphColor;
        public final double widthOfEdge;
        public final double transparencyInPercent;

        public GraphProperty(String name,  Color graphColor, double widthOfEdge,
                double opacityInPercent) {
            super();
            this.name = name;
            this.graphColor = graphColor;
            this.widthOfEdge = widthOfEdge;
            this.transparencyInPercent = opacityInPercent;
        }
    }
}
