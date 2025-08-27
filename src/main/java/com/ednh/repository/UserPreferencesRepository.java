package com.ednh.repository;

import com.ednh.entity.UserPreferences;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for UserPreferences entity
 * Manages user notification preferences storage
 */
@Repository
public interface UserPreferencesRepository extends MongoRepository<UserPreferences, String> {

    Optional<UserPreferences> findByUserId(String userId);

    boolean existsByUserId(String userId);

    void deleteByUserId(String userId);

    // Multi-tenant support
    Optional<UserPreferences> findByUserIdAndTenantId(String userId, String tenantId);
}
