# JPA 기반 엔티티 설계

Date: 2025-02-20

## 상태

적용 중

## 맥락

지금까지 아래와 같은 흐름으로 설계해왔다.

<pre>
└── 1. 도메인 설계
          ├── 2. DB의 Table 설계
          │    
          └── 3. Backend의 Entity 설계
</pre>

그리고 DB에 맞게 클래스를 설계해서 이를 서로 연결해야하는 문제가 남아있다.

### 문제 1. DB와 연결하기

우선 Spring과 DB의 연결 방법을 고민했다. 다음과 같은 선택지가 있었다.

- #### JDBC API 직접 사용 시

  - DB의 범용성 측면에선 장점이 존재
  - DB가 변할 때마다 해야할 작업이 많고, 또한 질의 결과 매핑에 대한 번거로움 존재

- #### SQL Mapper 사용 시 (iBatis, MyBatis)

  - DB의 범용성 측면의 장점이 존재
  - DB 변경 시 작성해야할 코드가 줄어서 비교적 나음
  - 여전히 SQL문을 작성해야하며, 질의 결과를 객체에 매핑해야하는 번거로움 존재

- #### ORM 방식 사용 시 `(JPA 인터페이스 + Hibernate 구현체)`
  - DB 변경이 자유로움
  - SQL로부터 자유로움
    - 복잡한 조회 시, JSQL 등을 통해 쿼리를 작성해야하지만,
    - Table이 아닌 엔티티 중심이며 방언으로부터 자유롭다. => 따라서 DB에 덜 종속적이다.

따라서 이번 프로젝트에서는 `JPA가 적합하다고 판단`했다.

### 문제 2. Table과 Entity 연결하기

연결된 DB의 Table을 코드 상에서는 Class로 표현해야 한다. 하지만 이 둘 사이엔 큰 차이점이 있다. \
DB에서 테이블 간 연관관계는 외래키(Foreign Key)를 통한 양방향 연결이 가능하다. \
하지만 Java에서는 클래스 필드를 통해 연관관계를 정의하며, 단방향 참조가 기본이다. \
정리하자면 아래와 같다.

| 구분 |   연결 대상   |           연결 형태           | 연관관계 설정 방식 |  방향성  |
| :--: | :-----------: | :---------------------------: | :----------------: | :------: |
|  DB  | Table 간 연결 | 한 컬럼이 다른 컬럼의 PK 소유 |    외래키 지정     | `양방향` |
| Java | Class 간 연결 | 한 필드가 다른 필드 정보 소유 | 타 필드 정보 추가  | `단방향` |

즉, 연결할 클래스의 정보를 현재 필드에 추가하는 방식으로 연관관계를 설정할 수 있다. \
필드에 다른 클래스의 정보를 추가하는 방식은 다음 두 가지 선택지가 존재한다.

- #### 기본형 타입 필드에 직접 정보 저장

  - SQL 중심적인 방식으로, JPA보다는 MyBatis에 적합
  - 다른 엔티티 정보를 조회하려면 추가적인 쿼리가 필요
  - 예시
    ```
    class Feed {
        private Long userId; // FK를 직접 저장
    }
    ```

- #### 참조형 타입 필드에 연결 정보 저장 (사용 중)
  - JPA가 객체 관계를 자동으로 매핑
  - 예시
    ```
    class Feed {
        @ManyToOne
        private User user; // User 객체를 직접 참조
    }
    ```

### 문제 3. 제한된 선택지 강요 여부

정해진 값 중 하나만 갖는 필드는 보통 다음 두 가지 방식으로 설계할 수 있다

- #### 문자열 저장 방식

  - 단순한 구현이 가능하지만, 오타 발생 가능성이 높음
  - 예시
    ```
    userSetting1.profileVisibility = "public"
    userSetting2.profileVisibility = "privvdsv12zate" // 실수할 가능성
    ```

- #### Enum 저장 방식 (사용 중)
  - 정해진 선택지 강요로, 오타 가능성 사전 방지
  - 예시
    ```
    userSetting1.profileVisibility = ProfileVisibility.PUBLIC
    userSetting2.profileVisibility = ProfileVisibility.PRIVATE
    ```

## 결정

### 결정 1. DB와 연결하기 : JPA + Hibernate 사용

### 결정 2. Table과 Entity 연결하기 : 참조를 통한 연관 관계 설정 사용

### 결정 3. 제한된 선택지 강요 여부 : Enum을 활용한 선택지 제한 사용

!["Entity Architecture"](./07-JPA-기반-엔티티-설계.jpg)

## 결과

JPA 기반 엔티티 설계를 통해 객체 지향적인 개발이 가능해졌으며, 연관관계를 명확하게 정의할 수 있었다.
하지만 아래과 같은 문제가 등장했다.

### 1. 양방향 연관관계 설정 시 발생 문제 : ManyToOne에서만 매핑하여 단방향 관계만 설정

- JPA에서 양방향 연관관계를 설정하려면 두 엔티티가 서로 참조해야 한다.

- 참조형 필드의 setter 또는 add 메서드를 구현할 때, 한쪽에서만 정보를 추가하면 DB에 값이 정상적으로 반영되지 않는 문제가 있었다. 따라서 아래와 같이 한쪽에서 추가하더라도 양쪽 모두에서 그 정보를 갱신해줘야 했다.

```
class User {
    @OneToMany(mappedBy = "user")
    private List<Feed> feeds = new ArrayList<>();

    public void addFeed(Feed feed) {
        this.feeds.add(feed); // User -> Feed 연결
        feed.setUser(this); // Feed -> User 연결
    }
}
```

- 불필요한 조회 발생으로 인한 성능 저하 문제와 개발 복잡성 증가를 고려하여, ManyToOne에서만 매핑하여 단방향 관계를 유지하기로 결정했다.

### 2. Lazy 로딩과 N+1 문제 : 부분적으로 Fetch Join 사용 (철회)

- 전체 피드 목록을 가져올 때, 각 피드마다 user의 추가 조회 문제 발생 (1+N 번의 쿼리)
- Fetch Join 적용하여 개선했으나, 조회해야할 정보가 복잡하며, 페이지네이션과의 충돌로 인해 DTO Projection 방식으로 교체

### 3. 페이지네이션 문제 : 부분적으로 DTO Projection 사용

- Fetch Join을 사용할 경우 limit 및 offset 적용이 어려움 → DTO Projection 사용
- JPQL을 직접 작성해야 하는 단점 존재

### 4. 영속성 컨텍스트의 트랜잭션을 지원하는 쓰기 지연으로 인한 문제 : 특정 상황에서만 @PreUpdate 사용

- 한 트랜잭션 내에서 객체를 업데이트 후, 곧바로 갱신된 시간을 불러오는 경우 문제가 발생

  - 변경이 반영되기 이전 시간이 불러와졌다.
  - 갱신 시간은 DB에서 직접 작성되며, 쓰기 지연 저장소에서 트랜잭션이 커밋하기 이전에 값을 불러왔기에 당연지사 이전의 값이 불러와진 것

  ```
  @LastModifiedDate
  protected LocalDateTime updatedAt;

  @PreUpdate
  public void preUpdate() {
  this.updatedAt = LocalDateTime.now();
  }

  ```

- entityManager.flush()를 사용하여 강제 반영할 수도 있지만, 추가적인 DB 트래픽이 발생하는 문제 존재
- 트랜잭션 실패 시 잘못된 updatedAt 값이 반환될 가능성 인지
- 현재 프로젝트에서는 해당 기능에서 DB 반영 시간과 0.001초 수준의 차이가 허용 가능하여 PreUpdate 방식 채택

## 맥락

지금까지 아래와 같은 흐름으로 설계해왔다.

<pre>
└── 1. 도메인 설계
          ├── 2. DB의 Table 설계
          │    
          └── 3. Backend의 Entity 설계
</pre>

그리고 2.와 3.을 서로 연결해야하는 문제가 남아있었다.

### 문제 1. DB와 연결하기

우선 Spring과 DB의 연결 방법을 고민했다. 다음과 같은 선택지가 있었다.

- #### JDBC API 직접 사용 시

  - DB의 범용성 측면에선 장점이 존재
  - DB가 변할 때마다 해야할 작업이 많고, 또한 질의 결과 매핑에 대한 번거로움 존재

- #### SQL Mapper 사용 시 (iBatis, MyBatis)

  - DB의 범용성 측면의 장점이 존재
  - DB 변경 시 작성해야할 코드가 줄어서 비교적 나음
  - 여전히 SQL문을 작성해야하며, 질의 결과를 객체에 매핑해야하는 번거로움 존재

- #### JPA 사용 시 (사용 중)
  - DB 변경이 자유로움
  - SQL로부터 자유로움
    - JSQL 사용 시 쿼리를 작성해야하지만, Table이 아닌 엔티티 중심이며 방언으로부터 자유롭다.

따라서 이번 프로젝트에서는 `JPA가 적합하다고 판단`했다.

### 문제 2. Table과 Entity 연결하기

연결된 DB의 Table을 코드 상에서는 Class로 표현해야 한다. 하지만 이 둘 사이엔 큰 차이점이 있다. \
DB에서 테이블 간 연관관계는 외래키(Foreign Key)를 통한 양방향 연결이 가능하다. \
하지만 Java에서는 클래스 필드를 통해 연관관계를 정의하며, 단방향 참조가 기본이다.

| 구분 |   연결 대상   |           연결 형태           | 연관관계 설정 방식 |  방향성  |
| :--: | :-----------: | :---------------------------: | :----------------: | :------: |
|  DB  | Table 간 연결 | 한 컬럼이 다른 컬럼의 PK 소유 |    외래키 지정     | `양방향` |
| Java | Class 간 연결 | 한 필드가 다른 필드 정보 소유 | 타 필드 정보 추가  | `단방향` |

즉, 연결할 클래스의 정보를 현재 필드에 추가하는 방식으로 연관관계를 설정할 수 있다. \
필드에 다른 클래스의 정보를 추가하는 방식은 다음 두 가지 선택지가 존재한다.

- #### 기본형 타입 필드에 직접 정보 저장

  - SQL 중심적인 방식으로, JPA보다는 MyBatis에 적합
  - 다른 엔티티 정보를 조회하려면 추가적인 쿼리가 필요
  - 예시
    ```
    class Feed {
        private Long userId; // FK를 직접 저장
    }
    ```

- #### 참조형 타입 필드에 연결 정보 저장 (사용 중)
  - JPA가 객체 관계를 자동으로 매핑
  - 예시
    ```
    class Feed {
        @ManyToOne
        private User user; // User 객체를 직접 참조
    }
    ```

### 문제 3. 제한된 선택지 강요 여부

정해진 값 중 하나만 갖는 필드는 보통 다음 두 가지 방식으로 설계할 수 있다

- #### 문자열 저장 방식

  - 단순한 구현이 가능하지만, 오타 발생 가능성이 높음
  - 예시
    ```
    userSetting1.profileVisibility = "public"
    userSetting2.profileVisibility = "privvdsv12zate" // 실수할 가능성
    ```

- #### Enum 저장 방식 (사용 중)
  - 정해진 선택지 강요로, 오타 가능성 사전 방지
  - 예시
    ```
    userSetting1.profileVisibility = ProfileVisibility.PUBLIC
    userSetting2.profileVisibility = ProfileVisibility.PRIVATE
    ```

## 결정

### 결정 1. DB와 연결하기 : JPA + Hibernate 사용

### 결정 2. Table과 Entity 연결하기 : 참조를 통한 연관 관계 설정 사용

### 결정 3. 제한된 선택지 강요 여부 : Enum을 활용한 선택지 제한 사용

## 결과

JPA 기반 엔티티 설계를 통해 객체 지향적인 개발이 가능해졌으며, 연관관계를 명확하게 정의할 수 있었다.
하지만 아래과 같은 문제가 등장했다.

### 1. 양방향 연관관계 설정 시 발생 문제 : ManyToOne에서만 매핑하여 단방향 관계만 설정

- JPA에서 양방향 연관관계를 설정하려면 두 엔티티가 서로 참조해야 한다.

- 참조형 필드의 setter 또는 add 메서드를 구현할 때, 한쪽에서만 정보를 추가하면 DB에 값이 정상적으로 반영되지 않는 문제가 있었다. 따라서 아래와 같이 한쪽에서 추가하더라도 양쪽 모두에서 그 정보를 갱신해줘야 했다.

```
class User {
    @OneToMany(mappedBy = "user")
    private List<Feed> feeds = new ArrayList<>();

    public void addFeed(Feed feed) {
        this.feeds.add(feed); // User -> Feed 연결
        feed.setUser(this); // Feed -> User 연결
    }
}
```

- 성능 저하 문제와 복잡성 증가를 고려하여, ManyToOne에서만 매핑하여 단방향 관계를 유지하기로 결정했다.

### 2. Lazy 로딩과 N+1 문제 : 부분적으로 Fetch Join 사용 (철회)

- 전체 피드 목록을 가져올 때, 각 피드마다 user의 추가 조회 문제 발생 (1+N 번의 쿼리)
- Fetch Join 적용하여 개선했으나, 페이지네이션과의 충돌로 인해 DTO Projection 방식으로 교체

### 3. 페이지네이션 문제 : 부분적으로 DTO Projection 사용

- Fetch Join을 사용할 경우 limit 및 offset 적용이 어려움 → DTO Projection 사용
- JPQL을 직접 작성해야 하는 단점 존재

### 4. 영속성 컨텍스트의 트랜잭션을 지원하는 쓰기 지연으로 인한 문제 : 특정 상황에서만 @PreUpdate 사용

- 한 트랜잭션 내에서 객체를 업데이트 후, 곧바로 갱신된 시간을 불러오는 경우 문제가 발생

  - 변경이 반영되기 이전 시간이 불러와졌다.
  - 갱신 시간은 DB에서 직접 작성되며, 쓰기 지연 저장소에서 트랜잭션이 커밋하기 이전에 값을 불러왔기에 당연지사 이전의 값이 불러와진 것

  ```
  @LastModifiedDate
  protected LocalDateTime updatedAt;

  @PreUpdate
  public void preUpdate() {
  this.updatedAt = LocalDateTime.now();
  }

  ```

- entityManager.flush()를 사용하여 강제 반영할 수도 있지만, 추가적인 DB 트래픽이 발생하는 문제 존재
- 트랜잭션 실패 시 잘못된 updatedAt 값이 반환될 가능성 인지
- 현재 프로젝트에서는 해당 기능에서 DB 반영 시간과 0.001초 수준의 차이가 허용 가능하여 PreUpdate 방식 채택
