package com.ednh.repository;

import com.ednh.entity.Application;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Application entity
 * Manages webhook client applications
 */
@Repository
public interface ApplicationRepository extends MongoRepository<Application, String> {

    Optional<Application> findByAppId(String appId);

    Optional<Application> findByAppIdAndEnabled(String appId, boolean enabled);

    boolean existsByAppId(String appId);
}
