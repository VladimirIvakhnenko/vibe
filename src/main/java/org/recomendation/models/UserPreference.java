package org.recomendation.models;

import java.util.HashSet;
import java.util.Set;

public class UserPreference {
    private final String userId;
    private final Set<String> likedTrackIds = new HashSet<>();
    private final Set<String> dislikedTrackIds = new HashSet<>();

    public UserPreference(String userId) {
        this.userId = userId;
    }

    public String getUserId() { return userId; }
    public Set<String> getLikedTrackIds() { return likedTrackIds; }
    public Set<String> getDislikedTrackIds() { return dislikedTrackIds; }
} 