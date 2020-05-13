package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@BekreftelseKode(kode="5015")
public class ForesloVedtakBekreftelse extends AksjonspunktBekreftelse {



    //TODO se om dette stemmer enda
    //protected int antallBarn;
    protected Kode avslagCode;
    //protected int beregningResultat;

    protected String fritekstBrev;
    protected Boolean skalBrukeOverstyrendeFritekstBrev;
    protected Boolean isVedtakSubmission;

    public ForesloVedtakBekreftelse() {
        super();
    }


    public ForesloVedtakBekreftelse setAvslagCode(Kode avslagCode) {
        this.avslagCode = avslagCode;
        return this;
    }

    public ForesloVedtakBekreftelse setIsVedtakSubmission(Boolean verdi) {
        this.isVedtakSubmission = verdi;
        return this;
    }



}
