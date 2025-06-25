package org.recommendation.utils;

import org.recommendation.RecommendationGraphService.TrackNode;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.Graph;
import java.util.*;

public class SCCFinder {
    public static List<Set<TrackNode>> findSCCs(Graph<TrackNode, DefaultWeightedEdge> graph) {
        Set<TrackNode> visited = new HashSet<>();
        Deque<TrackNode> order = new ArrayDeque<>();

        for (TrackNode v : graph.vertexSet()) {
            if (!visited.contains(v)) dfs1(graph, v, visited, order);
        }

        Graph<TrackNode, DefaultWeightedEdge> transposed = transpose(graph);

        visited.clear();
        List<Set<TrackNode>> sccs = new ArrayList<>();
        while (!order.isEmpty()) {
            TrackNode v = order.pollLast();
            if (!visited.contains(v)) {
                Set<TrackNode> component = new HashSet<>();
                dfs2(transposed, v, visited, component);
                sccs.add(component);
            }
        }
        return sccs;
    }

    private static void dfs1(Graph<TrackNode, DefaultWeightedEdge> g, TrackNode v, Set<TrackNode> visited, Deque<TrackNode> order) {
        visited.add(v);
        for (DefaultWeightedEdge e : g.outgoingEdgesOf(v)) {
            TrackNode u = g.getEdgeTarget(e);
            if (!visited.contains(u)) dfs1(g, u, visited, order);
        }
        order.addLast(v);
    }

    private static void dfs2(Graph<TrackNode, DefaultWeightedEdge> g, TrackNode v, Set<TrackNode> visited, Set<TrackNode> component) {
        visited.add(v);
        component.add(v);
        for (DefaultWeightedEdge e : g.outgoingEdgesOf(v)) {
            TrackNode u = g.getEdgeTarget(e);
            if (!visited.contains(u)) dfs2(g, u, visited, component);
        }
    }

    private static Graph<TrackNode, DefaultWeightedEdge> transpose(Graph<TrackNode, DefaultWeightedEdge> g) {
        org.jgrapht.graph.SimpleDirectedWeightedGraph<TrackNode, DefaultWeightedEdge> t =
                new org.jgrapht.graph.SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        for (TrackNode v : g.vertexSet()) t.addVertex(v);
        for (DefaultWeightedEdge e : g.edgeSet()) {
            TrackNode src = g.getEdgeSource(e);
            TrackNode tgt = g.getEdgeTarget(e);
            DefaultWeightedEdge edge = t.addEdge(tgt, src);
            if (edge != null) t.setEdgeWeight(edge, g.getEdgeWeight(e));
        }
        return t;
    }
} 