package com.se.idoctor.repository;

import com.se.idoctor.entity.Userx;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<Userx, Long> {

    Optional<Userx> findUserxByEmail(String email);
    Optional<Userx> findUserxByUsername(String email);
    Optional<Userx> findUserxByUsernameOrEmail(String cred1, String cred2);
}
