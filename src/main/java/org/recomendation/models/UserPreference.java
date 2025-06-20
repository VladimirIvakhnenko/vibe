package org.recomendation.models;

import java.util.HashSet;
import java.util.Set;

/**
 * Класс UserPreference хранит лайки и дизлайки для одного пользователя.
 * Так как система рассчитана на одного пользователя, идентификатор не требуется.
 */
public class UserPreference {
    /** Множество id треков, которые пользователь лайкнул */
    private final Set<String> likedTrackIds = new HashSet<>();
    /** Множество id треков, которые пользователь дизлайкнул */
    private final Set<String> dislikedTrackIds = new HashSet<>();

    /**
     * Получить множество id лайкнутых треков.
     */
    public Set<String> getLikedTrackIds() { return likedTrackIds; }
    /**
     * Получить множество id дизлайкнутых треков.
     */
    public Set<String> getDislikedTrackIds() { return dislikedTrackIds; }
} 