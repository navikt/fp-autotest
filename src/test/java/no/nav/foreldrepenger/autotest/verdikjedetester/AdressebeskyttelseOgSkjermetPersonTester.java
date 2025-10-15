package no.nav.foreldrepenger.autotest.verdikjedetester;

import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.FEDREKVOTE;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.MØDREKVOTE;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.uttaksperiode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Saksbehandler;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.FagsakKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkType;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler;
import no.nav.foreldrepenger.generator.soknad.maler.UttaksperiodeType;
import no.nav.foreldrepenger.vtp.kontrakter.v2.Adressebeskyttelse;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;
import no.nav.vedtak.exception.ManglerTilgangException;

@Tag("verdikjede")
class AdressebeskyttelseOgSkjermetPersonTester {

    private static final Logger LOG = LoggerFactory.getLogger(AdressebeskyttelseOgSkjermetPersonTester.class);
    private Saksbehandler saksbehandler6;
    private Saksbehandler saksbehandlerEgenAnsatt;

    private FagsakKlient fpsakKlientBeslutter;
    private FagsakKlient fpsakKlientSaksbehandler;
    private FagsakKlient fpsakKlientSaksbehandler6;
    private FagsakKlient fpsakKlientSaksbehandler7;
    private FagsakKlient fpsakKlientSaksbehandlerEgenAnsatt;
    private FagsakKlient fpsakKlientOverstyrer;
    private FagsakKlient fpsakKlientVeileder;
    private FagsakKlient fpsakKlientDrifter;

    @BeforeEach
    void setUp() {
        saksbehandler6 = new Saksbehandler(SaksbehandlerRolle.SAKSBEHANDLER_KODE_6);
        saksbehandlerEgenAnsatt = new Saksbehandler(SaksbehandlerRolle.SAKSBEHANDLER_EGEN_ANSATT);

        fpsakKlientSaksbehandler6 = new FagsakKlient(SaksbehandlerRolle.SAKSBEHANDLER_KODE_6);
        fpsakKlientSaksbehandlerEgenAnsatt = new FagsakKlient(SaksbehandlerRolle.SAKSBEHANDLER_EGEN_ANSATT);
        fpsakKlientBeslutter = new FagsakKlient(SaksbehandlerRolle.BESLUTTER);
        fpsakKlientSaksbehandler = new FagsakKlient(SaksbehandlerRolle.SAKSBEHANDLER);
        fpsakKlientSaksbehandler7 = new FagsakKlient(SaksbehandlerRolle.SAKSBEHANDLER_KODE_7);
        fpsakKlientOverstyrer = new FagsakKlient(SaksbehandlerRolle.OVERSTYRER);
        fpsakKlientVeileder = new FagsakKlient(SaksbehandlerRolle.VEILEDER);
        fpsakKlientDrifter = new FagsakKlient(SaksbehandlerRolle.DRIFTER);
    }

    @Test
    void adressebeskyttet_strengt_fortrolig_kun_saksbehandles_av_sakbehanlder_med_strengt_fortrolig_ad_gruppe() {
        var familie = FamilieGenerator.ny(SaksbehandlerRolle.SAKSBEHANDLER_KODE_6)
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .addressebeskyttelse(Adressebeskyttelse.STRENGT_FORTROLIG)
                        .build())
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .addressebeskyttelse(Adressebeskyttelse.UGRADERT)
                        .build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .build();
        var termindato = LocalDate.now().minusWeeks(2);
        var søknadMor = SøknadForeldrepengerMaler.lagSøknadForeldrepengerTermin(termindato, BrukerRolle.MOR)
                .medUttaksplan(List.of(
                        uttaksperiode(FORELDREPENGER_FØR_FØDSEL, termindato.minusWeeks(3), termindato.minusDays(1)),
                        uttaksperiode(MØDREKVOTE, termindato, termindato.plusWeeks(6).minusDays(1))
                ))
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .build();
        var mor = familie.mor();
        var saksnummerMor = mor.søk(søknadMor);

        saksbehandler6.hentFagsak(saksnummerMor);
        saksbehandler6.ventTilHistorikkinnslag(HistorikkType.MIN_SIDE_ARBEIDSGIVER);
        mor.arbeidsgiver().sendInntektsmeldingerFP(saksnummerMor, termindato);
        saksbehandler6.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        LOG.info("Sjekker om fagsak er beskyttet ...");
        assertThatThrownBy(() -> fpsakKlientSaksbehandler.hentFagsakFull(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> fpsakKlientSaksbehandler7.hentFagsakFull(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> fpsakKlientSaksbehandlerEgenAnsatt.hentFagsakFull(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> fpsakKlientOverstyrer.hentFagsakFull(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> fpsakKlientVeileder.hentFagsakFull(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> fpsakKlientDrifter.hentFagsakFull(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> fpsakKlientBeslutter.hentFagsakFull(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        LOG.info("Fagsak er beskyttet!");

        var far = familie.far();
        var søknadFar = SøknadForeldrepengerMaler.lagSøknadForeldrepengerTermin(termindato, BrukerRolle.FAR)
                .medUttaksplan(List.of(
                        uttaksperiode(FEDREKVOTE, termindato, termindato.plusWeeks(1).minusDays(1), 100, UttaksperiodeType.SAMTIDIGUTTAK),
                        uttaksperiode(FEDREKVOTE, termindato.plusWeeks(6), termindato.plusWeeks(9).minusDays(1))
                ))
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.mor()))
                .build();
        var saksnummerFar = far.søk(søknadFar);

        saksbehandler6.hentFagsak(saksnummerFar);
        saksbehandler6.ventTilHistorikkinnslag(HistorikkType.MIN_SIDE_ARBEIDSGIVER);
        far.arbeidsgiver().sendInntektsmeldingerFP(saksnummerFar, termindato);
        saksbehandler6.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        // Hele fagsaken skal være beskyttet og krever KODE_6 tilgang, selv om far ikke har beskyttet addresse
        LOG.info("Sjekker om fagsak er beskyttet ...");
        assertThatThrownBy(() -> fpsakKlientSaksbehandler.hentFagsakFull(saksnummerFar)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> fpsakKlientSaksbehandler7.hentFagsakFull(saksnummerFar)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> fpsakKlientSaksbehandlerEgenAnsatt.hentFagsakFull(saksnummerFar)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> fpsakKlientOverstyrer.hentFagsakFull(saksnummerFar)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> fpsakKlientVeileder.hentFagsakFull(saksnummerFar)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> fpsakKlientDrifter.hentFagsakFull(saksnummerFar)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> fpsakKlientBeslutter.hentFagsakFull(saksnummerFar)).isExactlyInstanceOf(ManglerTilgangException.class);
        LOG.info("Fagsak er beskyttet!");

        var morsSakInnsyn = mor.innsyn().hentFpSakUtenÅpenBehandling(saksnummerMor);
        assertThat(morsSakInnsyn).isNotNull();
        assertThat(morsSakInnsyn.gjeldendeVedtak().perioder()).isNotEmpty();
        assertThat(morsSakInnsyn.saksnummer().value()).isEqualTo(saksnummerMor.value());
        assertThat(morsSakInnsyn.annenPart()).isNotNull(); // Far er ikke beskyttet
        assertThat(morsSakInnsyn.annenPart().fnr().value()).isEqualTo(far.fødselsnummer().value()); // Far er ikke beskyttet
        assertThat(mor.innsyn().hentAnnenpartsSak(far.fødselsnummer(), termindato)).isNotNull(); // Far er ikke beskyttet
        assertThat(mor.innsyn().hentAnnenpartsSak(far.fødselsnummer(), termindato).perioder()).isNotEmpty(); // Far er ikke beskyttet

        var farsSakInnsyn = far.innsyn().hentFpSakUtenÅpenBehandling(saksnummerFar);
        assertThat(farsSakInnsyn).isNotNull();
        assertThat(farsSakInnsyn.gjeldendeVedtak().perioder()).isNotEmpty();
        assertThat(farsSakInnsyn.saksnummer().value()).isEqualTo(saksnummerFar.value());
        assertThat(farsSakInnsyn.annenPart()).isNull(); // Mor er beskyttet
        assertThat(far.innsyn().hentAnnenpartsSak(mor.fødselsnummer(), termindato)).isNull(); // Mor er beskyttet
    }

    @Test
    void skjermet_person_må_behandles_av_saksbehandler_med_egen_ansatt_ad_rolle() {
        var familie = FamilieGenerator.ny(SaksbehandlerRolle.SAKSBEHANDLER_EGEN_ANSATT)
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .erSkjermet(true)
                        .build())
                .forelder(far().build())
                .build();
        var termindato = LocalDate.now().minusWeeks(2);
        var søknadMor = SøknadForeldrepengerMaler.lagSøknadForeldrepengerTermin(termindato, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .build();
        var mor = familie.mor();
        var saksnummerMor = mor.søk(søknadMor);

        saksbehandlerEgenAnsatt.hentFagsak(saksnummerMor);
        saksbehandlerEgenAnsatt.ventTilHistorikkinnslag(HistorikkType.MIN_SIDE_ARBEIDSGIVER);
        mor.arbeidsgiver().sendInntektsmeldingerFP(saksnummerMor, termindato);
        saksbehandlerEgenAnsatt.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        LOG.info("Sjekker om fagsak er beskyttet ...");
        assertThatThrownBy(() -> fpsakKlientSaksbehandler.hentFagsakFull(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> fpsakKlientSaksbehandler6.hentFagsakFull(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> fpsakKlientSaksbehandler7.hentFagsakFull(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> fpsakKlientOverstyrer.hentFagsakFull(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> fpsakKlientVeileder.hentFagsakFull(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> fpsakKlientDrifter.hentFagsakFull(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> fpsakKlientBeslutter.hentFagsakFull(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        LOG.info("Fagsak er beskyttet!");

        var innsynSak = mor.innsyn().hentFpSakUtenÅpenBehandling(saksnummerMor);
        assertThat(innsynSak).isNotNull();
        assertThat(innsynSak.saksnummer().value()).isEqualTo(saksnummerMor.value());
    }
}
