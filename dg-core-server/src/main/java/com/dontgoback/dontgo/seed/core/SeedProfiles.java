package com.dontgoback.dontgo.seed.core;

// "seed"라는 Spring Profile 이름을 하드코딩 대신 상수로 관리하기 위한 유틸 클래스
// 예: @Profile(SeedProfiles.SEED)
public class SeedProfiles {
    public static final String SEED = "seed";
    private SeedProfiles() {}
}
