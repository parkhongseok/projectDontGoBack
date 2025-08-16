package com.dontgoback.dontgo.seed.core;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OneTimeSeedLockService {

    private final JdbcTemplate jdbc;

    /**
     * LOCK_KEY를 insert하여 1회 실행을 보장한다.
     * 이미 있으면 false 반환(=이미 실행됨).
     */
    public boolean acquire(String lockKey) {
        try {
            jdbc.update("insert into data_seed_lock(lock_key) values (?)", lockKey);
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }
}
