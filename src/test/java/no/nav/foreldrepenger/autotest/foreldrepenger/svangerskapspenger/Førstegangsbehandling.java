package no.nav.foreldrepenger.autotest.foreldrepenger.svangerskapspenger;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.SvangerskapspengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.SvangerskapspengerBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.erketyper.ArbeidsforholdErketyper;
import no.nav.foreldrepenger.autotest.erketyper.TilretteleggingsErketyper;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForesloVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaFødselOgTilrettelegging;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.BekreftSvangerskapspengervilkår;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.inntektkomponent.Inntektsperiode;
import no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Tilrettelegging;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("fpsak")
@Tag("svangerskapspenger")
public class Førstegangsbehandling extends SvangerskapspengerTestBase {

    @Test
    @DisplayName("Mor søker SVP - ingen tilrettelegging")
    @Description("Mor søker SVP med ett arbeidsforhold fire uke før termin. ingen tilrettelegging")
    public void morSøkerSvp_IngenTilrettelegging_FireUkerFørTermin_EttArbeidsforhold() throws Exception {

        final TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");
        final String morAktoerId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        final String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();

        final String orgNrMor = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        final int beløpMor = 45_000;

        final Tilrettelegging tilrettelegging = TilretteleggingsErketyper.ingenTilrettelegging(
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                ArbeidsforholdErketyper.virksomhet(orgNrMor));

        final SvangerskapspengerBuilder søknad = lagSvangerskapspengerSøknad(
                morAktoerId, SøkersRolle.MOR,
                LocalDate.now().plusWeeks(4),
                Collections.singletonList(tilrettelegging));

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);

        final long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER);

        final InntektsmeldingBuilder inntektsmelding = lagSvangerskapspengerInntektsmelding(fnrMor, beløpMor, orgNrMor);
        fordel.sendInnInntektsmelding(inntektsmelding, testscenario, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);

        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class)
                .setBegrunnelse("Test");
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaFødselOgTilrettelegging.class);

        saksbehandler.hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class)
                .setBegrunnelse("Test");
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(BekreftSvangerskapspengervilkår.class);

        saksbehandler.hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class)
                .leggTilInntekt(beløpMor * 12,1L);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(VurderBeregnetInntektsAvvikBekreftelse.class);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);


    }

    @Test
    @DisplayName("Mor søker SVP med to arbeidsforhold - hel tilrettelegging")
    @Description("Mor søker SVP med to arbeidsforhold, fire uke før termin, hel tilrettelegging")
    public void morSøkerSvp_HelTilrettelegging_FireUkerFørTermin_ToArbeidsforholdFraUlikeVirksomheter() throws Exception {

        final TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("56");
        final String morAktoerId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        final String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();

        final List<Inntektsperiode> inntektsperioder = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder();
        final List<Arbeidsforhold> arbeidsforhold = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold();
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

        SvangerskapspengerBuilder søknad = lagSvangerskapspengerSøknad(morAktoerId, SøkersRolle.MOR, LocalDate.now().plusWeeks(4),
                List.of(forsteTilrettelegging, andreTilrettelegging2));

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        final long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER);

        // Inntektsmelding
        InntektsmeldingBuilder inntektsmelding1 = lagSvangerskapspengerInntektsmelding(fnrMor, inntektsperioder.get(0).getBeløp(), orgnr1);
        InntektsmeldingBuilder inntektsmelding2 = lagSvangerskapspengerInntektsmelding(fnrMor, inntektsperioder.get(1).getBeløp(), orgnr2);
        fordel.sendInnInntektsmeldinger(List.of(inntektsmelding1, inntektsmelding2), testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaFødselOgTilrettelegging.class);


        saksbehandler.hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class)
                .setBegrunnelse("Test");
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(BekreftSvangerskapspengervilkår.class);

    }

    @Test
    @DisplayName("Mor søker SVP med tre arbeidsforhold - hel, halv og ingen tilrettelegging. Full refusjon")
    @Description("Mor søker SVP med tre arbeidsforhold - hel, halv og ingen tilrettelegging. Full refusjon")
    public void mor_søker_svp_tre_arbeidsforhold_hel_halv_og_ingen_tilrettelegging() throws Exception {

        final TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("79");
        final String morAktoerId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        final String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();

        final List<Arbeidsforhold> arbeidsforhold = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold();
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

        final long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER);

        // Inntektsmelding
        InntektsmeldingBuilder inntektsmelding1 = lagSvangerskapspengerInntektsmelding(fnrMor, 20_833, orgnr1);
        InntektsmeldingBuilder inntektsmelding2 = lagSvangerskapspengerInntektsmelding(fnrMor, 62_500, orgnr2)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(27_778));
        InntektsmeldingBuilder inntektsmelding3 = lagSvangerskapspengerInntektsmelding(fnrMor,50_000, orgnr3)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(46_667));
        fordel.sendInnInntektsmeldinger(List.of(inntektsmelding1, inntektsmelding2, inntektsmelding3), testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class)
                .setBegrunnelse("Begrunnelse");
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaFødselOgTilrettelegging.class);


        saksbehandler.hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class)
                .setBegrunnelse("Test");
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(BekreftSvangerskapspengervilkår.class);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        List<BeregningsgrunnlagPeriodeDto> bgPerioder = saksbehandler.valgtBehandling.getBeregningsgrunnlag().getBeregningsgrunnlagPerioder()
                .stream()
                .sorted(Comparator.comparing(BeregningsgrunnlagPeriodeDto::getBeregningsgrunnlagPeriodeFom))
                .collect(Collectors.toList());
        assertThat(bgPerioder).hasSize(2);

        BeregningsgrunnlagPeriodeDto førstePeriode = bgPerioder.get(0);
        assertThat(førstePeriode.getDagsats()).isEqualTo(0);

        BeregningsgrunnlagPeriodeDto andrePeriode = bgPerioder.get(1);
        assertThat(andrePeriode.getDagsats()).isGreaterThan(0);
    }
    @Test
    @DisplayName("Mor søker SVP med tre arbeidsforhold - halv og halv tilrettelegging. Full refusjon")
    @Description("Mor søker SVP med tre arbeidsforhold - halv og halv tilrettelegging. Full refusjon")
    public void mor_søker_svp_tre_arbeidsforhold_to_halv() throws Exception {

        final TestscenarioDto testscenario = opprettTestscenario("79");
        final String morAktoerId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        final String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();

        final List<Arbeidsforhold> arbeidsforhold = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold();
        final String orgnr1 = arbeidsforhold.get(0).getArbeidsgiverOrgnr();
        final String orgnr2 = arbeidsforhold.get(1).getArbeidsgiverOrgnr();
        final String orgnr3 = arbeidsforhold.get(2).getArbeidsgiverOrgnr();

        LocalDate termindato = LocalDate.now().plusMonths(3);

        final Tilrettelegging delvisTilrettelegging = TilretteleggingsErketyper.delvisTilrettelegging(
                termindato.minusMonths(3),
                termindato.minusMonths(3),
                ArbeidsforholdErketyper.virksomhet(orgnr1),
                BigDecimal.valueOf(70));
        final Tilrettelegging delvisTilrettelegging2 = TilretteleggingsErketyper.delvisTilrettelegging(
                termindato.minusMonths(2),
                termindato.minusMonths(2),
                ArbeidsforholdErketyper.virksomhet(orgnr2),
                BigDecimal.valueOf(70));


        SvangerskapspengerBuilder søknad = lagSvangerskapspengerSøknad(morAktoerId, SøkersRolle.MOR, termindato,
                List.of(delvisTilrettelegging, delvisTilrettelegging2));

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);

        final long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER);

        // Inntektsmelding
        InntektsmeldingBuilder inntektsmelding1 = lagSvangerskapspengerInntektsmelding(fnrMor, 20_833, orgnr1)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(20_833));
        InntektsmeldingBuilder inntektsmelding2 = lagSvangerskapspengerInntektsmelding(fnrMor, 62_500, orgnr2)
                .medRefusjonsBelopPerMnd(BigDecimal.valueOf(27_778));
        fordel.sendInnInntektsmeldinger(List.of(inntektsmelding1, inntektsmelding2), testscenario, saksnummer);
    }

    @Test
    @Disabled
    @DisplayName("mor_SVP_imFørSøknad")
    @Description("mor_SVP_imFørSøknad")
    public void mor_SVP_imFørSøknad() throws Exception {

        // TODO: Gjør ferdig, feiler på tilkjentytelse.
        // TODO (OL) Utvide med videre funksjonalitet

        final TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");
        final String morAktoerId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        final String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();

        final int beløpMor = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        final String orgNrMor = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();

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

    @Test
    @Disabled
    @DisplayName("Papirsøknad for Svangerskapspenger")
    public void morSøkerSvangersskapspengerMedPapirsøknad() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnPapirsøknadSvangerskapspenger(testscenario);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);


    }

}
