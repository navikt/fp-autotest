package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.InntektPrAndel;

@BekreftelseKode(kode = "5038")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VurderBeregnetInntektsAvvikBekreftelse extends AksjonspunktBekreftelse {

    private List<InntektPrAndel> inntektPrAndelList = new ArrayList<>();
    private Integer inntektFrilanser;

    public VurderBeregnetInntektsAvvikBekreftelse() {
        super();
    }

    public List<InntektPrAndel> getInntektPrAndelList() {
        return inntektPrAndelList;
    }

    public Integer getInntektFrilanser() {
        return inntektFrilanser;
    }

    public VurderBeregnetInntektsAvvikBekreftelse leggTilInntekt(Integer inntekt, Integer andelsnr) {
        inntektPrAndelList.add(new InntektPrAndel(inntekt, andelsnr.longValue()));
        return this;
    }

    public VurderBeregnetInntektsAvvikBekreftelse leggTilInntektFrilans(Integer inntektFrilanser) {
        this.inntektFrilanser = inntektFrilanser;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VurderBeregnetInntektsAvvikBekreftelse that = (VurderBeregnetInntektsAvvikBekreftelse) o;
        return Objects.equals(inntektPrAndelList, that.inntektPrAndelList) &&
                Objects.equals(inntektFrilanser, that.inntektFrilanser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inntektPrAndelList, inntektFrilanser);
    }

    @Override
    public String toString() {
        return "VurderBeregnetInntektsAvvikBekreftelse{" +
                "inntektPrAndelList=" + inntektPrAndelList +
                ", inntektFrilanser=" + inntektFrilanser +
                "} " + super.toString();
    }
}
