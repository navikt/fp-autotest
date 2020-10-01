package no.nav.foreldrepenger.autotest.klienter.fpsak.hendelser.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HendelseWrapper {
    protected Hendelse hendelse;

    public HendelseWrapper(Hendelse hendelse) {
        super();
        this.hendelse = hendelse;
    }

    public Hendelse getHendelse() {
        return hendelse;
    }
}
