package org.recomendation.db;

import org.recomendation.models.UserPreference;

/**
 * Репозиторий для хранения предпочтений единственного пользователя.
 * Реализован как синглтон, так как в системе только один пользователь.
 */
public class UserPreferenceRepository {
    /** Единственный экземпляр предпочтений пользователя */
    private final UserPreference userPreference = new UserPreference();

    /**
     * Получить экземпляр предпочтений пользователя.
     * @return UserPreference
     */
    public UserPreference get() {
        return userPreference;
    }
} 