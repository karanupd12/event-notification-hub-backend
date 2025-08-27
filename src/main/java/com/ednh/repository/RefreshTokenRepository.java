package com.ednh.repository;

import com.ednh.entity.RefreshToken;
import com.ednh.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for RefreshToken entity
 * Manages refresh token lifecycle
 */
@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);

    void deleteByToken(String token);

    // Clean up expired tokens
    void deleteByRevokedTrue();
}
