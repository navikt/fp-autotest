package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.feriepenger;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AktivitetStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Feriepengeandel (AktivitetStatus aktivitetStatus, Integer opptjeningsår, BigDecimal årsbeløp,
                               boolean erBrukerMottaker, String arbeidsgiverId, String arbeidsforholdId) {}
