package org.recommendation;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.springframework.stereotype.Service;
import org.recommendation.utils.SCCFinder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.io.InputStream;

import java.util.*;

@Service
public class RecommendationGraphService {
    public static class TrackNode {
        public final String id;
        public final String title;
        public final String genre;
        public final String artist;
        public TrackNode(String id, String title, String genre, String artist) {
            this.id = id;
            this.title = title;
            this.genre = genre;
            this.artist = artist;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TrackNode trackNode = (TrackNode) o;
            return Objects.equals(id, trackNode.id);
        }
        @Override
        public int hashCode() { return Objects.hash(id); }
    }

    private final SimpleWeightedGraph<TrackNode, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
    private final Map<String, TrackNode> trackMap = new HashMap<>();
    private final Map<String, Set<String>> userLikes = new HashMap<>();
    private final Map<String, Set<String>> userDislikes = new HashMap<>();

    public void addTrack(String id, String title, String genre, String artist) {
        TrackNode node = new TrackNode(id, title, genre, artist);
        graph.addVertex(node);
        trackMap.put(id, node);
    }

    public void addEdgeIfNeeded(String id1, String id2, double initialWeight) {
        if (id1.equals(id2)) return;
        TrackNode n1 = trackMap.get(id1);
        TrackNode n2 = trackMap.get(id2);
        if (n1 == null || n2 == null) return;
        DefaultWeightedEdge edge = graph.getEdge(n1, n2);
        if (edge == null) {
            edge = graph.addEdge(n1, n2);
            graph.setEdgeWeight(edge, initialWeight);
        }
    }

    public void buildInitialEdges() {
        for (TrackNode t1 : graph.vertexSet()) {
            for (TrackNode t2 : graph.vertexSet()) {
                if (t1.id.equals(t2.id)) continue;
                double weight = 0;
                if (t1.genre != null && t1.genre.equalsIgnoreCase(t2.genre)) weight += 1;
                if (t1.artist != null && t1.artist.equalsIgnoreCase(t2.artist)) weight += 1;
                if (weight > 0) addEdgeIfNeeded(t1.id, t2.id, weight);
            }
        }
    }

    public void likeTrack(String userId, String trackId) {
        userLikes.computeIfAbsent(userId, k -> new HashSet<>()).add(trackId);
        // Увеличиваем веса связей с другими лайкнутыми треками
        for (String otherId : userLikes.get(userId)) {
            if (!otherId.equals(trackId)) {
                addEdgeIfNeeded(trackId, otherId, 1);
                DefaultWeightedEdge edge = graph.getEdge(trackMap.get(trackId), trackMap.get(otherId));
                if (edge != null) graph.setEdgeWeight(edge, graph.getEdgeWeight(edge) + 1);
            }
        }
    }

    public void dislikeTrack(String userId, String trackId) {
        userDislikes.computeIfAbsent(userId, k -> new HashSet<>()).add(trackId);
        // Уменьшаем веса связей с другими лайкнутыми треками
        for (String otherId : userLikes.getOrDefault(userId, Set.of())) {
            if (!otherId.equals(trackId)) {
                DefaultWeightedEdge edge = graph.getEdge(trackMap.get(trackId), trackMap.get(otherId));
                if (edge != null) graph.setEdgeWeight(edge, Math.max(0, graph.getEdgeWeight(edge) - 1));
            }
        }
    }

    public List<TrackNode> recommend(String userId, int limit) {
        Set<String> liked = userLikes.getOrDefault(userId, Set.of());
        Set<String> disliked = userDislikes.getOrDefault(userId, Set.of());
        Set<TrackNode> result = new HashSet<>();
        Map<TrackNode, Double> scoreMap = new HashMap<>();
        // BFS глубины 2 от топ-10 лайкнутых
        List<TrackNode> likedNodes = liked.stream().map(trackMap::get).filter(Objects::nonNull).limit(10).toList();
        for (TrackNode node : likedNodes) {
            Set<TrackNode> visited = new HashSet<>();
            Queue<TrackNode> queue = new ArrayDeque<>();
            queue.add(node);
            int depth = 0;
            while (!queue.isEmpty() && depth < 2) {
                int size = queue.size();
                for (int i = 0; i < size; i++) {
                    TrackNode current = queue.poll();
                    for (DefaultWeightedEdge edge : graph.edgesOf(current)) {
                        TrackNode neighbor = graph.getEdgeSource(edge).equals(current) ? graph.getEdgeTarget(edge) : graph.getEdgeSource(edge);
                        if (visited.contains(neighbor) || liked.contains(neighbor.id) || disliked.contains(neighbor.id)) continue;
                        scoreMap.merge(neighbor, graph.getEdgeWeight(edge), Double::sum);
                        queue.add(neighbor);
                        visited.add(neighbor);
                    }
                }
                depth++;
            }
        }
        return scoreMap.entrySet().stream()
                .sorted(Map.Entry.<TrackNode, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }

    public List<TrackNode> randomWalkRecommend(String userId, int limit, int depth, int walks) {
        Set<String> liked = userLikes.getOrDefault(userId, Set.of());
        Set<String> disliked = userDislikes.getOrDefault(userId, Set.of());
        List<TrackNode> likedNodes = liked.stream().map(trackMap::get).filter(Objects::nonNull).limit(10).toList();
        Map<TrackNode, Integer> visitCounts = new HashMap<>();
        Random random = new Random();
        for (TrackNode start : likedNodes) {
            for (int w = 0; w < walks; w++) {
                TrackNode current = start;
                Set<TrackNode> visited = new HashSet<>();
                for (int d = 0; d < depth; d++) {
                    List<DefaultWeightedEdge> edges = new ArrayList<>(graph.edgesOf(current));
                    if (edges.isEmpty()) break;
                    // Выбираем случайное ребро с вероятностью, пропорциональной весу
                    double totalWeight = edges.stream().mapToDouble(graph::getEdgeWeight).sum();
                    double r = random.nextDouble() * totalWeight;
                    double acc = 0;
                    DefaultWeightedEdge chosen = null;
                    for (DefaultWeightedEdge edge : edges) {
                        acc += graph.getEdgeWeight(edge);
                        if (acc >= r) { chosen = edge; break; }
                    }
                    if (chosen == null) break;
                    TrackNode neighbor = graph.getEdgeSource(chosen).equals(current) ? graph.getEdgeTarget(chosen) : graph.getEdgeSource(chosen);
                    if (visited.contains(neighbor) || liked.contains(neighbor.id) || disliked.contains(neighbor.id)) break;
                    visitCounts.merge(neighbor, 1, Integer::sum);
                    current = neighbor;
                    visited.add(neighbor);
                }
            }
        }
        return visitCounts.entrySet().stream()
                .sorted(Map.Entry.<TrackNode, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }

    public List<TrackNode> recommendWithSCC(String userId, int limit) {
        Set<String> liked = userLikes.getOrDefault(userId, Set.of());
        Set<String> disliked = userDislikes.getOrDefault(userId, Set.of());
        if (liked.size() <= 1) {
            // Мало лайков — ищем SCC для всех треков
            List<Set<TrackNode>> sccs = SCCFinder.findSCCs(graph);
            Set<TrackNode> userScc = null;
            if (liked.isEmpty()) {

                userScc = sccs.stream().max(Comparator.comparingInt(Set::size)).orElse(Set.of());
            } else {
                TrackNode likedNode = trackMap.get(liked.iterator().next());
                for (Set<TrackNode> scc : sccs) {
                    if (scc.contains(likedNode)) { userScc = scc; break; }
                }
            }
            if (userScc == null) return List.of();
            return userScc.stream()
                    .filter(t -> liked.stream().noneMatch(id -> id.equals(t.id)))
                    .filter(t -> disliked.stream().noneMatch(id -> id.equals(t.id)))
                    .limit(limit)
                    .toList();
        } else {
            // Обычные рекомендации
            return recommend(userId, limit);
        }
    }

    /**
     * Загрузка треков из JSON-файла
     */
    public void loadTracksFromJson(String resourcePath) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (is == null) throw new RuntimeException("Resource not found: " + resourcePath);
            JsonObject root = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();
            JsonArray tracks = root.getAsJsonArray("tracks");
            if (tracks != null) {
                for (var elem : tracks) {
                    JsonObject node = elem.getAsJsonObject();
                    String id = node.get("id").getAsString();
                    String title = node.get("name").getAsString();
                    String genre = node.getAsJsonArray("genres").size() > 0 ? node.getAsJsonArray("genres").get(0).getAsString() : null;
                    String artist = node.getAsJsonArray("artists").size() > 0 ? node.getAsJsonArray("artists").get(0).getAsString() : null;
                    addTrack(id, title, genre, artist);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки треков из json: " + e.getMessage(), e);
        }
    }
} 