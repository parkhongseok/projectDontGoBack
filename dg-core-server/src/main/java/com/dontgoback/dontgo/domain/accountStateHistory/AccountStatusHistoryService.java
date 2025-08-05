package com.dontgoback.dontgo.domain.accountStateHistory;

import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.AccountStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AccountStatusHistoryService {
    private final AccountStatusHistoryRepository accountStatusHistoryRepository;
//    private final UserRepository userRepository;

//    @Transactional
//    public User updateStatus(User user, AccountStatus newStatus, String reason) {
//        LocalDateTime now = LocalDateTime.now();
//        AccountStatusHistory currentStatusHistory = user.getCurrentStatusHistory();
//        // 현재 상태 종료
//        currentStatusHistory.setEndedAt(now);
//        accountStatusHistoryRepository.save(currentStatusHistory); // 명시적으로 호출
//
//        // 새 이력 추가
//        AccountStatusHistory newStatusHistory = AccountStatusHistory.builder()
//                .user(user)
//                .accountStatus(newStatus)
//                .changedAt(now)
//                .reason(reason)
//                .build();
//        accountStatusHistoryRepository.save(newStatusHistory);
//
//        user.setCurrentStatusHistory(newStatusHistory);
//        return userRepository.save(user);
//    }

    public AccountStatusHistory create(User user, AccountStatus status, String reason){

        AccountStatusHistory newStatusHistory = AccountStatusHistory
                .builder()
                .user(user)
                .accountStatus(status)
                .changedAt(LocalDateTime.now())
                .reason(reason)
                .build();

        return accountStatusHistoryRepository.save(newStatusHistory);
    }

//    @Transactional
//    public User initializeStatus(User user, AccountStatus newStatus, String reason) {
//        LocalDateTime now = LocalDateTime.now();
//        // 새 이력 추가
//        AccountStatusHistory newStatusHistory = AccountStatusHistory.builder()
//                .user(user)
//                .accountStatus(newStatus)
//                .changedAt(now)
//                .reason(reason)
//                .build();
//        accountStatusHistoryRepository.save(newStatusHistory);
//
//        user.setCurrentStatusHistory(newStatusHistory);
//
//        return userRepository.save(user);
//    }
}
