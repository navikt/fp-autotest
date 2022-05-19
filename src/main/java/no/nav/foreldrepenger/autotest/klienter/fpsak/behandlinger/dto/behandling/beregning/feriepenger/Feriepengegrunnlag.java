package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.feriepenger;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Feriepengegrunnlag(LocalDate feriepengeperiodeFom,
                                 LocalDate feriepengeperiodeTom,
                                 List<Feriepengeandel> andeler) {}
