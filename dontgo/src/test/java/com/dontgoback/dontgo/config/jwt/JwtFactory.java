//package com.dontgoback.dontgo.config.jwt;
//import io.jsonwebtoken.Header;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import lombok.Builder;
//import lombok.Getter;
//
//
//import java.time.Duration;
//import java.util.Date;
//import java.util.Map;
//
//import static java.util.Collections.emptyMap;
//
//@Getter
//public class JwtFactory {
//    private String subject = "test@email.com";
//
//    private Date issuedAt = new Date();
//
//    private Date expiration = new Date(new Date().getTime() + Duration.ofDays(14).toMillis());
//
//    private Map<String, Object> claims = emptyMap();
//
//    private Long id = null;
//
//    @Builder
//    public JwtFactory(String subject, Date issuedAt, Date expiration,
//                      Map<String, Object> claims, Long id) {
//        this.subject = subject != null ? subject : this.subject;
//        this.issuedAt = issuedAt != null ? issuedAt : this.issuedAt;
//        this.expiration = expiration != null ? expiration : this.expiration;
//        this.claims = claims != null ? claims : this.claims;
//        this.id = id;
//    }
//
//    public static JwtFactory withDefaultValues() {
//        return JwtFactory.builder().build();
//    }
//
//    public String createToken(JwtProperties jwtProperties) {
//        var builder = Jwts.builder()
//                .setSubject(subject)
//                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
//                .setIssuer(jwtProperties.getIssuer())
//                .setIssuedAt(issuedAt)
//                .setExpiration(expiration)
//                .addClaims(claims);
//
//        // 👇 id 있으면 claim으로 추가
//        if (id != null) {
//            builder.claim("id", id);
//        }
//
//        return builder
//                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
//                .compact();
//    }
//}