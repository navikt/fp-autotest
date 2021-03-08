package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AktivitetStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AndelKilde;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.OpptjeningAktivitetType;

public class RedigerbarAndelDto {

    private final Long andelsnr;
    private final String arbeidsgiverId;
    private final String arbeidsforholdId;
    private final Boolean nyAndel;
    private final AndelKilde kilde;
    private final AktivitetStatus aktivitetStatus;
    private final OpptjeningAktivitetType arbeidsforholdType;
    private final Boolean lagtTilAvSaksbehandler;
    private final LocalDate beregningsperiodeFom;
    private final LocalDate beregningsperiodeTom;


    public RedigerbarAndelDto(Long andelsnr, String arbeidsgiverId, String arbeidsforholdId, Boolean nyAndel,
                              AndelKilde kilde, AktivitetStatus aktivitetStatus,
                              OpptjeningAktivitetType arbeidsforholdType, Boolean lagtTilAvSaksbehandler,
                              LocalDate beregningsperiodeFom, LocalDate beregningsperiodeTom) {
        this.andelsnr = andelsnr;
        this.arbeidsgiverId = arbeidsgiverId;
        this.arbeidsforholdId = arbeidsforholdId;
        this.nyAndel = nyAndel;
        this.kilde = kilde;
        this.aktivitetStatus = aktivitetStatus;
        this.arbeidsforholdType = arbeidsforholdType;
        this.lagtTilAvSaksbehandler = lagtTilAvSaksbehandler;
        this.beregningsperiodeFom = beregningsperiodeFom;
        this.beregningsperiodeTom = beregningsperiodeTom;
    }

    public Long getAndelsnr() {
        return andelsnr;
    }

    public String getArbeidsgiverId() {
        return arbeidsgiverId;
    }

    public String getArbeidsforholdId() {
        return arbeidsforholdId;
    }

    public Boolean getNyAndel() {
        return nyAndel;
    }

    public AndelKilde getKilde() {
        return kilde;
    }

    public AktivitetStatus getAktivitetStatus() {
        return aktivitetStatus;
    }

    public OpptjeningAktivitetType getArbeidsforholdType() {
        return arbeidsforholdType;
    }

    public Boolean getLagtTilAvSaksbehandler() {
        return lagtTilAvSaksbehandler;
    }

    public LocalDate getBeregningsperiodeFom() {
        return beregningsperiodeFom;
    }

    public LocalDate getBeregningsperiodeTom() {
        return beregningsperiodeTom;
    }
}
