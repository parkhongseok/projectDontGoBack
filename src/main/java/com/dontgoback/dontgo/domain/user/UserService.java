package com.dontgoback.dontgo.domain.user;

import com.dontgoback.dontgo.domain.feed.Feed;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    final UserRepository userRepository;

    public Optional<User> findById(long id){
        return userRepository.findById(id);
    }

    public User signUp(String userName, String email, RedBlueType type) {
        User user = User.builder()
                .userName(userName)
                .email(email)
                .userType(type)
                .build();
        this.userRepository.save(user);
        return user;
    }

    public User createDummyUser(String userName, String email, RedBlueType type) {
        User user = User.builder()
                .userName(userName)
                .email(email)
                .userType(type)
                .build();
        this.userRepository.save(user);
        return user;
    }
}
