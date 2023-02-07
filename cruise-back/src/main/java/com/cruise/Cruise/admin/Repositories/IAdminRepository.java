package com.cruise.Cruise.admin.Repositories;

import com.cruise.Cruise.models.Admin;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAdminRepository extends JpaRepository<Admin, Long> {
    Admin findByUsername(String username);

    @NotNull
    List<Admin> findAll();

}
