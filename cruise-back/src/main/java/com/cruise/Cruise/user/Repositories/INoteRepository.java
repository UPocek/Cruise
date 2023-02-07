package com.cruise.Cruise.user.Repositories;

import com.cruise.Cruise.models.Note;
import com.cruise.Cruise.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface INoteRepository extends JpaRepository<Note, Long> {

    Set<Note> findByUser(User user);

}
