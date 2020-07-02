package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@BekreftelseKode(kode = "5026")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VarselOmRevurderingBekreftelse extends AksjonspunktBekreftelse {

    private String begrunnelseForVarsel;
    private String fritekst;
    private String sendVarsel;
    private LocalDate frist;
    private String ventearsak;

    public VarselOmRevurderingBekreftelse() {
        super();
    }

    @JsonCreator
    public VarselOmRevurderingBekreftelse(String begrunnelseForVarsel, String fritekst, String sendVarsel, LocalDate frist, String ventearsak) {
        this.begrunnelseForVarsel = begrunnelseForVarsel;
        this.fritekst = fritekst;
        this.sendVarsel = sendVarsel;
        this.frist = frist;
        this.ventearsak = ventearsak;
    }

    public String getBegrunnelseForVarsel() {
        return begrunnelseForVarsel;
    }

    public String getFritekst() {
        return fritekst;
    }

    public String getSendVarsel() {
        return sendVarsel;
    }

    public LocalDate getFrist() {
        return frist;
    }

    public String getVentearsak() {
        return ventearsak;
    }

    public VarselOmRevurderingBekreftelse setFrist(LocalDate frist) {
        this.frist = frist;
        return this;
    }

    public VarselOmRevurderingBekreftelse bekreftSendVarsel(Kode årsak, String fritekst) {
        this.sendVarsel = "" + true;
        this.fritekst = fritekst;
        this.ventearsak = årsak.kode;
        return this;
    }

    public VarselOmRevurderingBekreftelse bekreftIkkeSendVarsel() {
        this.sendVarsel = "" + false;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VarselOmRevurderingBekreftelse that = (VarselOmRevurderingBekreftelse) o;
        return Objects.equals(begrunnelseForVarsel, that.begrunnelseForVarsel) &&
                Objects.equals(fritekst, that.fritekst) &&
                Objects.equals(sendVarsel, that.sendVarsel) &&
                Objects.equals(frist, that.frist) &&
                Objects.equals(ventearsak, that.ventearsak);
    }

    @Override
    public int hashCode() {
        return Objects.hash(begrunnelseForVarsel, fritekst, sendVarsel, frist, ventearsak);
    }

    @Override
    public String toString() {
        return "VarselOmRevurderingBekreftelse{" +
                "begrunnelseForVarsel='" + begrunnelseForVarsel + '\'' +
                ", fritekst='" + fritekst + '\'' +
                ", sendVarsel='" + sendVarsel + '\'' +
                ", frist=" + frist +
                ", ventearsak='" + ventearsak + '\'' +
                "} " + super.toString();
    }
}
