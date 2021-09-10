package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.modell.foreldrepenger.fordeling.MorsAktivitet.ARBEID_OG_UTDANNING;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.Stønadskonto.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.Stønadskonto.FORELDREPENGER;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.Stønadskonto.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.Stønadskonto.MØDREKVOTE;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.SøknadUtsettelseÅrsak.FRI;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.FordelingErketyper.fordelingEndringssøknadGradering;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.FordelingErketyper.generiskFordeling;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.SøknadEndringErketyper.lagEndringssøknad;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.UttaksperioderErketyper.utsettelsesperiode;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.UttaksperioderErketyper.uttaksperiode;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.IkkeOppfyltÅrsak.AKTIVITETSKRAVET_ARBEID_I_KOMB_UTDANNING_IKKE_DOKUMENTERT;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugFritekst;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandling;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.Stønadskonto;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.RettigheterErketyper;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingForeldrepengeErketyper;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.FagsakStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.IkkeOppfyltÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.KonsekvensForYtelsen;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KontrollerAktivitetskravBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KontrollerManueltOpprettetRevurdering;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderSoknadsfristForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrMedlemskapsvilkaaret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.autotest.util.localdate.Virkedager;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Tag("fpsak")
@Tag("foreldrepenger")
class Revurdering extends ForeldrepengerTestBase {

    private static final Logger logger = LoggerFactory.getLogger(Revurdering.class);

    @Test
    @DisplayName("Revurdering opprettet manuelt av saksbehandler.")
    @Description("Førstegangsbehandling til positivt vedtak. Saksbehandler oppretter revurdering manuelt. " +
            "Overstyrer medlemskap. Vedtaket opphører.")
    void opprettRevurderingManuelt() {
        var testscenario = opprettTestscenario("50");
        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);

        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var inntektsmeldinger = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                fpStartdato,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_OPPLYSNINGER_OM_MEDLEMSKAP);

        overstyrer.hentFagsak(saksnummer);
        overstyrer.ventPåOgVelgRevurderingBehandling();
        var overstyrMedlemskapsvilkaaret = new OverstyrMedlemskapsvilkaaret()
                .avvis(Avslagsårsak.SØKER_ER_IKKE_MEDLEM)
                .setBegrunnelse("avvist");
        overstyrer.overstyr(overstyrMedlemskapsvilkaaret);
        overstyrer.bekreftAksjonspunktMedDefaultVerdier(KontrollerManueltOpprettetRevurdering.class);
        overstyrer.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);
        assertThat(overstyrer.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.OPPHØR);

        beslutter.hentFagsak(saksnummer);
        beslutter.ventPåOgVelgRevurderingBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        beslutter.ventTilFagsakAvsluttet();

        assertThat(beslutter.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.OPPHØR);
        assertThat(beslutter.valgtBehandling.hentAvslagsarsak())
                .as("Avslagsårsak")
                .isEqualTo(Avslagsårsak.SØKER_ER_IKKE_MEDLEM);
        assertThat(beslutter.valgtBehandling.status)
                .as("Behandlingsstatus")
                .isEqualTo(BehandlingStatus.AVSLUTTET);
        logger.info("Status på sak: {}", beslutter.valgtFagsak.status().getKode());
    }

    @Test
    @DisplayName("Endringssøknad med ekstra uttaksperiode.")
    @Description("Førstegangsbehandling til positivt vedtak. Søker sender inn endringsøknad. Endring i uttak. Vedtak fortsatt løpende.")
    void endringssøknad() {
        var testscenario = opprettTestscenario("50");

        // Førstegangssøknad
        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        var søkerIdent = testscenario.personopplysninger().søkerIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);

        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        var inntektsmeldinger = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                fpStartdato,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilHistorikkinnslag(HistorikkinnslagType.VEDLEGG_MOTTATT);
        debugLoggBehandling(saksbehandler.valgtBehandling);
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.behandlinger)
                .as("Antall behandlinger")
                .hasSize(1);
        assertThat(saksbehandler.valgtBehandling.type)
                .as("Behandlingstype")
                .isEqualTo(BehandlingType.FØRSTEGANGSSØKNAD);
        saksbehandler.ventTilAvsluttetBehandling();
        debugFritekst("Ferdig med første behandling");

        // Endringssøknad
        var fordeling = generiskFordeling(
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(10).minusDays(1)));
        var søknadE = lagEndringssøknad(søkerAktørIdent, SøkersRolle.MOR, fordeling, saksnummer);
        var saksnummerE = fordel.sendInnSøknad(søknadE.build(), søkerAktørIdent, søkerIdent,
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_ENDRET);
        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen())
                .as("konsekvensForYtelsen")
                .contains(KonsekvensForYtelsen.ENDRING_I_UTTAK);

        saksbehandler.hentFagsak(saksnummerE);
        assertThat(saksbehandler.valgtFagsak.status())
                .as("Fagsak stauts")
                .isEqualTo(FagsakStatus.LØPENDE);
    }

    @Test
    @DisplayName("Endringssøknad med gradering")
    @Description("Førstegangsbehandling til positivt vedtak. Endringssøknad med gradering fra bruker. Vedtak fortsatt løpende.")
    void endringssøknadMedGradering() {
        var testscenario = opprettTestscenario("50");

        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        var søkerIdent = testscenario.personopplysninger().søkerIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var orgnr = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();

        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var inntektsmeldinger = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                fpStartdato,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker())
                .as("Antall uttaksperioder")
                .hasSize(4);

        // Endringssøknad
        var graderingFom = fødselsdato.plusWeeks(20);
        var graderingTom = fødselsdato.plusWeeks(23).minusDays(1);
        var fordelingGradering = fordelingEndringssøknadGradering(FELLESPERIODE, graderingFom, graderingTom,
                orgnr, 40);
        var endretSøknad = lagEndringssøknad(søkerAktørIdent, SøkersRolle.MOR,
                fordelingGradering, saksnummer);
        var saksnummerE = fordel.sendInnSøknad(endretSøknad.build(), søkerAktørIdent, søkerIdent,
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtFagsak.status())
                .as("Fagsak stauts")
                .isEqualTo(FagsakStatus.LØPENDE);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_ENDRET);
        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen())
                .as("Konsekvenser for ytelse")
                .contains(KonsekvensForYtelsen.ENDRING_I_UTTAK);
        assertThat(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker())
                .as("Antall uttaksperioder")
                .hasSize(5);
        for (UttakResultatPeriode periode : saksbehandler.valgtBehandling.getUttakResultatPerioder()
                .getPerioderSøker()) {
            assertThat(periode.getAktiviteter())
                    .as("Aktiviteter i uttaksperiode")
                    .hasSize(1);
        }
        assertThat(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker().get(4).getGraderingInnvilget())
                .as("Uttaksperiode 4 er en graderingsperiode")
                .isTrue();
    }

    @Test
    @DisplayName("Ikke få avslåg på innvilget perioder pga søknadsfrist")
    @Description("Ikke få avslåg på innvilget perioder pga søknadsfrist.")
    void ikke_avslag_pa_innvilget_perioder_pga_søknadsfrist_i_revurdering() {
        var testscenario = opprettTestscenario("74");
        var aktørIdSøker = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var fordeling = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(13).minusDays(1)));
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, aktørIdSøker, SøkersRolle.MOR)
                .medFordeling(fordeling)
                .medMottattDato(fødselsdato.plusWeeks(9));
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var im = InntektsmeldingForeldrepengeErketyper
                .makeInntektsmeldingFromTestscenario(testscenario, fpStartdato).get(0);
        fordel.sendInnInntektsmelding(im, testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        // Sender endringssøknad for å gi fagsaken en ny søknad mottatt dato
        var fordelingEndringssøknad = generiskFordeling(
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(13), fødselsdato.plusWeeks(14).minusDays(1)));
        var søknadE = lagEndringssøknad(aktørIdSøker, SøkersRolle.MOR, fordelingEndringssøknad, saksnummer)
                .medMottattDato(fødselsdato.plusWeeks(10));
        fordel.sendInnSøknad(søknadE.build(), testscenario, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.velgSisteBehandling();
        saksbehandler.ventTilAvsluttetBehandling();

        // Manuell behandling for å få endringssdato satt til første uttaksdag
        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_OPPLYSNINGER_OM_FORDELING);
        saksbehandler.velgSisteBehandling();
        saksbehandler.bekreftAksjonspunktbekreftelserer(
                saksbehandler.hentAksjonspunktbekreftelse(KontrollerManueltOpprettetRevurdering.class),
                saksbehandler.hentAksjonspunktbekreftelse(ForeslåVedtakManueltBekreftelse.class));
        saksbehandler.ventTilAvsluttetBehandling();

        assertThat(saksbehandler.valgtBehandling.hentUttaksperioder())
                .as("Forventer at alle uttaksperioder er innvilget")
                .allMatch(p -> p.getPeriodeResultatType().equals(PeriodeResultatType.INNVILGET));
    }

    @Test
    @DisplayName("Fortsatt få avslag på avslåtte perioder pga søknadsfrist i neste revurdering")
    @Description("Fortsatt få avslag på avslåtte perioder pga søknadsfrist i neste revurdering. Bruker papirsøknad for å kunne sette mottatt dato tilbake i tid")
    void fortsatt_tape_avslåtte_perioder_pga_søknadsfrist_i_revurdering() {
        var testscenario = opprettTestscenario("74");
        var aktørIdSøker = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var fordeling = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(13).minusDays(1)));
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, aktørIdSøker, SøkersRolle.MOR)
                .medFordeling(fordeling)
                //Ikke alle periodene skal avlås pga søknadsfrist
                .medMottattDato(fødselsdato.plusWeeks(18));
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var im = InntektsmeldingForeldrepengeErketyper
                .makeInntektsmeldingFromTestscenario(testscenario, fpStartdato).get(0);
        fordel.sendInnInntektsmelding(im, testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);

        var vurderSoknadsfristForeldrepengerBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class)
                .bekreftHarIkkeGyldigGrunn();
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.bekreftAksjonspunkt(bekreftelse);

        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Avslåtte uttaksperioder")
                .hasSizeGreaterThan(1);

        var fordelingEndringssøknad = generiskFordeling(
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(13), fødselsdato.plusWeeks(12).plusWeeks(2)));
        var søknadE = lagEndringssøknad(testscenario.personopplysninger().søkerAktørIdent(), SøkersRolle.MOR,
                fordelingEndringssøknad, saksnummer);
        fordel.sendInnSøknad(søknadE.build(), testscenario, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.velgSisteBehandling();
        saksbehandler.ventTilAvsluttetBehandling();

        // Manuell behandling for å få endringssdato satt til første uttaksdag
        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_OPPLYSNINGER_OM_FORDELING);
        saksbehandler.velgSisteBehandling();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Avslåtte uttaksperioder")
                .hasSizeGreaterThan(1);
    }


    @Test
    @DisplayName("Bare far har rett. Endringssøknad med utsettelse. Delvis aktivitetskrav")
    @Description("Bare far har rett (BFHR) sender inn en endringssøknad med 2 utsettelsesperioder. Aktivitetskravet for " +
            "første utsettesle er oppfylt men ikke for andre perioder. Andre periode avslås og trekker dager.")
    void farSøkerMedToAktiveArbeidsforholdOgEtInaktivtTest() {
        var testscenario = opprettTestscenario("60");
        var aktørIdFar = testscenario.personopplysninger().søkerAktørIdent();
        var fnrFar = testscenario.personopplysninger().søkerIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdatoFar = Virkedager.helgejustertTilMandag(fødselsdato.plusWeeks(6));
        var fordeling = generiskFordeling(
                uttaksperiode(FORELDREPENGER, fpStartdatoFar, fpStartdatoFar.plusWeeks(40).minusDays(1), ARBEID_OG_UTDANNING));

        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, aktørIdFar, SøkersRolle.FAR)
                .medFordeling(fordeling)
                .medRettigheter(RettigheterErketyper.harIkkeAleneomsorgOgAnnenpartIkkeRett())
                .medMottattDato(fødselsdato);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), aktørIdFar, fnrFar, DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var im = InntektsmeldingForeldrepengeErketyper
                .makeInntektsmeldingFromTestscenario(testscenario, fødselsdato);
        fordel.sendInnInntektsmeldinger(im, testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        var kontrollerAktivitetskravBekreftelse1 = saksbehandler
                .hentAksjonspunktbekreftelse(KontrollerAktivitetskravBekreftelse.class)
                .morErIAktivitetForAllePerioder()
                .setBegrunnelse("Mor er i aktivitet!");
        saksbehandler.bekreftAksjonspunkt(kontrollerAktivitetskravBekreftelse1);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);
        beslutter.hentFagsak(saksnummer);
        var bekreftelseFørstegangsbehandling = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelseFørstegangsbehandling);

        // Verifiseringer førstegangsbehandling
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(Stønadskonto.FORELDREPENGER).getSaldo())
                .as("Saldoen for stønadskonton FORELDREPENGER")
                .isZero();


        // Endringssøknad
        var fordelingUtsettelse = generiskFordeling(
                utsettelsesperiode(FRI, fpStartdatoFar.plusWeeks(32), fpStartdatoFar.plusWeeks(35).minusDays(1), ARBEID_OG_UTDANNING),
                uttaksperiode(FORELDREPENGER, fpStartdatoFar.plusWeeks(35), fpStartdatoFar.plusWeeks(37).minusDays(1), ARBEID_OG_UTDANNING),
                utsettelsesperiode(FRI, fpStartdatoFar.plusWeeks(37), fpStartdatoFar.plusWeeks(38).minusDays(1), ARBEID_OG_UTDANNING),
                uttaksperiode(FORELDREPENGER, fpStartdatoFar.plusWeeks(38), fpStartdatoFar.plusWeeks(43).minusDays(1), ARBEID_OG_UTDANNING));
        var endretSøknad = lagEndringssøknad(aktørIdFar, SøkersRolle.FAR,
                fordelingUtsettelse, saksnummer);
        var saksnummerE = fordel.sendInnSøknad(endretSøknad.build(), aktørIdFar, fnrFar,
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.hentFagsak(saksnummerE);
        var kontrollerAktivitetskravBekreftelse2 = saksbehandler
                .hentAksjonspunktbekreftelse(KontrollerAktivitetskravBekreftelse.class)
                .periodeIkkeAktivitetIkkeDokumentert(fpStartdatoFar.plusWeeks(37), fpStartdatoFar.plusWeeks(38).minusDays(1))
                .periodeIkkeAktivitetIkkeDokumentert(fpStartdatoFar.plusWeeks(38), fpStartdatoFar.plusWeeks(43).minusDays(1))
                .setBegrunnelse("Mor er bare delvis i aktivitet!");
        saksbehandler.bekreftAksjonspunkt(kontrollerAktivitetskravBekreftelse2);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);
        beslutter.hentFagsak(saksnummer);
        var bekreftelseRevurdering = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelseRevurdering);

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_ENDRET);
        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen())
                .as("Konsekvenser for ytelse")
                .contains(KonsekvensForYtelsen.ENDRING_I_UTTAK);

        // Verifiseringer på uttak
        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(Stønadskonto.FORELDREPENGER).getSaldo())
                .as("Saldoen for stønadskonton FORELDREPENGER")
                .isZero();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder().size())
                .as("Forventer at det er 2 avslåtte uttaksperioder")
                .isEqualTo(2);
        assertThat(saksbehandler.valgtBehandling.hentUttaksperiode(3).getPeriodeResultatÅrsak())
                .as("Perioderesultatårsak")
                .isInstanceOf(IkkeOppfyltÅrsak.class)
                .isEqualTo(AKTIVITETSKRAVET_ARBEID_I_KOMB_UTDANNING_IKKE_DOKUMENTERT);
        assertThat(saksbehandler.valgtBehandling.hentUttaksperiode(3).getAktiviteter().get(0).getTrekkdagerDesimaler())
                .as("Trekkdager")
                .isNotZero();

        assertThat(saksbehandler.valgtBehandling.hentUttaksperiode(4).getPeriodeResultatÅrsak())
                .as("Perioderesultatårsak")
                .isInstanceOf(IkkeOppfyltÅrsak.class)
                .isEqualTo(AKTIVITETSKRAVET_ARBEID_I_KOMB_UTDANNING_IKKE_DOKUMENTERT);
        assertThat(saksbehandler.valgtBehandling.hentUttaksperiode(4).getAktiviteter().get(0).getTrekkdagerDesimaler())
                .as("Trekkdager")
                .isNotZero();


    }

}



