package com.dontgoback.dontgo.domain.accountStateHistory;

import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.DarkMode;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.ProfileVisibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSetting{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ProfileVisibility profileVisibility;

    @Enumerated(EnumType.STRING)
    private DarkMode darkMode;
}
