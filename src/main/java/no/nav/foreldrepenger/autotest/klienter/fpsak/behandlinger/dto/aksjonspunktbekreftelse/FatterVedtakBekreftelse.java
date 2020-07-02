package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;
import no.nav.foreldrepenger.autotest.util.AllureHelper;

@BekreftelseKode(kode = "5016")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FatterVedtakBekreftelse extends AksjonspunktBekreftelse {

    private List<AksjonspunktGodkjenningDto> aksjonspunktGodkjenningDtos = new ArrayList<>();

    public FatterVedtakBekreftelse() {
        super();
        /* TODO Auto-generated constructor stub */
    }

    public FatterVedtakBekreftelse(@JsonProperty("aksjonspunktGodkjenningDtos") List<AksjonspunktGodkjenningDto> aksjonspunktGodkjenningDtos) {
        this.aksjonspunktGodkjenningDtos = aksjonspunktGodkjenningDtos;
    }

    public List<AksjonspunktGodkjenningDto> getAksjonspunktGodkjenningDtos() {
        return aksjonspunktGodkjenningDtos;
    }

    public void godkjennAksjonspunkter(List<Aksjonspunkt> aksjonspunkter) {
        for (Aksjonspunkt aksjonspunkt : aksjonspunkter) {
            godkjennAksjonspunkt(aksjonspunkt);
        }
    }

    public FatterVedtakBekreftelse godkjennAksjonspunkt(Aksjonspunkt aksjonspunkt) {
        if (!aksjonspunkt.skalTilToTrinnsBehandling()) {
            AllureHelper.debugAksjonspunkt(aksjonspunkt);
            throw new RuntimeException(
                    "Godkjenner aksjonspunkt som ikke skal til totrinnskontroll: " + aksjonspunkt.getDefinisjon().kode);
        }

        AksjonspunktGodkjenningDto godkjenning = new AksjonspunktGodkjenningDto(aksjonspunkt);
        godkjenning.godkjent = true;
        aksjonspunktGodkjenningDtos.add(godkjenning);
        return this;
    }

    public FatterVedtakBekreftelse avvisAksjonspunkt(Aksjonspunkt aksjonspunkt, Kode kode) {
        List<String> årsaker = new ArrayList<>();
        årsaker.add(kode.kode);
        avvisAksjonspunkt(aksjonspunkt, årsaker);
        return this;
    }

    private void avvisAksjonspunkt(Aksjonspunkt aksjonspunkt, List<String> arsaker) {
        if (!aksjonspunkt.skalTilToTrinnsBehandling()) {
            throw new RuntimeException(
                    "Avvister aksjonspunkt som ikke skal til totrinnskontroll: " + aksjonspunkt.getDefinisjon().kode);
        }

        AksjonspunktGodkjenningDto godkjenning = new AksjonspunktGodkjenningDto(aksjonspunkt);
        godkjenning.godkjent = false;
        godkjenning.arsaker = arsaker;
        aksjonspunktGodkjenningDtos.add(godkjenning);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FatterVedtakBekreftelse that = (FatterVedtakBekreftelse) o;
        return Objects.equals(aksjonspunktGodkjenningDtos, that.aksjonspunktGodkjenningDtos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aksjonspunktGodkjenningDtos);
    }

    @Override
    public String toString() {
        return "FatterVedtakBekreftelse{" +
                "aksjonspunktGodkjenningDtos=" + aksjonspunktGodkjenningDtos +
                ", kode='" + kode + '\'' +
                ", begrunnelse='" + begrunnelse + '\'' +
                "} " + super.toString();
    }

    @JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class AksjonspunktGodkjenningDto {
        private String aksjonspunktKode;
        private List<String> arsaker = new ArrayList<>();
        private String begrunnelse = null;
        private boolean godkjent = false;

        public AksjonspunktGodkjenningDto(Aksjonspunkt aksjonspunkt) {
            aksjonspunktKode = aksjonspunkt.getDefinisjon().kode;
        }

        @JsonCreator
        public AksjonspunktGodkjenningDto(String aksjonspunktKode, List<String> arsaker, String begrunnelse, boolean godkjent) {
            this.aksjonspunktKode = aksjonspunktKode;
            this.arsaker = arsaker;
            this.begrunnelse = begrunnelse;
            this.godkjent = godkjent;
        }

        public String getAksjonspunktKode() {
            return aksjonspunktKode;
        }

        public List<String> getArsaker() {
            return arsaker;
        }

        public String getBegrunnelse() {
            return begrunnelse;
        }

        public boolean isGodkjent() {
            return godkjent;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AksjonspunktGodkjenningDto that = (AksjonspunktGodkjenningDto) o;
            return godkjent == that.godkjent &&
                    Objects.equals(aksjonspunktKode, that.aksjonspunktKode) &&
                    Objects.equals(arsaker, that.arsaker) &&
                    Objects.equals(begrunnelse, that.begrunnelse);
        }

        @Override
        public int hashCode() {
            return Objects.hash(aksjonspunktKode, arsaker, begrunnelse, godkjent);
        }

        @Override
        public String toString() {
            return "AksjonspunktGodkjenningDto{" +
                    "aksjonspunktKode='" + aksjonspunktKode + '\'' +
                    ", arsaker=" + arsaker +
                    ", begrunnelse='" + begrunnelse + '\'' +
                    ", godkjent=" + godkjent +
                    '}';
        }
    }
}
