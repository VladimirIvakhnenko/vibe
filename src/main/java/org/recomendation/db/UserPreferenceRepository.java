package org.recomendation.db;

import org.recomendation.models.UserPreference;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserPreferenceRepository {
    private final Map<String, UserPreference> userPreferences = new ConcurrentHashMap<>();

    public Optional<UserPreference> findByUserId(String userId) {
        return Optional.ofNullable(userPreferences.get(userId));
    }

    public UserPreference save(UserPreference userPreference) {
        userPreferences.put(userPreference.getUserId(), userPreference);
        return userPreference;
    }
} 