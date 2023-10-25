package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.autotest.aktoerer.innsender.InnsenderType.SEND_DOKUMENTER_UTEN_SELVBETJENING;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType.OPPHØR_YTELSE_NYTT_BARN;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType.REBEREGN_FERIEPENGER;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER;
import static no.nav.foreldrepenger.common.domain.BrukerRolle.FAR;
import static no.nav.foreldrepenger.common.domain.BrukerRolle.MOR;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.FEDREKVOTE;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.FELLESPERIODE;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.MØDREKVOTE;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadEndringMaler.lagEndringssøknad;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.generator.soknad.maler.UttakMaler.fordeling;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperiodeType.SAMTIDIGUTTAK;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.uttaksperiode;
import static no.nav.foreldrepenger.generator.soknad.util.VirkedagUtil.helgejustertTilMandag;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Comparator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsetteUttakEtterNesteSakDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderTilbakekrevingVedNegativSimulering;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseGenerator;
import no.nav.foreldrepenger.generator.soknad.maler.AnnenforelderMaler;
import no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler;
import no.nav.foreldrepenger.vtp.kontrakter.v2.FamilierelasjonModellDto;


/**
 * Sjekk at mor får ta ut rest dager etter de 6 ukene etter fødselen i en revurdering.
 * Vanligvis vil dager du har igjen med foreldrepenger falle bort når perioden for nytt barn starter.
 * Men siden barn er født med kort mellomrom (innen 46 uker), beholder en minsterett på 22/8 uker med foreldrepenger.
 * Dagene kan brukes før eller etter fødselen av barn 2, og frem til barn 1 er 3 år.
 * Scenario 1:  Mor har FP og får innvilget ny FP. Gammel FP opphører fordi hun har brukt mer enn minsteretten.
 *              Nytt barn opphører gammel sak fom 3 uker før termin og avslår periode
 * Scenario 2:  Mor har tatt ut mindre enn minsterett på barn 1 (f.eks. 4 uker igjen av minsteretten).
 *              Mor søker termin på barn 2 (40 uker etter barn 1).
 *              Mor søker endring på barn 1 om å ta ut resten av minsteretten etter de 6 første ukene for barn 2.
 *              Far tar ut 2 uker ifm fødsel og endringssøker om å ta ut resten av minsteretten sin på barn 1 (6 uker) etter mors peridoer for barn 2.
 */
@Tag("fpsak")
@Tag("foreldrepenger")
class ToTetteOgMinsterettTester extends FpsakTestBase {


    /**
     * Scenario 1:  Mor har FP og får innvilget ny FP. Gammel FP opphører fordi hun har brukt mer enn minsteretten.
     *              Nytt barn opphører gammel sak fom 3 uker før termin og avslår periode.
     *   Barn 1: x, Barn 2: y                                               saldo: FØR_FØDSEL|MØDRE|FEDRE|FELLES (3|15|15|16)
     *   Barn 1 (FS):   ---x----- ------        ----------                  saldo: 0|0|15|6    (28 brukt, 0 av 22 uker minstrett igjen)
     *   Barn 2 (FS):                               ---y------ --------     saldo: 0|0|15|6    (28 brukt)
     *   Barn 1 (R):    ---x----- ------        ----   y                    saldo: 0|0|15|12   Opphør. (24 brukt, 0 av 22 uker minstrett igjen)
     */
    @Test
    @DisplayName("Mor brukt opp minsterett. Nytt barn opphører gammel sak fom 3 uker før termin og avslår perioder etter dette")
    @Description("Mor har FP og får innvilget ny FP - revurder tidligste FP og avslå perioder inn i ny stønadsperiode. "
            + "Skal ikke havne i aksjonspunkt 5067 fordi mor har brukt opp minsteretten")
    void nytt_barn_opphører_gammel_sak_pga_minsterett_oppbrukt() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny().arbeidMedOpptjeningUnder6G().build())
                        .build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(40))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);

        // Barn 1: Førstgangssøknad
        var mor = familie.mor();
        var fødselsdatoBarn1 = familie.barn().fødselsdato();
        var fpStartdatoBarn1 = fødselsdatoBarn1.minusWeeks(3);
        var fordelingBarn1 = fordeling(
                uttaksperiode(StønadskontoType.FORELDREPENGER_FØR_FØDSEL, fpStartdatoBarn1, fødselsdatoBarn1.minusDays(1)),
                uttaksperiode(StønadskontoType.MØDREKVOTE, fødselsdatoBarn1, fødselsdatoBarn1.plusWeeks(15).minusDays(1)),
                uttaksperiode(StønadskontoType.FELLESPERIODE, fødselsdatoBarn1.plusWeeks(35), fødselsdatoBarn1.plusWeeks(51).minusDays(1))
        );
        var søknadBarn1 = lagSøknadForeldrepengerFødsel(fødselsdatoBarn1, BrukerRolle.MOR)
                .medFordeling(fordelingBarn1)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medMottattdato(fødselsdatoBarn1.minusWeeks(2));
        var saksnummerBarn1 = mor.søk(søknadBarn1.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummerBarn1, fpStartdatoBarn1);

        saksbehandler.hentFagsak(saksnummerBarn1);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        // Barn 2: Søker for barn 2 med termin 44 uker etter første barn
        var termindatoBarn2 = fødselsdatoBarn1.plusWeeks(44);
        var startdatoForeldrepengerMorBarn2 = termindatoBarn2.minusWeeks(3);
        var søknadBarn2 = lagSøknadForeldrepengerTermin(termindatoBarn2, BrukerRolle.MOR)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()));
        var saksnummerBarn2 = mor.søk(søknadBarn2.build());
        arbeidsgiver.sendInntektsmeldingerFP(saksnummerBarn2, startdatoForeldrepengerMorBarn2);

        saksbehandler.hentFagsak(saksnummerBarn2);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        // Barn 1: Revurdering skal avslå siste uttaksperiode med rett årsak
        saksbehandler.hentFagsak(saksnummerBarn1);
        saksbehandler.ventPåOgVelgRevurderingBehandling(OPPHØR_YTELSE_NYTT_BARN);
        if (forventerNegativSimuleringForBehandling(startdatoForeldrepengerMorBarn2)) {
            var vurderTilbakekrevingVedNegativSimulering = saksbehandler.hentAksjonspunktbekreftelse(VurderTilbakekrevingVedNegativSimulering.class)
                    .avventSamordningIngenTilbakekreving();
            saksbehandler.bekreftAksjonspunkt(vurderTilbakekrevingVedNegativSimulering);
            saksbehandler.ventTilAvsluttetBehandlingOgDetOpprettesTilbakekreving();
        } else {
            saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        }
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).isEqualTo(BehandlingResultatType.OPPHØR);
        var sisteUttaksperiode = sisteUttaksperiode();
        assertThat(sisteUttaksperiode.getFom())
                .as("Siste periode knekt ved startdato ny sak")
                .isEqualTo(helgejustertTilMandag(startdatoForeldrepengerMorBarn2));
        assertThat(sisteUttaksperiode.getPeriodeResultatÅrsak())
                .as("Siste periode avslått med årsak ny stønadsperiode")
                .isEqualTo(PeriodeResultatÅrsak.STØNADSPERIODE_NYTT_BARN);

    }




    /**
     * Scenario 2:  Mor har tatt ut mindre enn minsterett på barn 1 (f.eks. 4 uker igjen av minsteretten).
     *              Far tar ut ifm fødsel og 10 uker av fedrekvoten som strekker seg over uke 40 (termin for barn 2).
     *              Mor søker termin på barn 2 (40 uker etter barn 1) som førere til revurdering på far.
     *              Far sin sak revurderes og avslår uker som strekker seg forbi startdato barn 2 og som ikke er minstrett
     *              Mor søker endring på barn 1 om å ta 8 uker (bare 4 uker gjenstår av minstrett) etter de 6 første ukene for barn 2. Delvis innvilget.
     *   Barn 1: x, Barn 2: y                                                       saldo: FØR_FØDSEL|MØDRE|FEDRE|FELLES (3|15|15|16)
     *   MOR Barn 1 (FS):   ---x------ ---------                                    saldo: 0|3|15|13    INNVILGET (18 brukt, 4 av 22 uker igjen av minsterett)
     *   FAR Barn 1 (FS):      x--                  ----------                      saldo: 0|0|3|13     INNVILGET (2 uker brukt ifm fødsel, 10 brukt andre periode)
     *   MOR Barn 2 (FS):                               ---y------                  saldo: 0|9|15|13    INNVILGET (28 brukt)
     *   FAR Barn 1 (RE):      x--                  ------                          saldo: 0|0|9|13     Delvis innvilget/avslag: Forventer AP 5067. Vurder overlapp med sak 2. Avslå FK som strekker seg forbi startdato for mors nye sak.
     *   MOR Barn 1 (ENDR):    x                                  ---- ----         saldo: 0|0|15|12    Tar ut resten av minsrteretten på barn 1 (22 brukt, 0 av 22 uker igjen av minsterett)
     *   MOR Barn 1 (RE):   ---x------ ---------                  ----              saldo: 0|0|15|12    Delvis innvilget/avslag. Søker om mer enn minstretten. Forventer AP 5067. Vurder overlapp med sak 2.
     */
    @Test
    @DisplayName("Mor og far beholder minsteretten ved to tette og kan ta ut denne etter fødsel av siste barn")
    @Description("Scenario 2: Mor har tatt ut mindre enn minsterett på barn 1 (f.eks. 4 uker igjen av minsteretten)."
            + "Far tar ut ifm fødsel og 10 uker av fedrekvoten som strekker seg over uke 40 (termin for barn 2)."
            + "Mor søker termin på barn 2 (40 uker etter barn 1) som førere til revurdering på far."
            + "Far sin sak revurderes og avslår uker som strekker seg forbi startdato barn 2 og som ikke er minstrett."
            + "Mor søker endring på barn 1 om å ta 8 uker (bare 4 uker gjenstår av minstrett) etter de 6 første ukene for barn 2. Delvis innvilget")
    void mor_og_far_beholder_minsteretten_ved_to_tette_og_kan_ta_ut_denne_etter_fødel_av_siste_barn() {
        var familie = FamilieGenerator.ny()
                .forelder(mor()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidMedOpptjeningUnder6G()
                                .build())
                        .build())
                .forelder(far()
                        .inntektytelse(InntektYtelseGenerator.ny()
                                .arbeidMedOpptjeningUnder6G()
                                .build())
                        .build())
                .relasjonForeldre(FamilierelasjonModellDto.Relasjon.EKTE)
                .barn(LocalDate.now().minusWeeks(40))
                .build(SEND_DOKUMENTER_UTEN_SELVBETJENING);

        // MOR (barn 1): Førstegangssøknad for barn 1 (40 uker gammelt)
        var mor = familie.mor();
        var fødselsdatoBarn1 = familie.barn().fødselsdato();
        var fpStartdatoBarn1 = fødselsdatoBarn1.minusWeeks(3);
        var fordelingBarn1 = fordeling(
                uttaksperiode(StønadskontoType.FORELDREPENGER_FØR_FØDSEL, fpStartdatoBarn1, fødselsdatoBarn1.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdatoBarn1, fødselsdatoBarn1.plusWeeks(12).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdatoBarn1.plusWeeks(12), fødselsdatoBarn1.plusWeeks(15).minusDays(1))
        );
        var søknadBarn1 = lagSøknadForeldrepengerFødsel(fødselsdatoBarn1, MOR)
                .medFordeling(fordelingBarn1)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medMottattdato(fødselsdatoBarn1.minusWeeks(2))
                .build();
        var saksnummerMorBarn1 = mor.søk(søknadBarn1);

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummerMorBarn1, fpStartdatoBarn1);

        saksbehandler.hentFagsak(saksnummerMorBarn1);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var saldoMorFS = saksbehandler.valgtBehandling.getSaldoer().stonadskontoer();
        assertThat(saldoMorFS.containsKey(Saldoer.SaldoVisningStønadskontoType.MINSTERETT)).isFalse();
        assertThat(saldoMorFS.containsKey(Saldoer.SaldoVisningStønadskontoType.MINSTERETT_NESTE_STØNADSPERIODE)).isFalse();

        // FAR (barn 1): Førstegangssøknad for barn 1 (40 uker gammelt)
        var far = familie.far();
        var fordeling = fordeling(
                uttaksperiode(FEDREKVOTE, fødselsdatoBarn1, fødselsdatoBarn1.plusWeeks(2).minusDays(1), SAMTIDIGUTTAK),
                uttaksperiode(FEDREKVOTE, fødselsdatoBarn1.plusWeeks(36), fødselsdatoBarn1.plusWeeks(46).minusDays(1))
        );
        var søknadFar = SøknadForeldrepengerMaler.lagSøknadForeldrepengerTerminFødsel(fødselsdatoBarn1, FAR)
                .medFordeling(fordeling)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.mor()))
                .medMottattdato(fødselsdatoBarn1.minusWeeks(1));
        var saksnummerFarBarn1 = far.søk(søknadFar.build());

        far.arbeidsgivere().sendDefaultInntektsmeldingerFP(saksnummerFarBarn1, fødselsdatoBarn1);

        saksbehandler.hentFagsak(saksnummerFarBarn1);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var saldoFarFs = saksbehandler.valgtBehandling.getSaldoer().stonadskontoer();
        assertThat(saldoFarFs.containsKey(Saldoer.SaldoVisningStønadskontoType.MINSTERETT)).isFalse();
        assertThat(saldoFarFs.containsKey(Saldoer.SaldoVisningStønadskontoType.MINSTERETT_NESTE_STØNADSPERIODE)).isFalse();

        // MOR (barn 2): Førstegangssøknad på Barn 2 (termin 40 uker etter barn 1)
        var termindatoBarn2 = fødselsdatoBarn1.plusWeeks(40);
        var fordelingMorBarn2 = fordeling(
                uttaksperiode(StønadskontoType.FORELDREPENGER_FØR_FØDSEL, termindatoBarn2.minusWeeks(3), termindatoBarn2.minusDays(1)),
                uttaksperiode(MØDREKVOTE, termindatoBarn2, termindatoBarn2.plusWeeks(6).minusDays(1))
        );
        var søknadMorBarn2 = lagSøknadForeldrepengerTermin(termindatoBarn2, MOR)
                .medFordeling(fordelingMorBarn2)
                .medAnnenForelder(AnnenforelderMaler.norskMedRettighetNorge(familie.far()))
                .medMottattdato(termindatoBarn2.minusWeeks(3));
        var saksnummerMorBarn2 = mor.søk(søknadMorBarn2.build());
        arbeidsgiver.sendInntektsmeldingerFP(saksnummerMorBarn2, termindatoBarn2.minusWeeks(3));

        saksbehandler.hentFagsak(saksnummerMorBarn2);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        // Verifiser at fagsak på barn 1 IKKE blir berørt og det IKKE opprettes en revurdering
        saksbehandler.hentFagsak(saksnummerMorBarn1);
        var behadlingerMorFagsak1 = saksbehandler.hentAlleBehandlingerForFagsak(saksnummerMorBarn1).stream()
                .filter(behandling -> BehandlingType.REVURDERING.equals(behandling.type))
                .toList();
        assertThat(behadlingerMorFagsak1).hasSize(1);
        assertThat(behadlingerMorFagsak1.get(0).getBehandlingÅrsaker())
                .extracting(BehandlingÅrsak::behandlingArsakType)
                .containsExactly(REBEREGN_FERIEPENGER);

        // FAR (barn 1): Revurdering pga overlapp med nytt barn
        saksbehandler.hentFagsak(saksnummerFarBarn1);
        saksbehandler.ventPåOgVelgRevurderingBehandling(OPPHØR_YTELSE_NYTT_BARN);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(FastsetteUttakEtterNesteSakDto.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);
        beslutter.hentFagsak(saksnummerFarBarn1);
        beslutter.ventPåOgVelgRevurderingBehandling();
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(
                beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                        .godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling()));
        beslutter.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var saldoFarRevurdering = saksbehandler.valgtBehandling.getSaldoer().stonadskontoer();
        assertThat(saldoFarRevurdering.containsKey(Saldoer.SaldoVisningStønadskontoType.MINSTERETT)).isFalse();
        assertThat(saldoFarRevurdering.containsKey(Saldoer.SaldoVisningStønadskontoType.MINSTERETT_NESTE_STØNADSPERIODE)).isTrue();
        assertThat(saldoFarRevurdering.get(Saldoer.SaldoVisningStønadskontoType.MINSTERETT_NESTE_STØNADSPERIODE).saldo())
                .isZero();
        assertThat(saksbehandler.valgtBehandling.getBehandlingÅrsaker())
                .map(BehandlingÅrsak::behandlingArsakType)
                .containsExactly(BehandlingÅrsakType.OPPHØR_YTELSE_NYTT_BARN); // TODO: (TFP-5356) Skal ikke føre til opphør

        // Forventer at siste periode er avslått med avlsagstype STØNADSPERIODE_NYTT_BARN
        var sisteUttaksperiodeFarBarn1 = sisteUttaksperiode();
        assertThat(sisteUttaksperiodeFarBarn1.getFom())
                .as("Siste periode knekt ved startdato ny sak")
                .isEqualTo(helgejustertTilMandag(fødselsdatoBarn1.plusWeeks(36).plusWeeks(6)));
        assertThat(sisteUttaksperiodeFarBarn1.getPeriodeResultatÅrsak().isAvslåttÅrsak()).isTrue();
        assertThat(sisteUttaksperiodeFarBarn1.getPeriodeResultatÅrsak())
                .as("Siste periode avslått med årsak ny stønadsperiode")
                .isEqualTo(PeriodeResultatÅrsak.STØNADSPERIODE_NYTT_BARN);



        // MOR (barn 1): ENDRINGSSØKNAD FOR Å TA UT RESTEN AV MINSTERETTEN.
        // Har 4 uker igjen av minstretten, men søker om 8 (forventer 4 uker innvilget og de siste 4 ukene avslått)
        var fordelingEndringBarn1 = fordeling(
                uttaksperiode(MØDREKVOTE, termindatoBarn2.plusWeeks(6), termindatoBarn2.plusWeeks(9).minusDays(1)),
                uttaksperiode(FELLESPERIODE, termindatoBarn2.plusWeeks(9), termindatoBarn2.plusWeeks(14).minusDays(1))
        );
        var endringssøknadMorBarn1 = lagEndringssøknad(søknadBarn1, saksnummerMorBarn1, fordelingEndringBarn1);
        var saksnummerMorBarn1Endring = mor.søk(endringssøknadMorBarn1.build());

        saksbehandler.hentFagsak(saksnummerMorBarn1Endring);
        saksbehandler.ventPåOgVelgRevurderingBehandling(RE_ENDRING_FRA_BRUKER);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(FastsetteUttakEtterNesteSakDto.class);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);
        beslutter.hentFagsak(saksnummerMorBarn1Endring);
        beslutter.ventPåOgVelgRevurderingBehandling();
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(
                beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                        .godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling()));
        beslutter.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        // Forventer at siste periode er avslått med avlsagstype STØNADSPERIODE_NYTT_BARN
        // TODO: (TFP-5356) Skal ikke føre til opphør eller skal det det i denne sammenhengen?
        var sisteUttaksperiodeMorBarn1 = sisteUttaksperiode();
        assertThat(sisteUttaksperiodeMorBarn1.getFom())
                .as("Siste periode knekt ved startdato ny sak")
                .isEqualTo(helgejustertTilMandag(termindatoBarn2.plusWeeks(10)));
        assertThat(sisteUttaksperiodeMorBarn1.getPeriodeResultatÅrsak().isAvslåttÅrsak()).isTrue();
        assertThat(sisteUttaksperiodeMorBarn1.getPeriodeResultatÅrsak())
                .as("Siste periode avslått med årsak ny stønadsperiode")
                .isEqualTo(PeriodeResultatÅrsak.STØNADSPERIODE_NYTT_BARN);

        // Verifiser at fagsak på barn 12 IKKE blir berørt og det IKKE opprettes en revurdering
        saksbehandler.hentFagsak(saksnummerMorBarn2);
        assertThat(saksbehandler.harRevurderingBehandling()).isFalse();
    }

    private UttakResultatPeriode sisteUttaksperiode() {
        return saksbehandler.valgtBehandling.getUttakResultatPerioder()
                .getPerioderSøker()
                .stream()
                .max(Comparator.comparing(UttakResultatPeriode::getFom))
                .orElseThrow();
    }

}
