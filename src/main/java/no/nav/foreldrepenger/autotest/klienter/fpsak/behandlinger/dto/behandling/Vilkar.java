package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Vilkar {
    protected String vilkarType;
    protected String vilkarStatus;

    public String getVilkarType() {
        return vilkarType;
    }

    public String getVilkarStatus() {
        return vilkarStatus;
    }

    public void setVilkarType(String vilkarType) {
        this.vilkarType = vilkarType;
    }

    public void setVilkarStatus(String vilkarStatus) {
        this.vilkarStatus = vilkarStatus;
    }
}
