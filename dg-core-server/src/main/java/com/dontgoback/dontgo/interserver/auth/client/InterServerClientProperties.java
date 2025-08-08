package com.dontgoback.dontgo.interserver.auth.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "inter-server.auth.client")
public class InterServerClientProperties {
    private String id;
    private String secret;
}