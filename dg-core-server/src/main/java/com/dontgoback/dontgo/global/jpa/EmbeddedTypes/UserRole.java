package com.dontgoback.dontgo.global.jpa.EmbeddedTypes;

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
 */
public enum UserRole {
    ADMIN, USER, GUEST
}
