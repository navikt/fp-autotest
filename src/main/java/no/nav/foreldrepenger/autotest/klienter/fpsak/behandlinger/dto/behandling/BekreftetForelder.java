package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BekreftetForelder {
    protected String adresse;
    protected boolean bekreftetAvTps;
    protected Object dodsdato;
    protected boolean erMor;
    protected String navn;
    protected int nummer;
    protected Object oversyrtPersonstatus;
    protected String personstatus;
    protected Object utlandsadresse;
}
