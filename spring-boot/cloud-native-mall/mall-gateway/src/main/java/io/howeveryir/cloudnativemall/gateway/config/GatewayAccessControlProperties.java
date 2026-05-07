package io.howeveryir.cloudnativemall.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "gateway.access-control")
public class GatewayAccessControlProperties {

    private String tenantHeader = "X-Tenant-Id";
    private List<String> tenantWhitelist = new ArrayList<>();
    private List<String> tenantBlacklist = new ArrayList<>();
    private List<String> ipBlacklist = new ArrayList<>();

    public String getTenantHeader() {
        return tenantHeader;
    }

    public void setTenantHeader(String tenantHeader) {
        this.tenantHeader = tenantHeader;
    }

    public List<String> getTenantWhitelist() {
        return tenantWhitelist;
    }

    public void setTenantWhitelist(List<String> tenantWhitelist) {
        this.tenantWhitelist = tenantWhitelist;
    }

    public List<String> getTenantBlacklist() {
        return tenantBlacklist;
    }

    public void setTenantBlacklist(List<String> tenantBlacklist) {
        this.tenantBlacklist = tenantBlacklist;
    }

    public List<String> getIpBlacklist() {
        return ipBlacklist;
    }

    public void setIpBlacklist(List<String> ipBlacklist) {
        this.ipBlacklist = ipBlacklist;
    }
}
