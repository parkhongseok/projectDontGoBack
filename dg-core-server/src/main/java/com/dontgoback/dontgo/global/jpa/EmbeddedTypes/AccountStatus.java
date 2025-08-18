package com.dontgoback.dontgo.global.jpa.EmbeddedTypes;

public enum AccountStatus {
        ACTIVE(true),
        INACTIVE(true),                 // 비활성화
        CLOSE_REQUESTED(true),          // 탈퇴 요청 상태
        SUSPENDED(false);               // 관리자 제재로 인한 로그인 불가

        private final boolean allowLogin;

        AccountStatus(boolean allowLogin) {
                this.allowLogin = allowLogin;
        }

        public boolean isAllowLogin() {
                return allowLogin;
        }
}