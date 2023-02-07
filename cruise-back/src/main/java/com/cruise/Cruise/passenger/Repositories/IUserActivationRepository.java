package com.cruise.Cruise.passenger.Repositories;

import com.cruise.Cruise.models.UserActivation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserActivationRepository extends JpaRepository<UserActivation, Long> {

}
