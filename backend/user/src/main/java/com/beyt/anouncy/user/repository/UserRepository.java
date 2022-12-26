package com.beyt.anouncy.user.repository;

import com.beyt.anouncy.user.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findOneByEmailIgnoreCase(String email);

    Optional<User> findOneByActivationKey(String activationKey);

    Optional<User> findOneByResetKey(String resetKey);

    List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndActivationDateBefore(Instant dateTime);


    @Query("UPDATE User u SET u.selectedLocationIds = :locationIdList WHERE u.id = :userId")
    @Modifying
    void updateLocationIdList(@Param("userId") UUID userId, @Param("locationIdList") String locationIdList);
}
