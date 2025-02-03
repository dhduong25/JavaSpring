package com.hduong25.javalearn.modules.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author: hduong25
 */

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
