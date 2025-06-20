package org.recomendation.db;

import org.recomendation.models.UserPreference;

/**
 * Репозиторий для хранения предпочтений единственного пользователя.
 */
public class UserPreferenceRepository {
    private final UserPreference userPreference = new UserPreference();

    public UserPreference get() {
        return userPreference;
    }
} 