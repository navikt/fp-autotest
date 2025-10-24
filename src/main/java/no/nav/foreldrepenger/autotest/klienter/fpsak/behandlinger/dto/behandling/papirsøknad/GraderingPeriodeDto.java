package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad;

import java.math.BigDecimal;
import java.time.LocalDate;

import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.KontoType;

public class GraderingPeriodeDto {

    public KontoType periodeForGradering;
    public LocalDate periodeFom;
    public LocalDate periodeTom;

    public BigDecimal prosentandelArbeid;
    public String arbeidsgiverIdentifikator;
    public boolean erArbeidstaker;
    public boolean erFrilanser;
    public boolean erSelvstNæringsdrivende;

    public GraderingPeriodeDto(KontoType stønadskonto,
            LocalDate fom, LocalDate tom,
            BigDecimal prosentandelArbeid,
            String arbeidsgiverIdentifikator,
            boolean erArbeidstaker,
            boolean erFrilanser,
            boolean erSelvstNæringsdrivende) {
        periodeForGradering = stønadskonto;
        periodeFom = fom;
        periodeTom = tom;
        this.prosentandelArbeid = prosentandelArbeid;
        this.arbeidsgiverIdentifikator = arbeidsgiverIdentifikator;
        this.erArbeidstaker = erArbeidstaker;
        this.erFrilanser = erFrilanser;
        this.erSelvstNæringsdrivende = erSelvstNæringsdrivende;
    }
}
