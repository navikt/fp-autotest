package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.InntektPrAndel;

@BekreftelseKode(kode = "5038")
public class VurderBeregnetInntektsAvvikBekreftelse extends AksjonspunktBekreftelse {

    protected List<InntektPrAndel> inntektPrAndelList = new ArrayList<>();
    protected Integer inntektFrilanser;

    public VurderBeregnetInntektsAvvikBekreftelse() {
        super();
    }

    public VurderBeregnetInntektsAvvikBekreftelse leggTilInntekt(Integer inntekt, Integer andelsnr) {
        inntektPrAndelList.add(new InntektPrAndel(inntekt, andelsnr.longValue()));
        return this;
    }

    public VurderBeregnetInntektsAvvikBekreftelse leggTilInntektFrilans(Integer inntektFrilanser) {
        this.inntektFrilanser = inntektFrilanser;
        return this;
    }

}
