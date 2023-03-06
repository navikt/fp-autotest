package no.nav.foreldrepenger.autotest.klienter.vtp.saf.modell;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Bruker(String id, BrukerIdType type) {

    public boolean erAktoerId() {
        return BrukerIdType.AKTOERID.equals(type);
    }
}
