package no.nav.foreldrepenger.autotest.fpsak.svangerskapspenger;

import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.SøknadSvangerskapspengerErketyper.lagSvangerskapspengerSøknad;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.ArbeidsforholdErketyper;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.TilretteleggingsErketyper;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaFødselOgTilrettelegging;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.BekreftSvangerskapspengervilkår;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.util.localdate.Virkedager;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.Orgnummer;

@Tag("fpsak")
@Tag("svangerskapspenger")
class Førstegangsbehandling extends FpsakTestBase {

    // Sjekk VerdikjedeSvangeskapsenger.java om det finnes en eksisterende test før du lager en ny her.

    @Test
    @DisplayName("Mor søker SVP med to arbeidsforhold - hel tilrettelegging")
    @Description("Mor søker SVP med to arbeidsforhold, fire uke før termin, hel tilrettelegging")
    void morSøkerSvp_HelTilrettelegging_FireUkerFørTermin_ToArbeidsforholdFraUlikeVirksomheter() {
        var familie = new Familie("504", fordel);
        var mor = familie.mor();
        var termindato = LocalDate.now().plusWeeks(4);
        var arbeidsforholdene = mor.arbeidsforholdene();
        var arbeidsforhold1 = arbeidsforholdene.get(0).arbeidsgiverIdentifikasjon();
        var arbeidsforhold2 = arbeidsforholdene.get(1).arbeidsgiverIdentifikasjon();
        var forsteTilrettelegging = TilretteleggingsErketyper.helTilrettelegging(
                LocalDate.now().minusWeeks(1),
                LocalDate.now().plusWeeks(2),
                ArbeidsforholdErketyper.virksomhet((Orgnummer) arbeidsforhold1));
        var andreTilrettelegging2 = TilretteleggingsErketyper.helTilrettelegging(
                LocalDate.now(),
                LocalDate.now().plusWeeks(3),
                ArbeidsforholdErketyper.virksomhet((Orgnummer) arbeidsforhold2));
        var søknad = lagSvangerskapspengerSøknad(BrukerRolle.MOR, termindato, List.of(forsteTilrettelegging, andreTilrettelegging2));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgivere = mor.arbeidsgivere();
        arbeidsgivere.sendDefaultInnteksmeldingerSVP(saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaFødselOgTilrettelegging.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(BekreftSvangerskapspengervilkår.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        var bekreftelse = beslutter
                .hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        var tilkjentYtelsePerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger()
                .getPerioder();
        assertThat(tilkjentYtelsePerioder)
                .as("Antall tilkjent ytelses peridoer")
                .hasSize(2);
        assertThat(tilkjentYtelsePerioder.get(0).getFom())
                .as("Tilkjent ytelses fom")
                .isEqualTo(LocalDate.now().minusWeeks(1));
        assertThat(tilkjentYtelsePerioder.get(0).getTom())
                .as("Tilkjent ytelses tom")
                .isEqualTo(LocalDate.now().minusDays(1));
        assertThat(tilkjentYtelsePerioder.get(1).getFom())
                .as("Tilkjent ytelses fom")
                .isEqualTo(LocalDate.now());
        assertThat(tilkjentYtelsePerioder.get(1).getTom())
                .as("Tilkjent ytelses tom")
                .isEqualTo(termindato.minusWeeks(3).minusDays(1));

        assertThat(tilkjentYtelsePerioder.get(0).getAndeler())
                .as("Antall andeler i tilkjent ytelsesperiode 1")
                .hasSize(1);
        assertThat(tilkjentYtelsePerioder.get(1).getAndeler())
                .as("Antall andeler i tilkjent ytelsesperiode 2")
                .hasSize(2);
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0))
                .as("Forventer at hele den utbetalte dagsatsen går til søker")
                .isTrue();
    }

    @Test
    @DisplayName("Mor søker SVP med tre arbeidsforhold - hel, halv og ingen tilrettelegging. Full refusjon")
    @Description("Mor søker SVP med tre arbeidsforhold - hel, halv og ingen tilrettelegging. Full refusjon")
    void mor_søker_svp_tre_arbeidsforhold_hel_halv_og_ingen_tilrettelegging() {
        var familie = new Familie("78", fordel);
        var mor = familie.mor();
        var termindato = LocalDate.now().plusMonths(3);
        var arbeidsforholdene = mor.arbeidsforholdene();
        var arbeidsforholdIdentifikator1 = arbeidsforholdene.get(0).arbeidsgiverIdentifikasjon();
        var arbeidsforholdIdentifikator2 = arbeidsforholdene.get(1).arbeidsgiverIdentifikasjon();
        var arbeidsforholdIdentifikator3 = arbeidsforholdene.get(2).arbeidsgiverIdentifikasjon();
        final var helTilrettelegging = TilretteleggingsErketyper.helTilrettelegging(
                termindato.minusMonths(3),
                termindato.minusMonths(3),
                ArbeidsforholdErketyper.virksomhet((Orgnummer) arbeidsforholdIdentifikator1));
        final var delvisTilrettelegging = TilretteleggingsErketyper.delvisTilrettelegging(
                termindato.minusMonths(2),
                termindato.minusMonths(2),
                ArbeidsforholdErketyper.virksomhet((Orgnummer) arbeidsforholdIdentifikator2),
                BigDecimal.valueOf(40));
        final var ingenTilrettelegging = TilretteleggingsErketyper.ingenTilrettelegging(
                termindato.minusMonths(2),
                termindato.minusMonths(2),
                ArbeidsforholdErketyper.virksomhet((Orgnummer) arbeidsforholdIdentifikator3));
        var søknad = lagSvangerskapspengerSøknad(BrukerRolle.MOR, termindato,
                List.of(helTilrettelegging, delvisTilrettelegging, ingenTilrettelegging));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgivere = mor.arbeidsgivere();
        var arbeidsgiver1 = arbeidsgivere.toList().get(0);
        var inntektsmelding1 = arbeidsgiver1.lagInntektsmeldingSVP()
                .medBeregnetInntekt(20_833);
        arbeidsgiver1.sendInntektsmeldinger(saksnummer, inntektsmelding1);

        var arbeidsgiver2 = arbeidsgivere.toList().get(1);
        var inntektsmelding2 = arbeidsgiver2.lagInntektsmeldingSVP()
                .medBeregnetInntekt(62_500)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(27_778));
        arbeidsgiver2.sendInntektsmeldinger(saksnummer, inntektsmelding2);

        var arbeidsgiver3 = arbeidsgivere.toList().get(2);
        var inntektsmelding3 = arbeidsgiver3.lagInntektsmeldingSVP()
                .medBeregnetInntekt(50_000)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(46_667));
        arbeidsgiver3.sendInntektsmeldinger(saksnummer, inntektsmelding3);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaFødselOgTilrettelegging.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(BekreftSvangerskapspengervilkår.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        var bekreftelse = beslutter
                .hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        // Verifisering av Beregning
        var bgPerioder = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPerioder()
                .stream()
                .sorted(Comparator.comparing(BeregningsgrunnlagPeriodeDto::getBeregningsgrunnlagPeriodeFom))
                .collect(Collectors.toList());
        assertThat(bgPerioder).hasSize(3);

        var førstePeriode = bgPerioder.get(0);
        assertThat(førstePeriode.getDagsats()).isZero();

        var andrePeriode = bgPerioder.get(1);
        assertThat(andrePeriode.getDagsats()).isPositive();

        var tredjePeriode = bgPerioder.get(2);
        assertThat(tredjePeriode.getDagsats()).isZero();

        // Verifisering av Tilkjent ytelse
        var tilkjentYtelsePerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger()
                .getPerioder();
        assertThat(tilkjentYtelsePerioder)
                .as("Antall tilkjent ytelses peridoer")
                .hasSize(1);
        assertThat(tilkjentYtelsePerioder.get(0).getFom())
                .as("Tilkjent ytelses fom")
                .isEqualTo(termindato.minusMonths(2));
        assertThat(tilkjentYtelsePerioder.get(0).getTom())
                .as("Tilkjent ytelses tom")
                .isEqualTo(termindato.minusWeeks(3).minusDays(1));
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(100))
                .as("Forventer at hele den utbetalte dagsatsen går til arbeidsgiver")
                .isTrue();
    }

    @Test
    @DisplayName("Mor søker SVP og FP - revurder SVP")
    @Description("Mor søker SVP og FP - revurder SVP, SVP seks uker før termin, FP tre uker før tidligere termin")
    void revurder_svp_pga_innvilget_fp() {
        // Innvilg SVP fra nå til Termin-3uker - tom fredag
        var familie = new Familie("502", fordel);
        var mor = familie.mor();
        var termindato = Virkedager.helgejustertTilMandag(LocalDate.now().plusWeeks(6));
        var arbeidsforholdene = mor.arbeidsforholdene();
        var arbeidsforhold1 = arbeidsforholdene.get(0).arbeidsgiverIdentifikasjon();
        var forsteTilrettelegging = TilretteleggingsErketyper.helTilrettelegging(
                LocalDate.now().minusWeeks(1),
                termindato.minusWeeks(3).minusDays(3),
                ArbeidsforholdErketyper.virksomhet((Orgnummer) arbeidsforhold1));
        var søknad = lagSvangerskapspengerSøknad(BrukerRolle.MOR, termindato, List.of(forsteTilrettelegging));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgivere = mor.arbeidsgivere();
        arbeidsgivere.sendDefaultInnteksmeldingerSVP(saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaFødselOgTilrettelegging.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(BekreftSvangerskapspengervilkår.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        var bekreftelse = beslutter
                .hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        // Innvilg FP fom Termin-4uker
        var søknadFP = lagSøknadForeldrepengerTermin(termindato.minusWeeks(1), BrukerRolle.MOR)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()));
        var saksnummerFP = mor.søk(søknadFP.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummerFP, termindato.minusWeeks(4));

        beslutter.hentFagsak(saksnummerFP);
        beslutter.ventTilAvsluttetBehandling();

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.OPPHØR_YTELSE_NYTT_BARN);

        // Revurder SVP - siste periode skal bli avslått i uttak og tilkjent dagsats = 0
        overstyrer.hentFagsak(saksnummer);
        overstyrer.ventPåOgVelgRevurderingBehandling();
        overstyrer.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaFødselOgTilrettelegging.class);
        overstyrer.bekreftAksjonspunktMedDefaultVerdier(BekreftSvangerskapspengervilkår.class);
        overstyrer.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        var bekreftelse2 = beslutter
                .hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse2);


        //assertThat(beslutter.valgtBehandling.hentBehandlingsresultat()).isEqualTo(BehandlingResultatType.OPPHØR);
        var tilkjentYtelsePerioder = beslutter.valgtBehandling.getBeregningResultatForeldrepenger()
                .getPerioder();
        assertThat(tilkjentYtelsePerioder.size())
                .as("Antall tilkjent ytelses peridoer")
                .isGreaterThan(1);
        // Litt datogamble
        assertThat(tilkjentYtelsePerioder.get(1).getFom())
                .as("Avslått tilkjent ytelse Fom")
                .isEqualTo(termindato.minusWeeks(4));
        assertThat(tilkjentYtelsePerioder.get(1).getDagsats())
                .as("Avslått tilkjent ytelse med dagsats 0")
                .isZero();


    }
}
