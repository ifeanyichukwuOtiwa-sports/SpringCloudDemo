package io.codewithwinnie.spring.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class UriConfiguration {

    private String httpBin = "http://httpbin.org:80";

    public String getHttpBin() {
        return httpBin;
    }

    public void setHttpBin(String httpBin) {
        this.httpBin = httpBin;
    }
}
