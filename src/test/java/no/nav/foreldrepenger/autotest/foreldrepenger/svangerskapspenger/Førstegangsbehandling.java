package no.nav.foreldrepenger.autotest.foreldrepenger.svangerskapspenger;

import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingSvangerskapspengerErketyper.lagSvangerskapspengerInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadSvangerskapspengerErketype.lagSvangerskapspengerSøknad;
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
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.SvangerskapspengerBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.erketyper.ArbeidsforholdErketyper;
import no.nav.foreldrepenger.autotest.erketyper.TilretteleggingsErketyper;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaFødselOgTilrettelegging;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.BekreftSvangerskapspengervilkår;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.inntektkomponent.Inntektsperiode;
import no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Tilrettelegging;

@Tag("fpsak")
@Tag("svangerskapspenger")
public class Førstegangsbehandling extends FpsakTestBase {

    // Sjekk VerdikjedeSvangeskapsenger.java om det finnes en eksisterende test før du lager en ny her.

    @Test
    @DisplayName("Mor søker SVP med to arbeidsforhold - hel tilrettelegging")
    @Description("Mor søker SVP med to arbeidsforhold, fire uke før termin, hel tilrettelegging")
    public void morSøkerSvp_HelTilrettelegging_FireUkerFørTermin_ToArbeidsforholdFraUlikeVirksomheter() {

        final TestscenarioDto testscenario = opprettTestscenario("504");
        final String morAktoerId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        final String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();
        final LocalDate termindato = LocalDate.now().plusWeeks(4);

        final List<Inntektsperiode> inntektsperioder = testscenario.getScenariodata().getInntektskomponentModell()
                .getInntektsperioder();
        final List<Arbeidsforhold> arbeidsforhold = testscenario.getScenariodata().getArbeidsforholdModell()
                .getArbeidsforhold();
        final String orgnr1 = arbeidsforhold.get(0).getArbeidsgiverOrgnr();
        final String orgnr2 = arbeidsforhold.get(1).getArbeidsgiverOrgnr();

        final Tilrettelegging forsteTilrettelegging = TilretteleggingsErketyper.helTilrettelegging(
                LocalDate.now().minusWeeks(1),
                LocalDate.now().plusWeeks(2),
                ArbeidsforholdErketyper.virksomhet(orgnr1));
        final Tilrettelegging andreTilrettelegging2 = TilretteleggingsErketyper.helTilrettelegging(
                LocalDate.now(),
                LocalDate.now().plusWeeks(3),
                ArbeidsforholdErketyper.virksomhet(orgnr2));

        SvangerskapspengerBuilder søknad = lagSvangerskapspengerSøknad(morAktoerId, SøkersRolle.MOR, termindato,
                List.of(forsteTilrettelegging, andreTilrettelegging2));

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        final long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER);

        // Inntektsmelding
        InntektsmeldingBuilder inntektsmelding1 = lagSvangerskapspengerInntektsmelding(fnrMor,
                inntektsperioder.get(0).getBeløp(), orgnr1);
        InntektsmeldingBuilder inntektsmelding2 = lagSvangerskapspengerInntektsmelding(fnrMor,
                inntektsperioder.get(1).getBeløp(), orgnr2);
        fordel.sendInnInntektsmeldinger(List.of(inntektsmelding1, inntektsmelding2), testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaFødselOgTilrettelegging.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(BekreftSvangerskapspengervilkår.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        BeregningsresultatPeriode[] tilkjentYtelsePerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger()
                .getPerioder();
        verifiser(tilkjentYtelsePerioder.length == 2, "Forventer 2 perioder i tilkjent ytelse!");
        verifiserLikhet(tilkjentYtelsePerioder[0].getFom(), LocalDate.now().minusWeeks(1));
        verifiserLikhet(tilkjentYtelsePerioder[0].getTom(), LocalDate.now().minusDays(1));
        verifiserLikhet(tilkjentYtelsePerioder[1].getFom(), LocalDate.now());
        verifiserLikhet(tilkjentYtelsePerioder[1].getTom(), termindato.minusWeeks(3).minusDays(1));
        verifiser(tilkjentYtelsePerioder[0].getAndeler().length == 1,
                "Forventer bare en andel i første periode fordi bare et AF skal tilrettelegges i perioden");
        verifiser(tilkjentYtelsePerioder[1].getAndeler().length == 2,
                "Forventer bare to andeler i andre periode fordi begge AFene skal tilrettelegges i perioden");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0),
                "Foventer at hele den utbetalte dagsatsen går til søker!");
    }

    @Test
    @DisplayName("Mor søker SVP med tre arbeidsforhold - hel, halv og ingen tilrettelegging. Full refusjon")
    @Description("Mor søker SVP med tre arbeidsforhold - hel, halv og ingen tilrettelegging. Full refusjon")
    public void mor_søker_svp_tre_arbeidsforhold_hel_halv_og_ingen_tilrettelegging() {

        final TestscenarioDto testscenario = opprettTestscenario("78");
        final String morAktoerId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        final String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();

        final List<Arbeidsforhold> arbeidsforhold = testscenario.getScenariodata().getArbeidsforholdModell()
                .getArbeidsforhold();
        final String orgnr1 = arbeidsforhold.get(0).getArbeidsgiverOrgnr();
        final String orgnr2 = arbeidsforhold.get(1).getArbeidsgiverOrgnr();
        final String orgnr3 = arbeidsforhold.get(2).getArbeidsgiverOrgnr();

        LocalDate termindato = LocalDate.now().plusMonths(3);

        final Tilrettelegging helTilrettelegging = TilretteleggingsErketyper.helTilrettelegging(
                termindato.minusMonths(3),
                termindato.minusMonths(3),
                ArbeidsforholdErketyper.virksomhet(orgnr1));
        final Tilrettelegging delvisTilrettelegging = TilretteleggingsErketyper.delvisTilrettelegging(
                termindato.minusMonths(2),
                termindato.minusMonths(2),
                ArbeidsforholdErketyper.virksomhet(orgnr2),
                BigDecimal.valueOf(40));
        final Tilrettelegging ingenTilrettelegging = TilretteleggingsErketyper.ingenTilrettelegging(
                termindato.minusMonths(2),
                termindato.minusMonths(2),
                ArbeidsforholdErketyper.virksomhet(orgnr3));

        SvangerskapspengerBuilder søknad = lagSvangerskapspengerSøknad(morAktoerId, SøkersRolle.MOR, termindato,
                List.of(helTilrettelegging, delvisTilrettelegging, ingenTilrettelegging));

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);

        final long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER);

        // Inntektsmelding
        InntektsmeldingBuilder inntektsmelding1 = lagSvangerskapspengerInntektsmelding(fnrMor, 20_833, orgnr1);
        InntektsmeldingBuilder inntektsmelding2 = lagSvangerskapspengerInntektsmelding(fnrMor, 62_500, orgnr2)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(27_778));
        InntektsmeldingBuilder inntektsmelding3 = lagSvangerskapspengerInntektsmelding(fnrMor, 50_000, orgnr3)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(46_667));
        fordel.sendInnInntektsmeldinger(List.of(inntektsmelding1, inntektsmelding2, inntektsmelding3), testscenario,
                saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaFødselOgTilrettelegging.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(BekreftSvangerskapspengervilkår.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        // Verifisering av Beregning
        List<BeregningsgrunnlagPeriodeDto> bgPerioder = saksbehandler.valgtBehandling.getBeregningsgrunnlag()
                .getBeregningsgrunnlagPerioder()
                .stream()
                .sorted(Comparator.comparing(BeregningsgrunnlagPeriodeDto::getBeregningsgrunnlagPeriodeFom))
                .collect(Collectors.toList());
        assertThat(bgPerioder).hasSize(3);

        BeregningsgrunnlagPeriodeDto førstePeriode = bgPerioder.get(0);
        assertThat(førstePeriode.getDagsats()).isEqualTo(0);

        BeregningsgrunnlagPeriodeDto andrePeriode = bgPerioder.get(1);
        assertThat(andrePeriode.getDagsats()).isGreaterThan(0);

        BeregningsgrunnlagPeriodeDto tredjePeriode = bgPerioder.get(2);
        assertThat(tredjePeriode.getDagsats()).isEqualTo(0);

        // Verifisering av Tilkjent ytelse
        BeregningsresultatPeriode[] tilkjentYtelsePerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger()
                .getPerioder();
        verifiser(tilkjentYtelsePerioder.length == 1, "Forventer 1 perioder i tilkjent ytelse!");
        verifiserLikhet(tilkjentYtelsePerioder[0].getFom(), termindato.minusMonths(2));
        verifiserLikhet(tilkjentYtelsePerioder[0].getTom(), termindato.minusWeeks(3).minusDays(1));
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(100),
                "Foventer at hele den utbetalte dagsatsen går til arbeidsgiver!");
    }

    @Test
    @Disabled
    @DisplayName("mor_SVP_imFørSøknad")
    @Description("mor_SVP_imFørSøknad")
    public void mor_SVP_imFørSøknad() {

        // TODO: Gjør ferdig, feiler på tilkjentytelse.
        // TODO (OL) Utvide med videre funksjonalitet

        final TestscenarioDto testscenario = opprettTestscenario("50");
        final String morAktoerId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        final String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();

        final int beløpMor = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0)
                .getBeløp();
        final String orgNrMor = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                .getArbeidsgiverOrgnr();

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);

        final InntektsmeldingBuilder inntektsmelding = lagSvangerskapspengerInntektsmelding(fnrMor, beløpMor, orgNrMor);
        final long saksnummer = fordel.sendInnInntektsmelding(inntektsmelding, testscenario, null);

        final Tilrettelegging tilrettelegging = TilretteleggingsErketyper.helTilrettelegging(
                LocalDate.now(),
                LocalDate.now().plusWeeks(1),
                ArbeidsforholdErketyper.virksomhet(orgNrMor));

        SvangerskapspengerBuilder søknad = lagSvangerskapspengerSøknad(
                morAktoerId, SøkersRolle.MOR,
                LocalDate.now().plusWeeks(4),
                Collections.singletonList(tilrettelegging));

        fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER, saksnummer);

        saksbehandler.hentFagsak(saksnummer);

    }
}
