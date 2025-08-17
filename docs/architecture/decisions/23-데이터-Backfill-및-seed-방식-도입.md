# 자산 이력(AssetHistory) 과거 데이터 백필 및 시드 방식 도입

Date: 2025-08-15  
Status: Accepted

## 맥락

운영 서버의 자산 데이터는 **하루 1회**만 갱신되어 과거 시계열이 부족했습니다.  
프로필 자산 그래프(일/주/월 단위) 검증과 데모를 위해 **가입일로 소급한 과거 데이터**가 필요했습니다.

기존 확장 서버의 일일 갱신 API는 과거 데이터를 대량으로 생성하기에는 비효율적이었으며,  
따라서 **코어 서버 내 전용 Seed 로직을 통한 백필(Backfill)** 방식을 도입하기로 했습니다.

### 목표

- 사용자별 **시작일 자산 = 10,000,000원** 고정 후, 매일 변동 값 생성·저장
- **재실행되어도 안전**(멱등/락)하며, **재현 가능(Deterministic)** 해야 함
- 운영/개발 환경에서 **한 번만 실행**하도록 3중 가드(테이블 락 + 프로필 + 워크플로우 제어)
- 기존 데이터가 있어도 **덮어쓰기(upsert)** 전략으로 일관성 유지

### 선택지

1. **확장 서버 호출**로 날짜별 갱신을 반복 호출

   - 장점: 기존 로직 재사용
   - 단점: 네트워크/JWT/캐시/타임존 변수, 속도/실패 처리 부담. 과거일자 지원 추가 필요

2. **코어 서버 내부 시드 로직**으로 직접 생성(결정적 배수) **[채택]**

   - 장점: 통신 제거, 속도/안정성↑, 운영/개발 동일 로직, 완전 재현 가능

<br/>
<br/>

## 결정

### 1. 패키지 구조

```
com.dontgoback.seed
├─ core
│  ├─ SeedProfiles            // "seed" 상수
│  ├─ OneTimeSeedLockService  // data_seed_lock insert (1회 실행 가드)
│  └─ SeedHelper              // runOnce(lockKey, Runnable)
└─ asset
   ├─ DailyAssetMultiplier    // (userId, day, salt) → multiplier (결정적)
   └─ AssetHistorySeeder      // CommandLineRunner (프로필: seed)
      AssetHistorySeedTx      // @Transactional public seed(...) 실제 작업
```

### 2. Triple Guard

- **DB 락 테이블**: `data_seed_lock(lock_key PK)` 에 insert 성공 시 1회 실행, 중복 시 skip
- **프로필**: `SPRING_PROFILES_ACTIVE=dev,seed` 등 seed 포함시에만 시더 빈 로딩
- **배포/워크플로우**: CI에서 seeder 컨테이너/작업을 **수동 트리거** 또는 별도 job으로 분리

### 3. 스키마 초기화

- Spring SQL Init로 부팅 초기에 생성

  - `data_seed_lock` 테이블 생성
  - `asset_history` 유니크 키: `(user_id, snapshot_day)`
  - 예시: 
    ```sql
    create table if not exists data_seed_lock (
      lock_key varchar(128) primary key,
      created_at timestamp default CURRENT_TIMESTAMP
    );
    alter table asset_history
      add constraint if not exists uk_asset_history_user_snapshot_day
      unique (user_id, snapshot_day);
    ```

### 4. 결정적 배수 생성기

- 입력: `(userId, LocalDate, secretSalt, σ, min%, max%)` → Box–Muller + HMAC 시드
- 시각적 편향 방지를 위해 σ/클램프는 데모 값(예: `σ=0.03`, `±5%`)로 운영

### 5. 업서트 시딩 로직

- 범위: `start = today - days` 부터 `today` **포함**
- **start일**: `amount=10,000,000`, `multiplier=1.0` 고정
- **start+1 \~ today**: `amount = round(prev * multiplier)`
- 존재 시 `findByUserIdAndSnapshotDay`로 조회 후 `amount/multiplier` **덮어쓰기**, 없으면 `persist`
- 대량 처리: 500건 주기로 `flush/clear`
- 트랜잭션: `AssetHistorySeedTx.seed(...) @Transactional`(프록시 경유, public)
- 락 키는 버저닝: `asset_history_seed_v2` (정책 변경 시 재실행 허용)

### 6. 프로필·YAML 예시

```yaml
seed:
  enabled: true
  days: 180
  startAmount: 10000000
  asset:
    volatility:
      sigma: 0.03
    clamp:
      minPercent: -5.0
      maxPercent: 5.0
    secretSalt: "secretSalt"
```


<br/>

## 결과

- **단순성과 안정성**: 외부 통신을 제거하여 속도와 재현성 확보
- **보안 및 일관성**: 업서트 전략으로 데이터 불일치 방지, 실행 횟수는 락 테이블로 보장
- **확장 가능성**: 운영/개발 환경에서 동일하게 동작하며, 필요 시 파라미터(σ, ±%) 조정 가능

### 1. 운영 절차

① 초회 시더 실행

- DB에 락 테이블/유니크 적용 (최초 한 번)
- GitHub Actions → Run workflow → run_seed=true로 수동 실행.
- Actions 로그/EC2 docker compose ps/애플리케이션 로그에서 “seed done” 확인.
- API로 임의 유저 Daily/Weekly/Monthly 호출 → 포인트 생성 확인.

② 재실행 방지/오류 처리

- 이미 실행됐다면 다음 트리거에서 seeder는 락 insert 실패로 즉시 종료.  
  (로그: [SEED] lock exists. already executed. skip.)
- 만약 데이터가 맘에 안 들어 초기화 후 재생성하고 싶다면:  
  delete from asset_history where snapshot_day >= ? 등 범위 삭제  
  delete from data_seed_lock where key='asset_history_seed_v1'  
  run_seed=true로 다시 시더 실행

③ 시더 설정 바꾸기

- 기간 조정: SEED_ASSET_HISTORY_DAYS
- 분산 폭/노이즈 변경: 시더 코드의 랜덤워크 파라미터

### 2. 운영/모니터링(Ops)

- 샘플 쿼리:

  - 일자별 유저 배수 다양성: `select snapshot_day, count(distinct multiplier) from asset_history group by 1 order by 1 desc;`
  - 유저별 범위 확인: `select user_id, min(snapshot_day), max(snapshot_day) from asset_history group by 1;`

- 재실행 필요 시: **락 키 버전** 업데이트 후 재배포

### 3. 대안/폐기(Alternatives)

- 확장 서버 과거일자 모드 추가(쿼리/헤더) 후 호출 반복: 네트워크 의존/실패 처리 부담으로 보류
- 전체 삭제 후 재삽입: 감사/참조 영향 가능해 기본은 업서트, 필요 시 특정 유저/구간에 한해 사용
