package cn.ccwisp.tcm.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "secure.jwt")
public class JwtPropertiesConfig {
    private String secret;
    private String tokenHeader;
    private long expiration;
    private String tokenHead;
}
