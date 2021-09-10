package no.nav.foreldrepenger.autotest.fpsak.svangerskapspenger;

import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.SøknadSvangerskapspengerErketype.lagSvangerskapspengerSøknad;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingSvangerskapspengerErketyper.lagSvangerskapspengerInntektsmelding;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.ArbeidsforholdErketyper;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.TilretteleggingsErketyper;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaFødselOgTilrettelegging;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.BekreftSvangerskapspengervilkår;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Tag("fpsak")
@Tag("svangerskapspenger")
class Førstegangsbehandling extends FpsakTestBase {

    // Sjekk VerdikjedeSvangeskapsenger.java om det finnes en eksisterende test før du lager en ny her.

    @Test
    @DisplayName("Mor søker SVP med to arbeidsforhold - hel tilrettelegging")
    @Description("Mor søker SVP med to arbeidsforhold, fire uke før termin, hel tilrettelegging")
    void morSøkerSvp_HelTilrettelegging_FireUkerFørTermin_ToArbeidsforholdFraUlikeVirksomheter() {

        final var testscenario = opprettTestscenario("504");
        final var morAktoerId = testscenario.personopplysninger().søkerAktørIdent();
        final var fnrMor = testscenario.personopplysninger().søkerIdent();
        final var termindato = LocalDate.now().plusWeeks(4);

        final var inntektsperioder = testscenario.scenariodataDto().inntektskomponentModell()
                .inntektsperioder();
        final var arbeidsforhold = testscenario.scenariodataDto().arbeidsforholdModell()
                .arbeidsforhold();
        final var orgnr1 = arbeidsforhold.get(0).arbeidsgiverOrgnr();
        final var orgnr2 = arbeidsforhold.get(1).arbeidsgiverOrgnr();

        final var forsteTilrettelegging = TilretteleggingsErketyper.helTilrettelegging(
                LocalDate.now().minusWeeks(1),
                LocalDate.now().plusWeeks(2),
                ArbeidsforholdErketyper.virksomhet(orgnr1));
        final var andreTilrettelegging2 = TilretteleggingsErketyper.helTilrettelegging(
                LocalDate.now(),
                LocalDate.now().plusWeeks(3),
                ArbeidsforholdErketyper.virksomhet(orgnr2));

        var søknad = lagSvangerskapspengerSøknad(morAktoerId, SøkersRolle.MOR, termindato,
                List.of(forsteTilrettelegging, andreTilrettelegging2));

        final var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER);

        // Inntektsmelding
        var inntektsmelding1 = lagSvangerskapspengerInntektsmelding(fnrMor,
                inntektsperioder.get(0).beløp(), orgnr1);
        var inntektsmelding2 = lagSvangerskapspengerInntektsmelding(fnrMor,
                inntektsperioder.get(1).beløp(), orgnr2);
        fordel.sendInnInntektsmeldinger(List.of(inntektsmelding1, inntektsmelding2), testscenario, saksnummer);

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

        final var testscenario = opprettTestscenario("78");
        final var morAktoerId = testscenario.personopplysninger().søkerAktørIdent();
        final var fnrMor = testscenario.personopplysninger().søkerIdent();

        final var arbeidsforhold = testscenario.scenariodataDto().arbeidsforholdModell()
                .arbeidsforhold();
        final var orgnr1 = arbeidsforhold.get(0).arbeidsgiverOrgnr();
        final var orgnr2 = arbeidsforhold.get(1).arbeidsgiverOrgnr();
        final var orgnr3 = arbeidsforhold.get(2).arbeidsgiverOrgnr();

        var termindato = LocalDate.now().plusMonths(3);

        final var helTilrettelegging = TilretteleggingsErketyper.helTilrettelegging(
                termindato.minusMonths(3),
                termindato.minusMonths(3),
                ArbeidsforholdErketyper.virksomhet(orgnr1));
        final var delvisTilrettelegging = TilretteleggingsErketyper.delvisTilrettelegging(
                termindato.minusMonths(2),
                termindato.minusMonths(2),
                ArbeidsforholdErketyper.virksomhet(orgnr2),
                BigDecimal.valueOf(40));
        final var ingenTilrettelegging = TilretteleggingsErketyper.ingenTilrettelegging(
                termindato.minusMonths(2),
                termindato.minusMonths(2),
                ArbeidsforholdErketyper.virksomhet(orgnr3));

        var søknad = lagSvangerskapspengerSøknad(morAktoerId, SøkersRolle.MOR, termindato,
                List.of(helTilrettelegging, delvisTilrettelegging, ingenTilrettelegging));


        final var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER);

        // Inntektsmelding
        var inntektsmelding1 = lagSvangerskapspengerInntektsmelding(fnrMor, 20_833, orgnr1);
        var inntektsmelding2 = lagSvangerskapspengerInntektsmelding(fnrMor, 62_500, orgnr2)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(27_778));
        var inntektsmelding3 = lagSvangerskapspengerInntektsmelding(fnrMor, 50_000, orgnr3)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(46_667));
        fordel.sendInnInntektsmeldinger(List.of(inntektsmelding1, inntektsmelding2, inntektsmelding3), testscenario,
                saksnummer);

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
    @Disabled
    @DisplayName("mor_SVP_imFørSøknad")
    @Description("mor_SVP_imFørSøknad")
    void mor_SVP_imFørSøknad() {

        // TODO: Gjør ferdig, feiler på tilkjentytelse.
        // TODO (OL) Utvide med videre funksjonalitet

        final var testscenario = opprettTestscenario("50");
        final var morAktoerId = testscenario.personopplysninger().søkerAktørIdent();
        final var fnrMor = testscenario.personopplysninger().søkerIdent();

        final var beløpMor = testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0)
                .beløp();
        final var orgNrMor = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();


        final var inntektsmelding = lagSvangerskapspengerInntektsmelding(fnrMor, beløpMor, orgNrMor);
        final var saksnummer = fordel.sendInnInntektsmelding(inntektsmelding, testscenario, null);

        final var tilrettelegging = TilretteleggingsErketyper.helTilrettelegging(
                LocalDate.now(),
                LocalDate.now().plusWeeks(1),
                ArbeidsforholdErketyper.virksomhet(orgNrMor));

        var søknad = lagSvangerskapspengerSøknad(
                morAktoerId, SøkersRolle.MOR,
                LocalDate.now().plusWeeks(4),
                Collections.singletonList(tilrettelegging));

        fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER, saksnummer);

        saksbehandler.hentFagsak(saksnummer);

    }
}
