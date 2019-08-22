package no.nav.foreldrepenger.autotest.foreldrepenger.svangerskapspenger;

import static no.nav.foreldrepenger.fpmock2.dokumentgenerator.foreldrepengesoknad.erketyper.SoekerErketyper.morSoeker;
import static no.nav.foreldrepenger.fpmock2.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingBuilder.createDefaultSvangerskapspenger;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForesloVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaFødselOgTilrettelegging;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.BekreftSvangerskapspengervilkår;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.SvangerskapspengerTestBase;
import no.nav.foreldrepenger.fpmock2.dokumentgenerator.foreldrepengesoknad.erketyper.ArbeidsforholdErketyper;
import no.nav.foreldrepenger.fpmock2.dokumentgenerator.foreldrepengesoknad.erketyper.MedlemskapErketyper;
import no.nav.foreldrepenger.fpmock2.dokumentgenerator.foreldrepengesoknad.erketyper.SvangerskapspengerYtelseErketyper;
import no.nav.foreldrepenger.fpmock2.dokumentgenerator.foreldrepengesoknad.erketyper.TilretteleggingsErketyper;
import no.nav.foreldrepenger.fpmock2.dokumentgenerator.foreldrepengesoknad.soeknad.ForeldrepengesoknadBuilder;
import no.nav.foreldrepenger.fpmock2.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingBuilder;
import no.nav.foreldrepenger.fpmock2.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.fpmock2.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.foreldrepenger.fpmock2.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.fpmock2.testmodell.inntektytelse.inntektkomponent.Inntektsperiode;
import no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Svangerskapspenger;
import no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Tilrettelegging;
import no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Virksomhet;
import org.modelmapper.internal.bytebuddy.implementation.bind.annotation.IgnoreForBinding;

@Tag("fpsak") //TODO (OL): Gjør til fpsak når klar
@Tag("svangerskapspenger")
public class Førstegangsbehandling extends SvangerskapspengerTestBase {

    @Test
    @DisplayName("Mor søker SVP - ingen tilrettelegging")
    @Description("Mor søker SVP med ett arbeidsforhold fire uke før termin. ingen tilrettelegging")
    public void morSøkerSvp_IngenTilrettelegging_FireUkerFørTermin_EttArbeidsforhold() throws Exception {

        final TestscenarioDto testscenario = opprettScenario("50");
        final String morAktoerId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        final String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();

        final String orgNrMor = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        final int beløpMor = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();

        final Tilrettelegging tilrettelegging = TilretteleggingsErketyper.ingenTilrettelegging(
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                ArbeidsforholdErketyper.virksomhet(orgNrMor));

        final Svangerskapspenger svangerskapspenger = SvangerskapspengerYtelseErketyper.svangerskapspenger(
                LocalDate.now().plusWeeks(4),
                MedlemskapErketyper.medlemskapNorge(),
                Collections.singletonList(tilrettelegging));

        final ForeldrepengesoknadBuilder soknad = ForeldrepengesoknadBuilder.startBuilding()
                .withSvangerskapspengeYtelse(svangerskapspenger)
                .withSoeker(morSoeker(morAktoerId))
                .withMottattDato(LocalDate.now());

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);

        final long saksnummer = fordel.sendInnSøknad(soknad.build(), testscenario, DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER);

        final InntektsmeldingBuilder inntektsmelding = createDefaultSvangerskapspenger(beløpMor, fnrMor, orgNrMor);
        fordel.sendInnInntektsmelding(inntektsmelding, testscenario, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);

        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class)
                .setBegrunnelse("Test");
        saksbehandler.bekreftAksjonspunktBekreftelse(AvklarFaktaFødselOgTilrettelegging.class);

        saksbehandler.hentAksjonspunktbekreftelse(BekreftSvangerskapspengervilkår.class)
                .setBegrunnelse("Test");
        saksbehandler.bekreftAksjonspunktBekreftelse(BekreftSvangerskapspengervilkår.class);

        saksbehandler.hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class)
                .leggTilInntekt(beløpMor * 12,1L);
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class);

        saksbehandler.bekreftAksjonspunktBekreftelse(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);


    }

    @Test
    @Disabled
    @DisplayName("Mor søker SVP med to arbeidsforhold, fire uke før termin, hel tilrettelegging")
    @Description("Mor søker SVP med to arbeidsforhold, fire uke før termin, hel tilrettelegging")
    public void morSøkerSvp_HelTilrettelegging_FireUkerFørTermin_ToArbeidsforholdFraUlikeVirksomheter() throws Exception {

        final TestscenarioDto testscenario = opprettScenario("56");
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

        final Svangerskapspenger svangerskapspenger = SvangerskapspengerYtelseErketyper.svangerskapspenger(
                LocalDate.now().plusWeeks(4),
                MedlemskapErketyper.medlemskapNorge(),
                List.of(forsteTilrettelegging, andreTilrettelegging2));

        final ForeldrepengesoknadBuilder soknad = ForeldrepengesoknadBuilder.startBuilding()
                .withSvangerskapspengeYtelse(svangerskapspenger)
                .withSoeker(morSoeker(morAktoerId))
                .withMottattDato(LocalDate.now());

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);

        final long saksnummer = fordel.sendInnSøknad(soknad.build(), testscenario, DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER);

        // Inntektsmelding
        InntektsmeldingBuilder inntektsmelding1 = createDefaultSvangerskapspenger(inntektsperioder.get(0).getBeløp(), fnrMor, orgnr1);
        InntektsmeldingBuilder inntektsmelding2 = createDefaultSvangerskapspenger(inntektsperioder.get(1).getBeløp(), fnrMor, orgnr2);
        fordel.sendInnInntektsmeldinger(List.of(inntektsmelding1, inntektsmelding2), testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);

    }

    @Test
    @Disabled
    @DisplayName("mor_SVP_imFørSøknad")
    @Description("mor_SVP_imFørSøknad")
    public void mor_SVP_imFørSøknad() throws Exception {

        // TODO: Gjør ferdig, feiler på tilkjentytelse.

        final TestscenarioDto testscenario = opprettScenario("50");
        final String morAktoerId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        final String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();

        final int beløpMor = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        final String orgNrMor = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);

        final InntektsmeldingBuilder inntektsmelding = createDefaultSvangerskapspenger(beløpMor, fnrMor, orgNrMor);
        final long saksnummer = fordel.sendInnInntektsmelding(inntektsmelding, testscenario, null);

        final Tilrettelegging tilrettelegging = TilretteleggingsErketyper.helTilrettelegging(
                LocalDate.now(),
                LocalDate.now().plusWeeks(1),
                ArbeidsforholdErketyper.virksomhet(orgNrMor));

        final Svangerskapspenger svangerskapspenger = SvangerskapspengerYtelseErketyper.svangerskapspenger(
                LocalDate.now().plusWeeks(4),
                MedlemskapErketyper.medlemskapNorge(),
                Collections.singletonList(tilrettelegging));

        final ForeldrepengesoknadBuilder soknad = ForeldrepengesoknadBuilder.startBuilding()
                .withSvangerskapspengeYtelse(svangerskapspenger)
                .withSoeker(morSoeker(morAktoerId))
                .withMottattDato(LocalDate.now());

        fordel.sendInnSøknad(soknad.build(), testscenario, DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER, saksnummer);

        saksbehandler.hentFagsak(saksnummer);


    }

}
