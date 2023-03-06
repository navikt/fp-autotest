package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

public class FastsettMaanedsinntektUtenInntektsmeldingAndel {

    protected long andelsnr;
    protected Integer fastsattBeløp;

    public FastsettMaanedsinntektUtenInntektsmeldingAndel(long andelsnr, int arbeidsinntekt) {
        this.andelsnr = andelsnr;
        this.fastsattBeløp = arbeidsinntekt;
    }

    public long getAndelsnr() {
        return andelsnr;
    }

}
