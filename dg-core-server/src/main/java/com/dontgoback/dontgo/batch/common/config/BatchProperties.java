package com.dontgoback.dontgo.batch.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "batch")
@Component
public class BatchProperties {
    private AssetRefresh assetRefresh = new AssetRefresh();
    private Scheduling scheduling = new Scheduling();

    @Getter
    @Setter
    public static class AssetRefresh {
        private boolean enabled = true;
        private String cron = "0 0 3 * * *";
        private String timezone = "Asia/Seoul";
    }
    @Getter
    @Setter
    public static class Scheduling {
        private int poolSize = 1;
    }
}
