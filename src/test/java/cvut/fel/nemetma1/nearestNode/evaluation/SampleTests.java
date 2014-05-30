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
package cvut.fel.nemetma1.nearestNode.evaluation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.junit.Test;

import cvut.fel.nemetma1.dataStructures.CycleEdge;
import cvut.fel.nemetma1.routingService.RoutingService;

/**
 * Class for evaluation tests. Runs 500 sample tests for each aspect and profile and saves results into .csv files.
 *
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class SampleTests {

    final static double LEFT = 14.323425;
    final static double RIGHT = 14.567871;
    final static double TOP = 50.146546;
    final static double BOTTOM = 50.020094;
    private static final Pattern FOR_BICYCLES_PATTERN = Pattern.compile("relation::route::bicycle|way::bicycle::designated|way::bicycle::permissive|"
            + "way::bicycle::yes|way::cycleway::lane|way::cycleway::share_busway|way::cycleway::shared_lane|way::cycleway::track|way::cycleway:left::lane|"
            + "way::cycleway:left::share_busway|way::cycleway:left::shared_lane|way::cycleway:right::lane|way::cycleway:right::share_busway|"
            + "way::cycleway:right::shared_lane|way::highway::cycleway");

    /**
     * Runs 500 sample tests for each aspect and profile and saves results into .csv files.
     */
    public static void main(String[] args) {
        ArrayList<PrintStream> printStreams = new ArrayList<>();
        try {
        	long currentime = System.currentTimeMillis();
            RoutingService r = RoutingService.INSTANCE;
            System.out.println("Is EPSGProjection initialized? " + (r.projection!=null) ); 
            
            // It seems to that aima library first search is much slower than next ones. That's why, we run unimportant search during the initialization.  
            long time = System.currentTimeMillis();
            r.findRouteJOSMLatLonWithAspectsReturnEdges(TOP, LEFT, BOTTOM, RIGHT, 5, 1, 0, 0, 0);
            System.out.println("************************");
            System.out.println("First planning: "+(System.currentTimeMillis() - time)+" ms");
            
            System.out.println("Initialization time: "+ (System.currentTimeMillis() - currentime)+"\n");
            
            System.out.println("Push arbitrary button");
            Scanner sc = new Scanner(System.in);
            sc.nextLine();
            ArrayList<Long> times = new ArrayList<>();
            Random random = new Random(103L);
            for (int i = 1; i <= 1; i++) {
                double lon1 = LEFT + (RIGHT - LEFT) * random.nextDouble();
                double lat1 = BOTTOM + (TOP - BOTTOM) * random.nextDouble();
                double lon2 = LEFT + (RIGHT - LEFT) * random.nextDouble();
                double lat2 = BOTTOM + (TOP - BOTTOM) * random.nextDouble();
                System.out.println("lon1 " + lon1);
                System.out.println("lat1 " + lat1);
                System.out.println("lon2 " + lon2);
                System.out.println("lat2 " + lat2);
                //cumputation times
//                ArrayList<Long> times = new ArrayList<>();

                currentime = System.currentTimeMillis();
                Collection<CycleEdge> path1 = r.findRouteJOSMLatLonWithAspectsReturnEdges(lat1, lon1, lat2, lon2, 5, 1, 0, 0, 0);
                times.add(System.currentTimeMillis() - currentime);
                
                String json = r.findRouteJOSMLatLonWithAspects(50.03182028516504, 14.336352565367896, 50.14005551450914, 14.458188563653868, 3.8, 1, 0, 0, 0);
                System.out.println("Fast 38: "+json);
                json = r.findRouteJOSMLatLonWithAspects(50.03182028516504, 14.336352565367896, 50.14005551450914, 14.458188563653868, 3.8, 1, 3, 3, 6);
                System.out.println("Peaceful 38: "+json);
//                json = r.findRouteJOSMLatLonWithAspects(50.03182028516504, 14.336352565367896, 50.14005551450914, 14.458188563653868, 3.8, 2, 1, 1, 1);
//                System.out.println("Commuting 38: "+json);
//                json = r.findRouteJOSMLatLonWithAspects(50.03182028516504, 14.336352565367896, 50.14005551450914, 14.458188563653868, 3.8, 1, 1, 3, 0);
//                System.out.println("Quiet 38: "+json);
//                json = r.findRouteJOSMLatLonWithAspects(50.03182028516504, 14.336352565367896, 50.14005551450914, 14.458188563653868, 3.8, 1, 0, 0, 6);
//                System.out.println("Flat 38: "+json);
//                
//                
//                json = r.findRouteJOSMLatLonWithAspects(50.13521726752461, 14.408953510482288, 50.02250718104671, 14.360998204644465, 3.8, 1, 0, 0, 0);
//                System.out.println("Fast 29: "+json);
//                json = r.findRouteJOSMLatLonWithAspects(50.13521726752461, 14.408953510482288, 50.02250718104671, 14.360998204644465, 3.8, 1, 3, 3, 6);
//                System.out.println("Peaceful 29: "+json);
//                json = r.findRouteJOSMLatLonWithAspects(50.13521726752461, 14.408953510482288, 50.02250718104671, 14.360998204644465, 3.8, 2, 1, 1, 1);
//                System.out.println("Commuting 29: "+json);
//                json = r.findRouteJOSMLatLonWithAspects(50.13521726752461, 14.408953510482288, 50.02250718104671, 14.360998204644465, 3.8, 1, 1, 3, 0);
//                System.out.println("Quiet 29: "+json);
//                json = r.findRouteJOSMLatLonWithAspects(50.13521726752461, 14.408953510482288, 50.02250718104671, 14.360998204644465, 3.8, 1, 0, 0, 6);
//                System.out.println("Flat 29: "+json);
               
                
//                currentime = System.currentTimeMillis();
//                Collection<CycleEdge> path2 = r.findRouteJOSMLatLonWithAspectsReturnEdges(lat1, lon1, lat2, lon2, 5, 0, 1, 0, 0);
//                times.add(System.currentTimeMillis() - currentime);              
//                currentime = System.currentTimeMillis();
//                Collection<CycleEdge> path3 = r.findRouteJOSMLatLonWithAspectsReturnEdges(lat1, lon1, lat2, lon2, 5, 0, 0, 1, 0);
//                times.add(System.currentTimeMillis() - currentime);
//                currentime = System.currentTimeMillis();
//                Collection<CycleEdge> path4 = r.findRouteJOSMLatLonWithAspectsReturnEdges(lat1, lon1, lat2, lon2, 5, 0, 0, 0, 1);
//                times.add(System.currentTimeMillis() - currentime);
//                currentime = System.currentTimeMillis();
//                Collection<CycleEdge> path5 = r.findRouteJOSMLatLonWithAspectsReturnEdges(lat1, lon1, lat2, lon2, 5, 2, 1, 0, 0);
//                times.add(System.currentTimeMillis() - currentime);
//                currentime = System.currentTimeMillis();
//                Collection<CycleEdge> path6 = r.findRouteJOSMLatLonWithAspectsReturnEdges(lat1, lon1, lat2, lon2, 5, 0.5, 3, 6, 0);
//                times.add(System.currentTimeMillis() - currentime);
//                currentime = System.currentTimeMillis();
//                Collection<CycleEdge> path7 = r.findRouteJOSMLatLonWithAspectsReturnEdges(lat1, lon1, lat2, lon2, 5, 3, 0, 0, 1);
//                times.add(System.currentTimeMillis() - currentime);
                ArrayList<Collection<CycleEdge>> list = new ArrayList<>();
//                list.add(path1);
//                list.add(path2);
//                list.add(path3);
//                list.add(path4);
//                list.add(path5);
//                list.add(path6);
//                list.add(path7);
                int fileint = 1;

                for (Collection<CycleEdge> path : list) {
                    if (i == 1) {
                        PrintStream c;
                        File csv = new File("outputCSV_Aspect_withTime" + fileint + ".csv");
//                        File csv = new File("outputCSV_Aspect_withTime" + fileint + "_projected.csv");
//                        System.out.println(csv.getAbsolutePath());
                        c = new PrintStream(csv);
                        printStreams.add(c);
                        c.println("iteration,elevation,from lat,from lon,to lat, to lon,"
                                + "number of path chunks,"
                                + "route length in m,route rises in m,route drops in m,length on bicycle ways,computation time in ms");
                    }
                    PrintStream c = printStreams.get(fileint - 1);
                    fileint++;
                    double routeLength = 0;
                    double routeRise = 0;
                    double routeDrops = 0;
                    double bicyleLength = 0;

                    for (Iterator<CycleEdge> it = path.iterator(); it.hasNext();) {
                        CycleEdge edge = it.next();
                        routeLength += edge.getLengthInMetres();
                        routeRise += edge.getRises();
                        routeDrops += edge.getDrops();
                        boolean isbicycle = false;
                        
                        /**
                         * this will not work if the graph is loaded from precomputed file which has OSM tags stripped. 
                         * in cvut.fel.nemetma1.routingService.resources/config.properties file 
                         * set strip_tags to false, and recreate_graph to true
                         */
                        if (edge.getOSMtags() != null) {
                            for (String edgeTag : edge.getOSMtags()) {
                                if (FOR_BICYCLES_PATTERN.matcher(edgeTag).matches()) {
                                    isbicycle = true;
                                    break;
                                }
                            }
                        }
                        if (isbicycle) {
                            bicyleLength += edge.getLengthInMetres();
                        }
                    }

//                    System.out.print(i + ",");
//                    System.out.print("yes,");
//                    System.out.print(lat1 + "," + lon1 + "," + lat2 + "," + lon2 + ",");
//                    System.out.print(path.size() + ",");
//                    System.out.print(routeLength + ",");
//                    System.out.print(routeRise + ",");
//                    System.out.print(routeDrops + ",");
//                    System.out.print(bicyleLength + ",");
//                    System.out.print(times.get(fileint - 2) + ",");
//                    System.out.println("");
//                    System.out.println("c " + c);
//                    System.out.println("priting " + i);


                    c.print(i + ",");
                    c.print("yes,");
                    c.print(lat1 + "," + lon1 + "," + lat2 + "," + lon2 + ",");
                    c.print(path.size() + ",");
                    c.print(routeLength + ",");
                    c.print(routeRise + ",");
                    c.print(routeDrops + ",");
                    c.print(bicyleLength + ",");
                    c.print(times.get(fileint - 2) + ",");
                    c.println("");
                    if ((i % 10) == 0) {
//                        System.out.println("i " + i + " -flush");
                        c.flush();
                    }

                }              
            }
            
            for (PrintStream p : printStreams) {
                p.close();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SampleTests.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            for (Iterator<PrintStream> it = printStreams.iterator(); it.hasNext();) {
                PrintStream p = it.next();
                p.close();
            }
        }
    }
}
