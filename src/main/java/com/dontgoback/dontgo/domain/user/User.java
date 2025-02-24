package com.dontgoback.dontgo.domain.user;
import com.dontgoback.dontgo.domain.feed.Feed;
import com.dontgoback.dontgo.global.jpa.BaseEntity;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
//@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name="users")
public class User extends BaseEntity implements UserDetails {

//    @OneToMany(mappedBy = "user")
//    private List<Feed> feeds;

    @Column(name = "user_asset", nullable = true)
    private String userAsset;

    @Column(nullable = false, unique = true)
    private String email;

    @Transient // 데이터베이스에 저장되지 않음
    @Column(name = "password")
    private String password = "";

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = true)
    private RedBlueType userType;

    // OAuth2 를 위해
    // 사용자 이름 필드 추가 -> 생성자에 닉네임 추가
    @Column(name = "nickname", unique = true)
    private String nickname;

    // 사용자 이름 변경
    public User update(String nickname){
        this.nickname = nickname;
        return this;
    }

    // 권한 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of(new SimpleGrantedAuthority("user"));
    }

    // 사용자 id(고유한 값) 반환
    @Override
    public String getUsername(){
        return email;
    }

    // 비밀번호 반환
    @Override
    public String getPassword(){
        return ""; //// 비밀번호를 사용하지 않음
    }

    // 계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired(){
        // 만료되었는지 확인하는 로직
        return true; // true -> 아직 만료 전
    }

    // 계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked(){
        // 계정이 잠겼는지 확인하는 로직
        return true; // true -> 잠기지 않았음
    }

    // 비밀번호 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired(){
        // 만료 확인 로직
        return true; // true -> 만료되지 않음
    }

    // 계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled(){
        // 사용 가능 확인 로직
        return true; // true -> 사용 가능
    }


}