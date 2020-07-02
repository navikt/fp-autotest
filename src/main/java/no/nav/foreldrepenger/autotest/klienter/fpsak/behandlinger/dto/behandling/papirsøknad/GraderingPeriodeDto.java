package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class GraderingPeriodeDto {

    private Stønadskonto periodeForGradering;
    private LocalDate periodeFom;
    private LocalDate periodeTom;
    private BigDecimal prosentandelArbeid;
    private String arbeidsgiverIdentifikator;
    private boolean erArbeidstaker;
    private boolean erFrilanser;
    private boolean erSelvstNæringsdrivende;

    public GraderingPeriodeDto(Stønadskonto stønadskonto,
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

    public Stønadskonto getPeriodeForGradering() {
        return periodeForGradering;
    }

    public LocalDate getPeriodeFom() {
        return periodeFom;
    }

    public LocalDate getPeriodeTom() {
        return periodeTom;
    }

    public BigDecimal getProsentandelArbeid() {
        return prosentandelArbeid;
    }

    public String getArbeidsgiverIdentifikator() {
        return arbeidsgiverIdentifikator;
    }

    public boolean isErArbeidstaker() {
        return erArbeidstaker;
    }

    public boolean isErFrilanser() {
        return erFrilanser;
    }

    public boolean isErSelvstNæringsdrivende() {
        return erSelvstNæringsdrivende;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraderingPeriodeDto that = (GraderingPeriodeDto) o;
        return erArbeidstaker == that.erArbeidstaker &&
                erFrilanser == that.erFrilanser &&
                erSelvstNæringsdrivende == that.erSelvstNæringsdrivende &&
                periodeForGradering == that.periodeForGradering &&
                Objects.equals(periodeFom, that.periodeFom) &&
                Objects.equals(periodeTom, that.periodeTom) &&
                Objects.equals(prosentandelArbeid, that.prosentandelArbeid) &&
                Objects.equals(arbeidsgiverIdentifikator, that.arbeidsgiverIdentifikator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(periodeForGradering, periodeFom, periodeTom, prosentandelArbeid, arbeidsgiverIdentifikator, erArbeidstaker, erFrilanser, erSelvstNæringsdrivende);
    }

    @Override
    public String toString() {
        return "GraderingPeriodeDto{" +
                "periodeForGradering=" + periodeForGradering +
                ", periodeFom=" + periodeFom +
                ", periodeTom=" + periodeTom +
                ", prosentandelArbeid=" + prosentandelArbeid +
                ", arbeidsgiverIdentifikator='" + arbeidsgiverIdentifikator + '\'' +
                ", erArbeidstaker=" + erArbeidstaker +
                ", erFrilanser=" + erFrilanser +
                ", erSelvstNæringsdrivende=" + erSelvstNæringsdrivende +
                '}';
    }
}
