package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.VurderUttakDokumentasjonBekreftelse.DokumentasjonVurderingBehov.Behov;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.VurderUttakDokumentasjonBekreftelse.DokumentasjonVurderingBehov.Vurdering;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.VurderUttakDokumentasjonBekreftelse.DokumentasjonVurderingBehov.Vurdering.GODKJENT;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.VurderUttakDokumentasjonBekreftelse.DokumentasjonVurderingBehov.Vurdering.IKKE_DOKUMENTERT;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.VurderUttakDokumentasjonBekreftelse.DokumentasjonVurderingBehov.Vurdering.IKKE_GODKJENT;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.ÅpenPeriodeDto;

public class VurderUttakDokumentasjonBekreftelse extends AksjonspunktBekreftelse {

    private List<DokumentasjonVurderingBehov> vurderingBehov;

    List<DokumentasjonVurderingBehov> getVurderingBehov() {
        return vurderingBehov == null ? List.of() : vurderingBehov;
    }

    public void setVurderingBehov(List<DokumentasjonVurderingBehov> vurderingBehov) {
        this.vurderingBehov = vurderingBehov;
    }

    public VurderUttakDokumentasjonBekreftelse godkjenn() {
        vurder(GODKJENT, null, null);
        return this;
    }

    public VurderUttakDokumentasjonBekreftelse godkjenn(ÅpenPeriodeDto åpenPeriodeDto) {
        vurder(GODKJENT, null, åpenPeriodeDto.fom(), åpenPeriodeDto.tom());
        return this;
    }

    public VurderUttakDokumentasjonBekreftelse godkjenn(LukketPeriodeMedVedlegg periode) {
        vurder(GODKJENT, null, periode);
        return this;
    }

    public VurderUttakDokumentasjonBekreftelse ikkeGodkjenn(LukketPeriodeMedVedlegg periode) {
        vurder(IKKE_GODKJENT, null, periode);
        return this;
    }

    public VurderUttakDokumentasjonBekreftelse ikkeGodkjenn(ÅpenPeriodeDto periode) {
        vurder(IKKE_GODKJENT, null, periode.fom(), periode.tom());
        return this;
    }

    public VurderUttakDokumentasjonBekreftelse godkjennSykdom() {
        vurder(GODKJENT, Behov.Årsak.SYKDOM_SØKER, null, null);
        return this;
    }

    public VurderUttakDokumentasjonBekreftelse godkjennMorsAktivitet(Behov.Årsak årsak) {
        vurder(GODKJENT, årsak, null);
        return this;
    }

    public VurderUttakDokumentasjonBekreftelse ikkeDokumentert(LukketPeriodeMedVedlegg periode) {
        vurder(IKKE_DOKUMENTERT, null, periode);
        return this;
    }


    public VurderUttakDokumentasjonBekreftelse ikkeDokumentert(ÅpenPeriodeDto åpenPeriodeDto) {
        vurder(IKKE_DOKUMENTERT, null, åpenPeriodeDto.fom(), åpenPeriodeDto.tom());
        return this;
    }

    private void vurder(Vurdering vurdering,
                        Behov.Årsak årsak,
                        LukketPeriodeMedVedlegg periode) {
        if (periode == null) {
            vurder(vurdering, årsak, null, null);
        } else {
            vurder(vurdering, årsak, periode.getFom(), periode.getTom());
        }
    }

    private void vurder(Vurdering vurdering,
                        Behov.Årsak årsak,
                        LocalDate fom,
                        LocalDate tom) {
        vurderingBehov = vurderingBehov.stream()
                .map(vb -> likPeriode(fom, tom, vb) && årsak == null || vb.årsak == årsak ? new DokumentasjonVurderingBehov(vb.fom,
                        vb.tom, vb.type, vb.årsak, vurdering) : vb)
                .toList();
        this.begrunnelse = "autotest begrunnelse";
    }



    private static boolean likPeriode(LocalDate fom, LocalDate tom, DokumentasjonVurderingBehov vb) {
        if (fom == null) {
            return true;
        }

        return fom.isEqual(vb.fom) && tom.isEqual(vb.tom);
    }

    @Override
    public String aksjonspunktKode() {
        return AksjonspunktKoder.VURDER_UTTAK_DOKUMENTASJON_KODE;
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        this.vurderingBehov = behandling.getDokumentasjonVurderingBehov().stream()
                .map(vb -> vb.vurdering != null ? vb : new DokumentasjonVurderingBehov(vb.fom, vb.tom, vb.type, vb.årsak, GODKJENT))
                .toList();
    }

    public record DokumentasjonVurderingBehov(LocalDate fom, LocalDate tom, Behov.Type type,
                                              Behov.Årsak årsak,
                                              Vurdering vurdering) {

        enum Vurdering {
            GODKJENT,
            IKKE_GODKJENT,
            IKKE_DOKUMENTERT
        }

        public record Behov() {

            public enum Type {
                UTSETTELSE,
                OVERFØRING,
                UTTAK,
            }

            public enum Årsak {
                //Utsettelse
                INNLEGGELSE_SØKER,
                INNLEGGELSE_BARN,
                HV_ØVELSE,
                NAV_TILTAK,
                SYKDOM_SØKER,

                //Overføring
                INNLEGGELSE_ANNEN_FORELDER,
                SYKDOM_ANNEN_FORELDER,
                BARE_SØKER_RETT,
                ALENEOMSORG,

                //Aktivitetskrav
                TIDLIG_OPPSTART_FAR,
                AKTIVITETSKRAV_ARBEID,
                AKTIVITETSKRAV_UTDANNING,
                AKTIVITETSKRAV_KVALPROG,
                AKTIVITETSKRAV_INTROPROG,
                AKTIVITETSKRAV_TRENGER_HJELP,
                AKTIVITETSKRAV_INNLAGT,
                AKTIVITETSKRAV_ARBEID_OG_UTDANNING,
                AKTIVITETSKRAV_IKKE_OPPGITT,
            }
        }

    }
}
