package org.recommendation;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.junit.jupiter.api.Test;
import org.recommendation.RecommendationGraphService.TrackNode;
import org.recommendation.utils.SCCFinder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SCCFinderTest {
    @Test
    void testSingleSCC() {
        var g = new SimpleDirectedWeightedGraph<TrackNode, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        var n1 = new TrackNode("1", "A", "Rock", "X");
        var n2 = new TrackNode("2", "B", "Rock", "X");
        g.addVertex(n1); g.addVertex(n2);
        g.addEdge(n1, n2); g.addEdge(n2, n1);
        var sccs = SCCFinder.findSCCs(g);
        assertEquals(1, sccs.size());
        assertTrue(sccs.get(0).containsAll(Set.of(n1, n2)));
    }

    @Test
    void testTwoSCCs() {
        var g = new SimpleDirectedWeightedGraph<TrackNode, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        var n1 = new TrackNode("1", "A", "Rock", "X");
        var n2 = new TrackNode("2", "B", "Rock", "X");
        var n3 = new TrackNode("3", "C", "Pop", "Y");
        g.addVertex(n1); g.addVertex(n2); g.addVertex(n3);
        g.addEdge(n1, n2); g.addEdge(n2, n1); // SCC1
        // n3 — отдельная SCC
        var sccs = SCCFinder.findSCCs(g);
        assertEquals(2, sccs.size());
        assertTrue(sccs.stream().anyMatch(scc -> scc.containsAll(Set.of(n1, n2))));
        assertTrue(sccs.stream().anyMatch(scc -> scc.contains(n3) && scc.size() == 1));
    }

    @Test
    void testCycleAndIsolated() {
        var g = new SimpleDirectedWeightedGraph<TrackNode, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        var n1 = new TrackNode("1", "A", "Rock", "X");
        var n2 = new TrackNode("2", "B", "Rock", "X");
        var n3 = new TrackNode("3", "C", "Pop", "Y");
        g.addVertex(n1); g.addVertex(n2); g.addVertex(n3);
        g.addEdge(n1, n2); g.addEdge(n2, n1); // цикл
        // n3 — изолированная вершина
        var sccs = SCCFinder.findSCCs(g);
        assertEquals(2, sccs.size());
        assertTrue(sccs.stream().anyMatch(scc -> scc.containsAll(Set.of(n1, n2))));
        assertTrue(sccs.stream().anyMatch(scc -> scc.contains(n3) && scc.size() == 1));
    }

    @Test
    void testLinearGraph() {
        var g = new SimpleDirectedWeightedGraph<TrackNode, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        var n1 = new TrackNode("1", "A", "Rock", "X");
        var n2 = new TrackNode("2", "B", "Rock", "X");
        var n3 = new TrackNode("3", "C", "Pop", "Y");
        g.addVertex(n1); g.addVertex(n2); g.addVertex(n3);
        g.addEdge(n1, n2); g.addEdge(n2, n3);
        var sccs = SCCFinder.findSCCs(g);
        // Все вершины — отдельные SCC
        assertEquals(3, sccs.size());
        assertTrue(sccs.stream().allMatch(scc -> scc.size() == 1));
    }

    @Test
    void testBigSCC() {
        var g = new SimpleDirectedWeightedGraph<TrackNode, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        TrackNode[] nodes = new TrackNode[5];
        for (int i = 0; i < 5; i++) {
            nodes[i] = new TrackNode(String.valueOf(i), "T"+i, "G", "A");
            g.addVertex(nodes[i]);
        }
        for (int i = 0; i < 5; i++) {
            g.addEdge(nodes[i], nodes[(i+1)%5]); // цикл
        }
        var sccs = SCCFinder.findSCCs(g);
        assertEquals(1, sccs.size());
        assertEquals(5, sccs.get(0).size());
    }
} 