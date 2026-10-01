package no.nav.foreldrepenger.autotest.verdikjedetester;

import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadEndringMaler.lagEndringssøknad;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerTerminFødsel;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.utsettelsesperiode;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.uttaksperiode;
import static no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType.FEDREKVOTE;
import static no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType.MØDREKVOTE;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.base.VerdikjedeTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpoversikt.InnsynKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettUttaksperioderManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrMedlemskapsvilkaaret;
import no.nav.foreldrepenger.autotest.util.vent.Vent;
import no.nav.foreldrepenger.generator.familie.Familie;
import no.nav.foreldrepenger.generator.familie.Søker;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektGenerator;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType;
import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.Rolle;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.UttakDto;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.UttakPeriodeDto;
import no.nav.foreldrepenger.kontrakter.fpoversikt.FpSak;
import no.nav.foreldrepenger.soknad.kontrakt.BrukerRolle;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.UtsettelsesÅrsak;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.Uttaksplanperiode;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.FamilierelasjonDto;

@Tag("verdikjede")
@Tag("foreldrepenger")
class FellesUttaksplan extends VerdikjedeTestBase {

    private final InnsynKlient innsyn = new InnsynKlient();

    @Test
    @DisplayName("F01: Ingen av foreldrene har søkt")
    @Description("Registrerer barnet med begge foreldre og henter begge perspektiver uten å opprette saker.")
    void f01_ingen_har_søkt() {
        var familie = nyFamilie();
        var f01 = stoppunkt("F01: ingen saker, begge planer kan være null", familie);
    }

    @Test
    @DisplayName("F02, F03: Mor søker med fars perioder, før og etter vedtak")
    @Description("Mor sender M1/F1. Fars forslag kan inspiseres mens mor venter på inntektsmelding og etter mors vedtak.")
    void mor_sender_inn_fars_perioder() {
        var familie = nyFamilie();
        var morsSak = søk(familie, BrukerRolle.MOR, familie.m1(), familie.f1());
        var f02 = stoppunkt("F02: mors søknad er ubehandlet", familie);

        innvilg(familie.mor(), morsSak, familie.m1());
        var f03 = stoppunkt("F03: mor har vedtak, far har bare forslag", familie);
    }

    @Test
    @DisplayName("F04: Mor får delvis innvilget uttak")
    @Description("Mor søker en uke mer enn mødrekvoten. Overskytende uttak avslås, mens forslaget F1 beholdes.")
    void f04_mor_får_delvis_avslag() {
        var familie = nyFamilie();
        var morsPerioder = familie.morsPerioder(16);
        var morsSak = søk(familie, BrukerRolle.MOR, morsPerioder, familie.f1());
        sendInntektsmelding(familie.mor(), morsSak, morsPerioder);

        var manueltUttak = new FastsettUttaksperioderManueltBekreftelse();
        if (saksbehandler.harAksjonspunkt(manueltUttak.aksjonspunktKode())) {
            saksbehandler.bekreftAksjonspunkt(saksbehandler.hentAksjonspunktbekreftelse(manueltUttak)
                    .avslåManuellePerioderMedPeriodeResultatÅrsak(PeriodeResultatÅrsak.IKKE_STØNADSDAGER_IGJEN));
            foreslårOgFatterVedtakVenterTilAvsluttetBehandling(morsSak, false, false, false);
        } else {
            saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        }
        familie.mor().innsyn().hentFpSakUtenÅpenBehandling(morsSak);
        var f04 = stoppunkt("F04: mors vedtak inneholder avslått uttak", familie);
    }

    @Test
    @DisplayName("F05: Mor søker uten forslag til far")
    @Description("Sender M1 med eksplisitt tomt forslag til annen part. Fars egen del skal være tom.")
    void f05_tomt_forslag_til_far() {
        var familie = nyFamilie();
        var morsSak = søk(familie, BrukerRolle.MOR, familie.m1(), List.of());
        var f05 = stoppunkt("F05: ingen foreslåtte perioder til far", familie);
    }

    @Test
    @DisplayName("F06: Mor erstatter F1 med F3 før far søker")
    @Description("To førstegangssøknader på samme sak, uten inntektsmelding. Den andre erstatter bare forslaget til far.")
    void f06_mor_erstatter_forslaget() {
        var familie = nyFamilie();
        var morsSak = søk(familie, BrukerRolle.MOR, familie.m1(), familie.f1());
        var førErstatning = stoppunkt("F06: første forslag F1", familie);

        søk(familie, BrukerRolle.MOR, familie.m1(), familie.f3(), morsSak);
        var f06 = stoppunkt("F06: F3 erstatter F1", familie);
    }

    @Test
    @DisplayName("F07: Far søker først og foreslår mors perioder")
    @Description("Sender F2/M2 fra far. Mor har ingen sak og kan hente M2 som sitt eget forslag.")
    void f07_far_søker_først() {
        var familie = nyFamilie();
        var farsSak = søk(familie, BrukerRolle.FAR, familie.f2(), familie.m2());
        var f07 = stoppunkt("F07: far har søkt først", familie);
    }

    @Test
    @DisplayName("B01, B03, B04: Begge søker før mors vedtak, deretter innvilges begge")
    @Description("Mor sender M1/F1 og far F2/M2. Egne søknader inspiseres før inntektsmelding, deretter begge vedtak.")
    void b01_b03_b04_begge_søker_før_vedtak() {
        var familie = nyFamilie();
        var morsSak = søk(familie, BrukerRolle.MOR, familie.m1(), familie.f1());
        var farsSak = søk(familie, BrukerRolle.FAR, familie.f2(), familie.m2());
        var b01_b03 = stoppunkt("B01/B03: begge har egne ubehandlede søknader", familie);

        innvilg(familie.mor(), morsSak, familie.m1());
        var morHarVedtak = stoppunkt("B03: mor har vedtak, fars F2 er ubehandlet", familie);

        innvilg(familie.far(), farsSak, familie.f2());
        var b04 = stoppunkt("B04: begge har vedtak", familie);
    }

    @Test
    @DisplayName("B02: Mor har vedtak før far søker")
    @Description("Mor får vedtak M1. Far sender deretter F2/M2 og får sitt eget datagrunnlag i stedet for F1.")
    void b02_mor_har_vedtak_før_far_søker() {
        var familie = nyFamilie();
        var morsSak = søk(familie, BrukerRolle.MOR, familie.m1(), familie.f1());
        innvilg(familie.mor(), morsSak, familie.m1());
        var førFarSøker = stoppunkt("B02: mor har vedtak, far har F1", familie);

        var farsSak = søk(familie, BrukerRolle.FAR, familie.f2(), familie.m2());
        var b02 = stoppunkt("B02: fars egen F2 erstatter forslaget F1", familie);
    }

    @Test
    @DisplayName("B06: Far får avslag uten innvilgede perioder")
    @Description("Mor foreslår F1. Fars medlemsvilkår overstyres til avslag før vedtak, slik at eget vedtak blokkerer F1.")
    void b06_far_får_avslag() {
        var familie = nyFamilie();
        var morsSak = søk(familie, BrukerRolle.MOR, familie.m1(), familie.f1());
        innvilg(familie.mor(), morsSak, familie.m1());
        var farsPerioder = familie.farsPerioder(20, 16);
        var farsSak = søk(familie, BrukerRolle.FAR, farsPerioder, familie.m2());
        sendInntektsmelding(familie.far(), farsSak, farsPerioder);

        // Kvoten er overskredet slik at behandlingen ikke rekker et automatisk innvilgelsesvedtak.
        overstyrer.hentFagsak(farsSak);
        overstyrer.overstyr(new OverstyrMedlemskapsvilkaaret()
                .avvis(Avslagsårsak.SØKER_ER_IKKE_MEDLEM)
                .setBegrunnelse("B06: avslag for å inspisere prioritering foran mors forslag"));
        overstyrer.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());
        beslutter.hentFagsak(farsSak);
        var vedtak = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse())
                .godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(vedtak);
        familie.far().innsyn().hentFpSakUtenÅpenBehandling(farsSak);
        var b06 = stoppunkt("B06: fars avslag skal blokkere F1", familie);
    }

    @ParameterizedTest(name = "E01/E02: farHarEgneData={0}")
    @ValueSource(booleans = {false, true})
    @DisplayName("E01, E02: Mor endrer M1/F1 til M3/F3, før og etter vedtak")
    @Description("Mors revurdering stoppes før vedtak. Kjøres med og uten fars egne data for å inspisere prioriteringen foran F3.")
    void e01_e02_mor_endrer_egen_plan_og_forslag(boolean farHarEgneData) {
        var familie = nyFamilie();
        var morsSak = søk(familie, BrukerRolle.MOR, familie.m1(), familie.f1());
        innvilg(familie.mor(), morsSak, familie.m1());
        if (farHarEgneData) {
            var farsSak = søk(familie, BrukerRolle.FAR, familie.f2(), familie.m2());
            innvilg(familie.far(), farsSak, familie.f2());
        }

        sendEndring(familie, BrukerRolle.MOR, morsSak, familie.m3(), familie.f3(), familie.fødselsdato().plusWeeks(8));
        var e01 = stoppunkt("E01: M1 er fortsatt gjeldende, F3 er lagret", familie);

        ferdigbehandleEndring(familie.mor(), morsSak);
        var e02 = stoppunkt("E02: M3 er vedtatt, F3 er fortsatt forslag", familie);
    }

    @Test
    @DisplayName("E03: Far endrer F2/M2 til F4/M4")
    @Description("Begge har vedtak. Fars endring inspiseres før og etter vedtak; mors egne perioder skal vinne over M4.")
    void e03_far_endrer_egen_plan_og_forslag() {
        var familie = nyFamilie();
        var morsSak = søk(familie, BrukerRolle.MOR, familie.m1(), familie.f1());
        innvilg(familie.mor(), morsSak, familie.m1());
        var farsSak = søk(familie, BrukerRolle.FAR, familie.f2(), familie.m2());
        innvilg(familie.far(), farsSak, familie.f2());

        sendEndring(familie, BrukerRolle.FAR, farsSak, familie.f4(), familie.m4(), familie.fødselsdato().plusWeeks(25));
        var førVedtak = stoppunkt("E03: F2 er fortsatt gjeldende, M4 overstyrer ikke mor", familie);

        ferdigbehandleEndring(familie.far(), farsSak);
        var etterVedtak = stoppunkt("E03: F4 er vedtatt", familie);
    }

    @ParameterizedTest(name = "E04/E06: farHarEgneData={0}")
    @ValueSource(booleans = {false, true})
    @DisplayName("E04, E06: Mor endrer bare forslaget, og tømmer det")
    @Description("Kjøres både uten fars sak og med fars vedtak. M1 beholdes, F1 erstattes av F3 og deretter tom liste.")
    void e04_e06_mor_endrer_og_tømmer_forslaget(boolean farHarEgneData) {
        var familie = nyFamilie();
        var morsSak = søk(familie, BrukerRolle.MOR, familie.m1(), familie.f1());
        innvilg(familie.mor(), morsSak, familie.m1());
        if (farHarEgneData) {
            var farsSak = søk(familie, BrukerRolle.FAR, familie.f2(), familie.m2());
            innvilg(familie.far(), farsSak, familie.f2());
        }

        sendEndring(familie, BrukerRolle.MOR, morsSak, familie.m1(), familie.f3(), familie.fødselsdato().minusWeeks(3));
        var e04 = stoppunkt("E04: bare forslaget til far er endret", familie);
        ferdigbehandleEndring(familie.mor(), morsSak);

        sendEndring(familie, BrukerRolle.MOR, morsSak, familie.m1(), List.of(), familie.fødselsdato().minusWeeks(3));
        var e06 = stoppunkt("E06: forslaget til far er tømt", familie);
        ferdigbehandleEndring(familie.mor(), morsSak);
        var etterTømming = stoppunkt("E06: tomt forslag etter vedtak", familie);
    }

    @ParameterizedTest(name = "E05: morHarEgneData={0}")
    @ValueSource(booleans = {false, true})
    @DisplayName("E05: Far endrer bare forslaget til mor")
    @Description("Far beholder F2 og erstatter M2 med M4. Kjøres både uten mors sak og med mors vedtak.")
    void e05_far_endrer_bare_forslaget(boolean morHarEgneData) {
        var familie = nyFamilie();
        if (morHarEgneData) {
            var morsSak = søk(familie, BrukerRolle.MOR, familie.m1(), familie.f1());
            innvilg(familie.mor(), morsSak, familie.m1());
        }
        var farsSak = søk(familie, BrukerRolle.FAR, familie.f2(), familie.m2());
        innvilg(familie.far(), farsSak, familie.f2());

        sendEndring(familie, BrukerRolle.FAR, farsSak, familie.f2(), familie.m4(), familie.fødselsdato().plusWeeks(6));
        var e05 = stoppunkt("E05: bare forslaget til mor er endret", familie);
        ferdigbehandleEndring(familie.far(), farsSak);
        var etterVedtak = stoppunkt("E05: M4 etter vedtak", familie);
    }

    @Test
    @DisplayName("E07: Mor flytter, splitter og forkorter eget uttak")
    @Description("M1 erstattes av M3 med fremtidige opphold, flyttet start og kortere samlet uttak. F1 beholdes uendret.")
    void e07_mor_flytter_splitter_og_forkorter() {
        var familie = nyFamilie();
        var morsSak = søk(familie, BrukerRolle.MOR, familie.m1(), familie.f1());
        innvilg(familie.mor(), morsSak, familie.m1());

        sendEndring(familie, BrukerRolle.MOR, morsSak, familie.m3(), familie.f1(), familie.fødselsdato().plusWeeks(8));
        var førVedtak = stoppunkt("E07: M3 er sendt, F1 er uendret", familie);
        ferdigbehandleEndring(familie.mor(), morsSak);
        var etterVedtak = stoppunkt("E07: flyttet og forkortet uttak er vedtatt", familie);
    }

    private Saksnummer søk(Testfamilie familie, BrukerRolle rolle, List<Periode> egne, List<Periode> forslag) {
        return søk(familie, rolle, egne, forslag, null);
    }

    private Saksnummer søk(Testfamilie familie, BrukerRolle rolle, List<Periode> egne, List<Periode> forslag, Saksnummer eksisterendeSak) {
        var søker = familie.søker(rolle);
        var forrigeOppdatering = eksisterendeSak == null ? LocalDateTime.MIN : hentSak(søker, eksisterendeSak).oppdatertTidspunkt();
        var søknad = lagSøknadForeldrepengerTerminFødsel(familie.fødselsdato(), familie.fødselsdato(), rolle)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.annenPart(rolle)))
                .medUttaksplan(egne.stream().map(Periode::tilSøknadsperiode).toList())
                .medPerioder(fellesperioder(rolle, egne, forslag));
        var saksnummer = eksisterendeSak == null ? søker.søk(søknad) : søker.søk(søknad, eksisterendeSak);
        saksbehandler.hentFagsak(saksnummer);
        ventPåOppdatertSak(søker, saksnummer, forrigeOppdatering);
        return saksnummer;
    }

    private void sendInntektsmelding(Søker søker, Saksnummer saksnummer, List<Periode> perioder) {
        ventPåInntektsmeldingForespørsel(saksnummer);
        søker.arbeidsgiver().sendInntektsmeldingerFP(saksnummer, perioder.getFirst().fom());
        saksbehandler.hentFagsak(saksnummer);
    }

    private void innvilg(Søker søker, Saksnummer saksnummer, List<Periode> perioder) {
        sendInntektsmelding(søker, saksnummer, perioder);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        søker.innsyn().hentFpSakUtenÅpenBehandling(saksnummer);
    }

    private void sendEndring(Testfamilie familie, BrukerRolle rolle, Saksnummer saksnummer,
                            List<Periode> egne, List<Periode> forslag, LocalDate endringFra) {
        var søker = familie.søker(rolle);
        saksbehandler.hentFagsak(saksnummer);
        // Manuell revurdering gir et stabilt stopp på foreslå vedtak, også når endringen ellers ville gått automatisk.
        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_OPPLYSNINGER_OM_FORDELING);
        saksbehandler.velgSisteBehandling();
        var åpenSak = Vent.på(() -> {
            var sak = hentSak(søker, saksnummer);
            return sak != null && sak.åpenBehandling() != null ? sak : null;
        }, () -> "Venter på åpen revurdering i fpoversikt: " + saksnummer.value(), 30, 3000);
        var forrigeOppdatering = åpenSak.oppdatertTidspunkt();
        var endredePerioder = egne.stream().filter(p -> !p.tom().isBefore(endringFra)).toList();
        var perioder = new ArrayList<>(fellesperioder(rolle, endredePerioder, forslag));
        var legacy = new ArrayList<Uttaksplanperiode>(endredePerioder.stream()
                .map(Periode::tilSøknadsperiode).toList());
        if (egne.stream().noneMatch(p -> !endringFra.isBefore(p.fom()) && !endringFra.isAfter(p.tom()))) {
            legacy.addFirst(utsettelsesperiode(UtsettelsesÅrsak.FRI, endringFra, endringFra.plusDays(4)));
            var utsettelse = new UttakDto(rolle == BrukerRolle.MOR ? Rolle.MOR : Rolle.FAR_MEDMOR, null,
                    FellesUttaksplanDto.UtsettelseÅrsak.FRI, null, null, null, null, false, null);
            perioder.add(new UttakPeriodeDto(endringFra, endringFra.plusDays(4), utsettelse, null, null));
        }
        perioder.sort(Comparator.comparing(UttakPeriodeDto::fom));
        søker.søk(lagEndringssøknad(søker.førstegangssøknad(), saksnummer, legacy)
                .medPerioder(perioder));
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        ventPåOppdatertSak(søker, saksnummer, forrigeOppdatering);
    }

    private void ferdigbehandleEndring(Søker søker, Saksnummer saksnummer) {
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakManueltBekreftelse());
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        søker.innsyn().hentFpSakUtenÅpenBehandling(saksnummer);
    }

    private FpSak hentSak(Søker søker, Saksnummer saksnummer) {
        return innsyn.hentSaker(søker.fødselsnummer()).foreldrepenger().stream()
                .filter(sak -> sak.saksnummer().equals(saksnummer)).findFirst().orElse(null);
    }

    private void ventPåOppdatertSak(Søker søker, Saksnummer saksnummer, LocalDateTime forrigeOppdatering) {
        Vent.på(() -> {
            var sak = hentSak(søker, saksnummer);
            return sak != null && sak.oppdatertTidspunkt().isAfter(forrigeOppdatering);
        }, () -> "Venter på oppdatert sak i fpoversikt: " + saksnummer.value(), 30, 3000);
    }

    @Step("{scenario}")
    private Planer stoppunkt(String scenario, Testfamilie familie) {
        var morsPlan = innsyn.hentFellesUttaksplan(familie.mor().fødselsnummer(), familie.far().fødselsnummer(), familie.fødselsdato());
        var farsPlan = innsyn.hentFellesUttaksplan(familie.far().fødselsnummer(), familie.mor().fødselsnummer(), familie.fødselsdato());
        // Sett breakpoint her for å inspisere begge planer eller åpne frontend før neste steg.
        LOG.info("Debugstoppunkt: {}", scenario);
        return new Planer(morsPlan, farsPlan);
    }

    private static Testfamilie nyFamilie() {
        var fødselsdato = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(3);
        var familie = FamilieGenerator.ny()
                .forelder(mor().inntekt(InntektGenerator.ny().arbeidMedOpptjeningUnder6G().build()).build())
                .forelder(far().inntekt(InntektGenerator.ny().arbeidMedOpptjeningUnder6G().build()).build())
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE)
                .barn(fødselsdato)
                .build();
        return new Testfamilie(familie, fødselsdato);
    }

    private static List<UttakPeriodeDto> fellesperioder(BrukerRolle rolle, List<Periode> egne, List<Periode> forslag) {
        var søkerRolle = rolle == BrukerRolle.MOR ? Rolle.MOR : Rolle.FAR_MEDMOR;
        var annenRolle = rolle == BrukerRolle.MOR ? Rolle.FAR_MEDMOR : Rolle.MOR;
        return Stream.concat(
                egne.stream().map(p -> new UttakPeriodeDto(p.fom(), p.tom(), p.uttak(søkerRolle), null, null)),
                forslag.stream().map(p -> new UttakPeriodeDto(p.fom(), p.tom(), null, p.uttak(annenRolle), null)))
                .sorted(Comparator.comparing(UttakPeriodeDto::fom)).toList();
    }

    private record Planer(no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto mor,
                          no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto far) {
    }

    private record Periode(LocalDate fom, LocalDate tom, KontoType konto) {
        Uttaksplanperiode tilSøknadsperiode() {
            return uttaksperiode(konto, fom, tom);
        }

        UttakDto uttak(Rolle rolle) {
            return new UttakDto(rolle, konto, null, null, null, null, null, false, null);
        }
    }

    private record Testfamilie(Familie familie, LocalDate fødselsdato) {
        Søker mor() {
            return familie.mor();
        }

        Søker far() {
            return familie.far();
        }

        Søker søker(BrukerRolle rolle) {
            return rolle == BrukerRolle.MOR ? mor() : far();
        }

        Søker annenPart(BrukerRolle rolle) {
            return rolle == BrukerRolle.MOR ? far() : mor();
        }

        List<Periode> morsPerioder(int uker) {
            return List.of(new Periode(fødselsdato.minusWeeks(3), fødselsdato.minusDays(3), FORELDREPENGER_FØR_FØDSEL),
                    new Periode(fødselsdato, fødselsdato.plusWeeks(6).minusDays(3), MØDREKVOTE),
                    new Periode(fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(uker + 2).minusDays(3), MØDREKVOTE));
        }

        List<Periode> farsPerioder(int startUke, int uker) {
            // Første uttak innen fire uker gjør at far ikke stoppes av «søkt for tidlig».
            return List.of(new Periode(fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(8).minusDays(3), FEDREKVOTE),
                    new Periode(fødselsdato.plusWeeks(startUke), fødselsdato.plusWeeks(startUke + uker - 2).minusDays(3), FEDREKVOTE));
        }

        List<Periode> m1() {
            return morsPerioder(15);
        }

        List<Periode> m2() {
            return morsPerioder(12);
        }

        List<Periode> m3() {
            return List.of(new Periode(fødselsdato.minusWeeks(3), fødselsdato.minusDays(3), FORELDREPENGER_FØR_FØDSEL),
                    new Periode(fødselsdato, fødselsdato.plusWeeks(6).minusDays(3), MØDREKVOTE),
                    new Periode(fødselsdato.plusWeeks(9), fødselsdato.plusWeeks(13).minusDays(3), MØDREKVOTE),
                    new Periode(fødselsdato.plusWeeks(14), fødselsdato.plusWeeks(18).minusDays(3), MØDREKVOTE));
        }

        List<Periode> m4() {
            return morsPerioder(10);
        }

        List<Periode> f1() {
            return farsPerioder(18, 15);
        }

        List<Periode> f2() {
            return farsPerioder(20, 15);
        }

        List<Periode> f3() {
            return farsPerioder(22, 15);
        }

        List<Periode> f4() {
            return List.of(new Periode(fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(8).minusDays(3), FEDREKVOTE),
                    new Periode(fødselsdato.plusWeeks(20), fødselsdato.plusWeeks(25).minusDays(3), FEDREKVOTE),
                    new Periode(fødselsdato.plusWeeks(26), fødselsdato.plusWeeks(33).minusDays(3), FEDREKVOTE));
        }
    }
}
