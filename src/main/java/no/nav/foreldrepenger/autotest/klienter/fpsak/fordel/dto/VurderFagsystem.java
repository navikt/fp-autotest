package no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
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
