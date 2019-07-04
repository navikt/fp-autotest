package no.nav.foreldrepenger.autotest.foreldrepenger.svangerskapspenger;

import static no.nav.foreldrepenger.fpmock2.dokumentgenerator.foreldrepengesoknad.erketyper.SoekerErketyper.morSoeker;
import static no.nav.foreldrepenger.fpmock2.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingBuilder.createDefaultSvangerskapspenger;

import java.time.LocalDate;
import java.util.Arrays;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaFødselOgTilrettelegging;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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
import no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Virksomhet;

@Tag("develop") //TODO (OL): Gjør til fpsak når klar
@Tag("svangerskapspenger")
public class Førstegangsbehandling extends SvangerskapspengerTestBase {

    @Test
    public void morSøkerSvangerskapspengerHelTilretteleggingFireUkerFørTermin() throws Exception {
        TestscenarioDto testscenario = opprettScenario("50");

        String morAktoerId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();
        int beløpMor = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        String orgNrMor = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        Virksomhet morVirksomhet = ArbeidsforholdErketyper.virksomhet(orgNrMor);
        ForeldrepengesoknadBuilder soknad = ForeldrepengesoknadBuilder.startBuilding()
                .withSvangerskapspengeYtelse(
                        SvangerskapspengerYtelseErketyper.svangerskapspenger(
                                LocalDate.now().plusWeeks(4),
                                MedlemskapErketyper.medlemskapNorge(),
                                Arrays.asList(TilretteleggingsErketyper.helTilrettelegging(
                                        LocalDate.now(),LocalDate.now().plusWeeks(1),morVirksomhet))
                        )
                )
                .withSoeker(morSoeker(morAktoerId))
                .withMottattDato(LocalDate.now());
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(soknad.build(), testscenario, DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER);

        // Inntektsmelding
        InntektsmeldingBuilder inntektsmeldingerSøker = createDefaultSvangerskapspenger(beløpMor, fnrMor, orgNrMor);
        fordel.sendInnInntektsmelding(inntektsmeldingerSøker, testscenario, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        verifiser(saksbehandler.harAksjonspunkt(AksjonspunktKoder.AVKLAR_FØDSEL_OG_TILRETTELEGGING),
                "Mangler aksjonspunkt om fødsel og tilrettelegging");

        AvklarFaktaFødselOgTilrettelegging bekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaFødselOgTilrettelegging.class);
        bekreftelse.setBegrunnelse("omg yes");
        saksbehandler.bekreftAksjonspunktBekreftelse(bekreftelse);

    }
    @Test
    @Disabled
    public void mor_SVP_imFørSøknad() throws Exception {
        //Feiler på tilkjentytelse.
        TestscenarioDto testscenario = opprettScenario("50");

        String morAktoerId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        String fnrMor = testscenario.getPersonopplysninger().getSøkerIdent();
        int beløpMor = testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp();
        String orgNrMor = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        Virksomhet morVirksomhet = ArbeidsforholdErketyper.virksomhet(orgNrMor);

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        InntektsmeldingBuilder inntektsmeldingerSøker = createDefaultSvangerskapspenger(beløpMor, fnrMor, orgNrMor);
        Long saksnummer = fordel.sendInnInntektsmelding(inntektsmeldingerSøker, testscenario, null);


        ForeldrepengesoknadBuilder soknad = ForeldrepengesoknadBuilder.startBuilding()
                .withSvangerskapspengeYtelse(
                        SvangerskapspengerYtelseErketyper.svangerskapspenger(
                                LocalDate.now().plusWeeks(4),
                                MedlemskapErketyper.medlemskapNorge(),
                                Arrays.asList(TilretteleggingsErketyper.helTilrettelegging(LocalDate.now(),LocalDate.now().plusWeeks(1),morVirksomhet))
                        )
                )
                .withSoeker(morSoeker(morAktoerId))
                .withMottattDato(LocalDate.now());
        fordel.sendInnSøknad(soknad.build(), testscenario, DokumenttypeId.SØKNAD_SVANGERSKAPSPENGER, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
    }


}
