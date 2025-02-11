package com.dontgoback.dontgo.domain.user;
import com.dontgoback.dontgo.domain.feed.Feed;
import com.dontgoback.dontgo.global.jpa.BaseEntity;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name="users")
public class User extends BaseEntity {

    @OneToMany(mappedBy = "user")
    private List<Feed> feeds;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private RedBlueType userType;
}