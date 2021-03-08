package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AktivitetStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AndelKilde;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.OpptjeningAktivitetType;

class RedigerbarAndel {

    protected String andel;
    protected int andelsnr;
    protected String arbeidsgiverId;
    protected String arbeidsforholdId;
    protected Boolean nyAndel;
    protected AndelKilde kilde;
    protected AktivitetStatus aktivitetStatus;
    protected OpptjeningAktivitetType arbeidsforholdType;
    protected Boolean lagtTilAvSaksbehandler;
    protected LocalDate beregningsperiodeFom;
    protected LocalDate beregningsperiodeTom;

    public RedigerbarAndel(String andel, int andelsnr, String arbeidsgiverId, String arbeidsforholdId, Boolean nyAndel,
                           Boolean lagtTilAvSaksbehandler, AktivitetStatus aktivitetStatus, LocalDate beregningsperiodeFom,
                           LocalDate beregningsperiodeTom, OpptjeningAktivitetType arbeidsforholdType) {
        this.andel = andel;
        this.andelsnr = andelsnr;
        this.arbeidsgiverId = arbeidsgiverId;
        this.arbeidsforholdId = arbeidsforholdId;
        this.nyAndel = nyAndel;
        this.lagtTilAvSaksbehandler = lagtTilAvSaksbehandler;
        this.aktivitetStatus = aktivitetStatus;
        this.beregningsperiodeFom = beregningsperiodeFom;
        this.beregningsperiodeTom = beregningsperiodeTom;
        this.arbeidsforholdType = arbeidsforholdType;
    }

    public String getAndel() {
        return andel;
    }

    public void setAndel(String andel) {
        this.andel = andel;
    }

    public int getAndelsnr() {
        return andelsnr;
    }

    public void setAndelsnr(int andelsnr) {
        this.andelsnr = andelsnr;
    }

    public String getArbeidsforholdId() {
        return arbeidsforholdId;
    }

    public void setArbeidsforholdId(String arbeidsforholdId) {
        this.arbeidsforholdId = arbeidsforholdId;
    }

    public Boolean getNyAndel() {
        return nyAndel;
    }

    public void setNyAndel(Boolean nyAndel) {
        this.nyAndel = nyAndel;
    }

    public Boolean getLagtTilAvSaksbehandler() {
        return lagtTilAvSaksbehandler;
    }

    public void setLagtTilAvSaksbehandler(Boolean lagtTilAvSaksbehandler) {
        this.lagtTilAvSaksbehandler = lagtTilAvSaksbehandler;
    }

    public void setKilde(AndelKilde kilde) {
        this.kilde = kilde;
    }

    public String getArbeidsgiverId() {
        return arbeidsgiverId;
    }
}
