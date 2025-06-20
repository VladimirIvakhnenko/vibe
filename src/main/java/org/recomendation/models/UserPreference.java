package org.recomendation.models;

import java.util.HashSet;
import java.util.Set;

/**
 * Класс UserPreference хранит лайки и дизлайки для одного пользователя.
 */
public class UserPreference {
    private final Set<String> likedTrackIds = new HashSet<>();
    private final Set<String> dislikedTrackIds = new HashSet<>();

    public Set<String> getLikedTrackIds() { return likedTrackIds; }
    public Set<String> getDislikedTrackIds() { return dislikedTrackIds; }
} 