package com.dontgoback.dontgo.domain.user;
import com.dontgoback.dontgo.domain.accountStateHistory.AccountStatusHistory;
import com.dontgoback.dontgo.domain.assetHistory.AssetHistory;
import com.dontgoback.dontgo.global.jpa.BaseEntity;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.UserRole;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole role;

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

    // 권한 반환
    /** UserRole 과 getAuthorities()의 관계
     *  1. getAuthorities():
     *      Spring Security에서 UserDetails 인터페이스의 getAuthorities() 메서드는
     *      해당 유저가 어떤 권한(들)을 가지고 있는지를 Collection<? extends GrantedAuthority> 형태로 반환
     *  2. SimpleGrantedAuthority:
     *      SimpleGrantedAuthority의 가장 일반적인 구현체로, 권한 문자열 하나를 감싸는 간단한 클래스
     *  3. "ROLE_" 접두사 (Prefix):
     *      Spring Security의 기본 규칙 중 하나로, 역할(Role) 기반으로 권한을 체크할 때,
     *      권한 문자열이 "ROLE_"로 시작해야 함
     *      예를 들어, ADMIN 역할은 "ROLE_ADMIN"이라는 문자열 권한으로 인식
     *   4. 이후 컨트롤러 등에서 @PreAuthorize("hasRole('ADMIN')")과 같은 권한 검증 로직이 정상적으로 동작
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
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