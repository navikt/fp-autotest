package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.VurderÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.util.AllureHelper;

public class FatterVedtakBekreftelse extends AksjonspunktBekreftelse {

    protected List<AksjonspunktGodkjenningDto> aksjonspunktGodkjenningDtos = new ArrayList<>();

    public FatterVedtakBekreftelse godkjennAksjonspunkter(List<Aksjonspunkt> aksjonspunkter) {
        aksjonspunkter.forEach(this::godkjennAksjonspunkt);
        return this;
    }

    public FatterVedtakBekreftelse godkjennAksjonspunkt(Aksjonspunkt aksjonspunkt) {
        if (!aksjonspunkt.skalTilToTrinnsBehandling()) {
            AllureHelper.debugAksjonspunkt(aksjonspunkt);
            throw new RuntimeException("Godkjenner aksjonspunkt som ikke skal til totrinnskontroll: " +
                    aksjonspunkt.getDefinisjon());
        }

        var godkjenning = new AksjonspunktGodkjenningDto(aksjonspunkt);
        godkjenning.godkjent = true;
        aksjonspunktGodkjenningDtos.add(godkjenning);
        return this;
    }

    public FatterVedtakBekreftelse avvisAksjonspunkt(Aksjonspunkt aksjonspunkt, VurderÅrsak kode) {
        avvisAksjonspunkt(aksjonspunkt, List.of(kode.name()));
        return this;
    }

    public FatterVedtakBekreftelse avvisAksjonspunkt(Aksjonspunkt aksjonspunkt, List<String> arsaker) {
        if (!aksjonspunkt.skalTilToTrinnsBehandling()) {
            throw new RuntimeException("Avvister aksjonspunkt som ikke skal til totrinnskontroll: " +
                    aksjonspunkt.getDefinisjon());
        }

        var godkjenning = new AksjonspunktGodkjenningDto(aksjonspunkt);
        godkjenning.godkjent = false;
        godkjenning.arsaker = arsaker;
        aksjonspunktGodkjenningDtos.add(godkjenning);
        return this;
    }

    public boolean harAvvisteAksjonspunkt() {
        return aksjonspunktGodkjenningDtos.stream().anyMatch(a -> !a.godkjent);
    }

    public List<String> avvisteAksjonspunkt() {
        return aksjonspunktGodkjenningDtos.stream()
                .filter(a -> !a.godkjent)
                .map(a -> a.aksjonspunktKode)
                .toList();
    }

    @Override
    public String aksjonspunktKode() {
        return "5016";
    }

    public static class AksjonspunktGodkjenningDto {
        public String aksjonspunktKode;
        protected List<String> arsaker = new ArrayList<>();
        protected String begrunnelse = null;
        protected boolean godkjent = false;

        public AksjonspunktGodkjenningDto(Aksjonspunkt aksjonspunkt) {
            aksjonspunktKode = aksjonspunkt.getDefinisjon();
        }
    }
}
