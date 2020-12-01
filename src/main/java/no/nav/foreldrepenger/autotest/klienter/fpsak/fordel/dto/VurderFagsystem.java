package no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record VurderFagsystem(String journalpostId,
                              boolean strukturertSøknad,
                              String aktørId,
                              String behandlingstemaOffisiellKode,
                              List<String>adopsjonsBarnFodselsdatoer,
                              String barnTermindato,
                              String barnFodselsdato,
                              String omsorgsovertakelsedato,
                              String årsakInnsendingInntektsmelding,
                              String saksnummer,
                              String annenPart) {
}
