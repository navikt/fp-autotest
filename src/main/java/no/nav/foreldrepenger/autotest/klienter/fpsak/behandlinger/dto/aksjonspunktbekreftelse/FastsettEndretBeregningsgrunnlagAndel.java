package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

class FastsettEndretBeregningsgrunnlagAndel extends RedigerbarAndel {

    protected FastsatteVerdier fastsatteVerdier;

    public FastsettEndretBeregningsgrunnlagAndel(RedigerbarAndel redigerbarAndel, FastsatteVerdier fastsatteVerdier) {
        super(redigerbarAndel.andel, redigerbarAndel.andelsnr, redigerbarAndel.arbeidsgiverId,
                redigerbarAndel.arbeidsforholdId, redigerbarAndel.nyAndel, redigerbarAndel.lagtTilAvSaksbehandler,
                redigerbarAndel.aktivitetStatus, redigerbarAndel.beregningsperiodeFom,
                redigerbarAndel.beregningsperiodeTom, redigerbarAndel.arbeidsforholdType);
        this.fastsatteVerdier = fastsatteVerdier;
    }

}
