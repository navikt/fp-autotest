package no.nav.foreldrepenger.autotest.base;

import static no.nav.foreldrepenger.autotest.brev.BrevFormateringUtils.førsteArbeidsdagEtter;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Beslutter;
import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Klagebehandler;
import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Oppgavestyrer;
import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Overstyrer;
import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Saksbehandler;
import no.nav.foreldrepenger.autotest.brev.BrevAssertionBuilder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.DokumentTag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkType;
import no.nav.foreldrepenger.autotest.util.pdf.Pdf;
import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;
import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;
import no.nav.foreldrepenger.kontrakter.risk.kodeverk.RisikoklasseType;

public abstract class VerdikjedeTestBase extends BrevTestBase {

    protected static final Integer G_2024 = 124_028;
    protected static final Integer G_2025 = 130_160;
    protected static final Integer SEKS_G_2024 = G_2024 * 6;
    protected static final Integer SEKS_G_2025 = G_2025 * 6;
    protected static final Integer DAGSATS_VED_6_G_2025 = BigDecimal.valueOf(SEKS_G_2025).divide(BigDecimal.valueOf(260), RoundingMode.HALF_EVEN).intValue();

    protected static final Logger LOG = LoggerFactory.getLogger(VerdikjedeTestBase.class);

    /*
     * Aktører
     */
    protected Saksbehandler saksbehandler;
    protected Overstyrer overstyrer;
    protected Beslutter beslutter;
    protected Klagebehandler klagebehandler;
    protected Oppgavestyrer oppgavestyrer;

    @BeforeEach
    public void setUp() {
        saksbehandler = new Saksbehandler();
        overstyrer = new Overstyrer();
        beslutter = new Beslutter();
        klagebehandler = new Klagebehandler();
        oppgavestyrer = new Oppgavestyrer();
    }

    protected void hentBrevOgSjekkAtInnholdetErRiktig(BrevAssertionBuilder assertionBuilder,
                                                      DokumentTag dokumentTag,
                                                      HistorikkType ventTilHistorikkinnslag) {
        hentBrevOgSjekkAtInnholdetErRiktig(assertionBuilder, dokumentTag, ventTilHistorikkinnslag, 0);
    }

    protected void hentBrevOgSjekkAtInnholdetErRiktig(BrevAssertionBuilder brevAssertions,
                                                      DokumentTag dokumentTag,
                                                      HistorikkType historikkInnslagType,
                                                      int historikkInnslagIndeks) {

        var behandler = saksbehandler;
        if (DokumentTag.KLAGE_OMGJØRIN.equals(dokumentTag)) {
            behandler = klagebehandler;
        }

        var pdf = hentPdf(dokumentTag, historikkInnslagType, historikkInnslagIndeks, behandler);
        assertThat(Pdf.is_pdf(pdf)).as("Sjekker om byte array er av typen PDF").isTrue();

        var assertions = brevAssertions.build();
        LOG.info("Sjekker {} assertions i {} brevet...", assertions.size(), dokumentTag.tag());
        validerBrevetInneholderForventedeTekstavsnitt(pdf, assertions);
        LOG.info("Brevet {} er validering OK", dokumentTag.tag());
    }

    protected void validerInnsendtInntektsmeldingForeldrepenger(Fødselsnummer fødselsnummer,
                                                                LocalDate førsteDagMedYtelsen,
                                                                Integer månedsInntekt,
                                                                boolean harRefusjon) {
        validerInnsendtInntektsmeldingForeldrepenger(fødselsnummer, førsteDagMedYtelsen, månedsInntekt, harRefusjon, 0);
    }

    protected void validerInnsendtInntektsmeldingForeldrepenger(Fødselsnummer fødselsnummer,
                                                                LocalDate førsteDagMedYtelsen,
                                                                Integer månedsInntekt,
                                                                boolean harRefusjon,
                                                                int historikkInnslagIndeks) {
        validerInnsendtInntektsmelding(fødselsnummer, førsteDagMedYtelsen, månedsInntekt, harRefusjon, TypeYtelse.FP,
                historikkInnslagIndeks);
    }

    protected void validerInnsendtInntektsmeldingSvangerskapspenger(Fødselsnummer fødselsnummer,
                                                                    LocalDate førsteDagMedYtelsen,
                                                                    Integer månedsInntekt,
                                                                    boolean harRefusjon) {
        validerInnsendtInntektsmeldingSvangerskapspenger(fødselsnummer, førsteDagMedYtelsen, månedsInntekt, harRefusjon, 0);
    }

    protected void validerInnsendtInntektsmeldingSvangerskapspenger(Fødselsnummer fødselsnummer,
                                                                    LocalDate førsteDagMedYtelsen,
                                                                    Integer månedsInntekt,
                                                                    boolean harRefusjon,
                                                                    int historikkInnslagIndeks) {
        validerInnsendtInntektsmelding(fødselsnummer, førsteDagMedYtelsen, månedsInntekt, harRefusjon, TypeYtelse.SVP,
                historikkInnslagIndeks);
    }

    protected void validerInnsendtInntektsmelding(Fødselsnummer fødselsnummer,
                                                  LocalDate førsteDagMedYtelsen,
                                                  Integer månedsInntekt,
                                                  boolean harRefusjon,
                                                  TypeYtelse typeYtelse,
                                                  int historikkInnslagIndeks) {

        Objects.requireNonNull(typeYtelse, "ytelseType");
        var førsteDagAvkortet = førsteDagMedYtelsen;
        if (TypeYtelse.FP.equals(typeYtelse)) {
            førsteDagAvkortet = førsteArbeidsdagEtter(førsteDagMedYtelsen);
        }

        var brevAssertionsBuilder = inntektsmeldingBrevAssertionsBuilder(fødselsnummer.value(), førsteDagAvkortet, månedsInntekt,
                harRefusjon, typeYtelse);

        hentBrevOgSjekkAtInnholdetErRiktig(brevAssertionsBuilder, DokumentTag.INNTEKSTMELDING, HistorikkType.VEDLEGG_MOTTATT,
                historikkInnslagIndeks);
    }

    private static byte[] hentPdf(DokumentTag dokumentTag,
                                  HistorikkType historikkInnslagType,
                                  int historikkInnslagIndeks,
                                  Saksbehandler behandler) {
        behandler.ventTilHistorikkinnslag(historikkInnslagType);
        var dokumentId = behandler.hentHistorikkinnslagAvTypeMedDokument(historikkInnslagType, dokumentTag, historikkInnslagIndeks)
                .dokumenter()
                .getFirst()
                .dokumentId();
        return behandler.hentJournalførtDokument(dokumentId, "ARKIV");
    }

    public void foreslårOgFatterVedtakVenterTilAvsluttetBehandling(Saksnummer saksnummer,
                                                                   boolean revurdering,
                                                                   boolean tilbakekreving) {
        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummer, revurdering, tilbakekreving, true);
    }

    public void foreslårOgFatterVedtakVenterTilAvsluttetBehandling(Saksnummer saksnummer, boolean revurdering,
                                                                   boolean tilbakekreving, boolean ventPåSendtBrev) {
        if (!revurdering) {
            saksbehandler.ventTilRisikoKlassefiseringsstatus(RisikoklasseType.IKKE_HØY);
        }
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        if (beslutter.harRevurderingBehandling() && revurdering) {
            beslutter.ventPåOgVelgRevurderingBehandling();
        }
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        if (tilbakekreving) {
            beslutter.bekreftAksjonspunkt(bekreftelse);
            beslutter.ventTilAvsluttetBehandlingOgDetOpprettesTilbakekreving();
        } else {
            beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        }
        if (ventPåSendtBrev) {
            saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        }
    }

    protected void ventPåInntektsmeldingForespørsel(Saksnummer saksnummer) {
        saksbehandler.hentFagsak(saksnummer);
        LOG.info("Venter på inntektsmelding forespørsel for saksnummer {}...", saksnummer.value());
        saksbehandler.ventTilHistorikkinnslag(HistorikkType.MIN_SIDE_ARBEIDSGIVER);
    }


}
