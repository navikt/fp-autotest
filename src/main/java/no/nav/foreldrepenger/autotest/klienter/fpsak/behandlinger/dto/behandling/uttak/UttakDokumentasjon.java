package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UttakDokumentasjon {

    protected LocalDate fom;
    protected LocalDate tom;

    public UttakDokumentasjon() {
    }

    public UttakDokumentasjon(LocalDate fom, LocalDate tom) {
        super();
        this.fom = fom;
        this.tom = tom;
    }
}
