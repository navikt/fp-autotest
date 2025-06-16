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

import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Beslutter;
import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Saksbehandler;
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
    private Beslutter beslutter;
    private Saksbehandler saksbehandler;
    private Saksbehandler saksbehandler6;
    private Saksbehandler saksbehandler7;
    private Saksbehandler saksbehandlerEgenAnsatt;
    private Saksbehandler overstyrer;
    private Saksbehandler veileder;
    private Saksbehandler drifter;

    @BeforeEach
    void setUp() {
        beslutter = new Beslutter();
        saksbehandler = new Saksbehandler(SaksbehandlerRolle.SAKSBEHANDLER);
        saksbehandler6 = new Saksbehandler(SaksbehandlerRolle.SAKSBEHANDLER_KODE_6);
        saksbehandler7 = new Saksbehandler(SaksbehandlerRolle.SAKSBEHANDLER_KODE_7);
        saksbehandlerEgenAnsatt = new Saksbehandler(SaksbehandlerRolle.SAKSBEHANDLER_EGEN_ANSATT);
        overstyrer = new Saksbehandler(SaksbehandlerRolle.OVERSTYRER);
        veileder = new Saksbehandler(SaksbehandlerRolle.VEILEDER);
        drifter = new Saksbehandler(SaksbehandlerRolle.DRIFTER);
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
        saksbehandler6.ventTilFagsakLøpende();

        assertThatThrownBy(() -> saksbehandler.hentFagsak(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> saksbehandler7.hentFagsak(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> saksbehandlerEgenAnsatt.hentFagsak(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> overstyrer.hentFagsak(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> veileder.hentFagsak(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> drifter.hentFagsak(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> beslutter.hentFagsak(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);

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
        assertThatThrownBy(() -> saksbehandler.hentFagsak(saksnummerFar)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> saksbehandler7.hentFagsak(saksnummerFar)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> saksbehandlerEgenAnsatt.hentFagsak(saksnummerFar)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> overstyrer.hentFagsak(saksnummerFar)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> veileder.hentFagsak(saksnummerFar)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> drifter.hentFagsak(saksnummerFar)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> beslutter.hentFagsak(saksnummerFar)).isExactlyInstanceOf(ManglerTilgangException.class);

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
        saksbehandlerEgenAnsatt.ventTilFagsakLøpende();

        assertThatThrownBy(() -> saksbehandler.hentFagsak(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> saksbehandler6.hentFagsak(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> saksbehandler7.hentFagsak(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> overstyrer.hentFagsak(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> veileder.hentFagsak(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> drifter.hentFagsak(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);
        assertThatThrownBy(() -> beslutter.hentFagsak(saksnummerMor)).isExactlyInstanceOf(ManglerTilgangException.class);

        var innsynSak = mor.innsyn().hentFpSakUtenÅpenBehandling(saksnummerMor);
        assertThat(innsynSak).isNotNull();
        assertThat(innsynSak.saksnummer().value()).isEqualTo(saksnummerMor.value());
    }
}
