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
package cvut.fel.nemetma1.graphCreator;

import cvut.fel.nemetma1.dataStructures.CycleEdge;
import cvut.fel.nemetma1.dataStructures.CycleNode;
import cvut.fel.nemetma1.plannerDataImporterExtensions.taskImpl.CycleWithHighwaysImportTaskImpl;
import cvut.fel.nemetma1.plannerDataImporterExtensions.RelationAndWayOsmImporter;
import cvut.fel.nemetma1.plannerDataImporterExtensions.osmBinder.RelationAndWayBicycleGraphOsmBinder;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.plannerdataimporter.graphimporter.OsmDataGetter;
import eu.superhub.wp5.plannerdataimporter.graphimporter.evaluator.graph.PermittedModeEvaluator;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * This class provides a graph that contains a network of routes for bicycle trip planner.
 *
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class HighwayGraphProvider extends GraphProviderAbstract {


    public HighwayGraphProvider(File graphOSMFile, File graphObjectFile) {
        super(graphOSMFile, graphObjectFile);
    }


//    public HighwayGraphProvider(File graphOSMFile, File graphObjectFile, List<Aspect> aspects) {
//        super(graphOSMFile, graphObjectFile, aspects);
//    }

  
    public HighwayGraphProvider(File graphOSMFile) {
        super(graphOSMFile);
    }


@Override
    protected Graph<CycleNode, CycleEdge> createGraph() {
        System.out.println("creating graph");
        List<PermittedModeEvaluator> graphEvaluators = Arrays.asList(
                RelationAndWayBicycleGraphOsmBinder.getPermittedModeEvaluator());
        //@todo
        File f = graphOSMFile;
        System.out.println(f.getAbsolutePath());
        OsmDataGetter osmDataGetter = OsmDataGetter.createOsmDataGetter(f);
        RelationAndWayOsmImporter importer = new RelationAndWayOsmImporter(osmDataGetter);
        Graph<CycleNode, CycleEdge> graph;
        graph = importer.executeTaskForWayAndRelation(new CycleWithHighwaysImportTaskImpl(50, graphEvaluators),
                //@todo
                RelationAndWayBicycleGraphOsmBinder.getSelector());
        System.out.println("Sinked in " + graph.getAllNodes().size() + " nodes and " + graph.getAllEdges().size() + " edges");

        //@Todo only for testing purposes
        graph = getLargestStronglyConnectedComponent(graph);
        // TODO improve graph intersections
        System.out.println("Final strongly connected component has " + graph.getAllNodes().size() + " nodes and " + graph.getAllEdges().size() + " edges");
        System.out.println("evaluating edges");
        evaluateEdges(graph);
        if (destroyTags) {
            destroyTags(graph);
        }


        return graph;
    }
}
