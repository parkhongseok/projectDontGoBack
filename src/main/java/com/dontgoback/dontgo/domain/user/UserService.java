package com.dontgoback.dontgo.domain.user;
import com.dontgoback.dontgo.domain.user.dto.AddUserRequest;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {
    final UserRepository userRepository;
//
//    public User save(AddUserRequest dto) {
////        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//            return userRepository.save(User.builder()
//                    .userName("BackDummy : Me")
//                    .userType(RedBlueType.BLUE)
//                    .email(dto.getEmail())
//            .build());
//}

    public User findById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("Unexpected user"));
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("unexpected user"));
    }




    public User createDummyUser(String userAsset, String email, RedBlueType type) {
        User user = User.builder()
                .userAsset(userAsset)
                .email(email)
                .userType(type)
                .build();
        this.userRepository.save(user);
        return user;
    }
}
