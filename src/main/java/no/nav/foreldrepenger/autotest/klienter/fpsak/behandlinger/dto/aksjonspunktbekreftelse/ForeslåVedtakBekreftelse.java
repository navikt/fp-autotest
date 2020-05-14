package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@BekreftelseKode(kode="5015")
public class ForeslåVedtakBekreftelse extends AksjonspunktBekreftelse {



    //TODO se om dette stemmer enda
    //protected int antallBarn;
    protected Kode avslagCode;
    //protected int beregningResultat;

    protected String fritekstBrev;
    protected Boolean skalBrukeOverstyrendeFritekstBrev;
    protected Boolean isVedtakSubmission;

    public ForeslåVedtakBekreftelse() {
        super();
    }


    public ForeslåVedtakBekreftelse setAvslagCode(Kode avslagCode) {
        this.avslagCode = avslagCode;
        return this;
    }

    public ForeslåVedtakBekreftelse setIsVedtakSubmission(Boolean verdi) {
        this.isVedtakSubmission = verdi;
        return this;
    }



}
