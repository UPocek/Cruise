package com.cruise.Cruise.user.Repositories;

import com.cruise.Cruise.models.ResetPasswordRequest;
import com.cruise.Cruise.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IResetPasswordRequestRepository extends JpaRepository<ResetPasswordRequest, Long> {
    Optional<ResetPasswordRequest> findByHash(String hash);

    Optional<ResetPasswordRequest> findByUserEmailAndHash(String email, String hash);

    Optional<ResetPasswordRequest> findByUser(User user);
}
