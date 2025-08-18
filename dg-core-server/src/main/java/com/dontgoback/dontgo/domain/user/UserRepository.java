package com.dontgoback.dontgo.domain.user;

import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); //이메일로 사용자 정보 조회

    @Query("""
            SELECT u
            FROM User u
            WHERE u.currentStatusHistory.accountStatus = ACTIVE""")
    List<User> findAllActiveUsers();
}
