package cz.agents.cycleplanner;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.util.JaccardDistanceCalculator;
import eu.superhub.wp5.wp5common.location.GPSLocation;

public class JaccardDistanceCalculatorTest {
	
	Set<String> yourFriends = Sets.newHashSet(
	        "Desiree Jagger",
	        "Benedict Casteel",
	        "Evon Saddler",
	        "Toby Greenland", 
	        "Norine Caruana",
	        "Felecia Houghton",
	        "Lanelle Franzoni",
	        "Armandina Everitt",
	        "Inger Honea", 
	        "Autumn Hendriks");

	Set<String> myFriends = Sets.newHashSet(
	        "Karrie Rutan",
	        "Desiree Jagger", 
	        "Armandina Everitt",
	        "Arlen Nowacki",
	        "Ward Siciliano",
	        "Mira Yonts",
	        "Marcelo Arab",
	        "Autumn Hendriks",
	        "Mazie Hemstreet",
	        "Toby Greenland");
	
	CycleNode node1 = new CycleNode(1, new GPSLocation(1, 1), "test");
	CycleNode node2 = new CycleNode(2, new GPSLocation(2, 2), "test");
	CycleNode node3 = new CycleNode(3, new GPSLocation(3, 3), "test");
	CycleNode node4 = new CycleNode(4, new GPSLocation(4, 4), "test");
	CycleNode node5 = new CycleNode(5, new GPSLocation(5, 5), "test");
	CycleNode node6 = new CycleNode(6, new GPSLocation(6, 6), "test");
	
	CycleEdge edge1 = new CycleEdge(node1, node2, 0, new HashSet<>(), new HashSet<>(), 0l, 0);
	CycleEdge edge2 = new CycleEdge(node2, node3, 0, new HashSet<>(), new HashSet<>(), 0l, 0);
	CycleEdge edge3 = new CycleEdge(node3, node4, 0, new HashSet<>(), new HashSet<>(), 0l, 0);
	CycleEdge edge4 = new CycleEdge(node4, node5, 0, new HashSet<>(), new HashSet<>(), 0l, 0);
	CycleEdge edge5 = new CycleEdge(node5, node6, 0, new HashSet<>(), new HashSet<>(), 0l, 0);

	Set<CycleEdge> path1 = Sets.newHashSet(edge1, edge3, edge5);
	Set<CycleEdge> path2 = Sets.newHashSet(edge2, edge4);
	
	Set<CycleEdge> path3 = Sets.newHashSet(edge1, edge2, edge3);
	Set<CycleEdge> path4 = Sets.newHashSet(edge3, edge4, edge5);
	
	Set<CycleEdge> path5 = Sets.newHashSet(edge1, edge2, edge3, edge4, edge5);
	
	@Test
	public void test() {
		double jd = JaccardDistanceCalculator.calculate(yourFriends, myFriends);

		assertEquals(0.75, jd, 0d);
	}
	
	@Test
	public void testPaths(){
		double jd = JaccardDistanceCalculator.calculate(path1, path2);
		
		assertEquals(1d, jd, 0d);
		
		jd = JaccardDistanceCalculator.calculate(path3, path4);
		
		assertEquals(0.8, jd, 0d);
		
		jd = JaccardDistanceCalculator.calculate(path5, path5);
		
		assertEquals(0d, jd, 0d);
	}

}
