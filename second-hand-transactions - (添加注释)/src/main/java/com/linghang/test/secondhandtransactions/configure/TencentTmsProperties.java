package com.linghang.test.secondhandtransactions.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "tencent.tms")
public class TencentTmsProperties {
    private String secretId;
    private String secretkey;
    private String bizType;
}