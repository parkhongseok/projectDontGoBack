package com.dontgoback.dontgo.domain.user;
import com.dontgoback.dontgo.domain.accountStateHistory.AccountStatusHistory;
import com.dontgoback.dontgo.domain.assetHistory.AssetHistory;
import com.dontgoback.dontgo.global.jpa.BaseEntity;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name="users")
public class User extends BaseEntity implements UserDetails {

//    @Column(name = "user_asset", nullable = true)
//    private String userAsset1;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_asset_history_id")
    private AssetHistory currentAssetHistory;

    public void setCurrentAssetHistory(AssetHistory assethistory) {
        this.currentAssetHistory = assethistory;
        if (assethistory.getUser() != this) {
            assethistory.setUser(this); // 양방향 동기화
        }
    }

    public String getUserAsset(){
        return this.currentAssetHistory.getAssetName();
    }

    public RedBlueType getUserType(){
        return this.currentAssetHistory.getAssetType();
    }

    @Column(nullable = false, unique = true)
    private String email;

    @Transient // 데이터베이스에 저장되지 않음
    @Column(name = "password")
    private final String password = "";

//    @Enumerated(EnumType.STRING)
//    @Column(name = "user_type", nullable = true)
//    private RedBlueType userType;

    // 유저의 설정 캐싱 (변동 시 이력과 캐싱 사항 동기화 필요) 하지만 무결성 문제로 일대일 관계로 매핑하는 방향으로 정정
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private AccountStatus currentStatus;
//
//    private LocalDateTime currentStatusChangedAt;

    // DB 논리적 구조: → 1:N / JPA 매핑: 현재 이력 1개만 참조
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_status_history_id") // FK가 User 테이블에 위치
    private AccountStatusHistory currentStatusHistory;

    public void setCurrentStatusHistory(AccountStatusHistory history) {
        this.currentStatusHistory = history;
        if (history.getUser() != this) {
            history.setUser(this); // 양방향 동기화
        }
    }

//    // OAuth2 를 위해
//    // 사용자 이름 필드 추가 -> 생성자에 닉네임 추가
//    @Column(name = "nickname", unique = true)
//    private String nickname;
//
    // 사용자 이름 변경
//    public User update(String nickname){
//        this.email = email;
//        return this;
//    }

    // 권한 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of(new SimpleGrantedAuthority("user"));
    }

    // 사용자 id(고유한 값) 반환 따라서 자산 정보 대신 email 사용
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
    public boolean isEnabled() {
        return this.currentStatusHistory.getAccountStatus().isAllowLogin();
    }
}