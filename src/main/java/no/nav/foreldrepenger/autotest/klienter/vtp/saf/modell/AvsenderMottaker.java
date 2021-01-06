package no.nav.foreldrepenger.autotest.klienter.vtp.saf.modell;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AvsenderMottaker(String id,
                               String type,
                               String navn) {

    public Optional<String> getIdHvisFNR() {
        return "FNR".equalsIgnoreCase(type) ? Optional.ofNullable(id) : Optional.empty();
    }

}
