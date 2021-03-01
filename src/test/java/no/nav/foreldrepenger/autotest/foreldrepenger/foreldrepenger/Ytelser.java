package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.ForeldrepengerBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Inntektskategori;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderBeregnetInntektsAvvikBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderFaktaOmBeregningBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarArbeidsforholdBekreftelse;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Execution(ExecutionMode.CONCURRENT)
@Tag("fpsak")
@Tag("foreldrepenger")
public class Ytelser extends ForeldrepengerTestBase {

    @Test
    @DisplayName("Mor søker fødsel og mottar sykepenger")
    @Description("Mor søker fødsel og mottar sykepenger - opptjening automatisk oppfylt")
    public void morSøkerFødselMottarSykepenger() {
        TestscenarioDto testscenario = opprettTestscenario("70");
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        String søkerIdent = testscenario.personopplysninger().søkerIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(
                søknad.build(),
                søkerAktørIdent,
                søkerIdent,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        saksbehandler.hentFagsak(saksnummer);
        AvklarArbeidsforholdBekreftelse avklarArbeidsforholdBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class);
        saksbehandler.bekreftAksjonspunkt(avklarArbeidsforholdBekreftelse);

        verifiser(saksbehandler.sjekkOmYtelseLiggerTilGrunnForOpptjening("SYKEPENGER"),
                "Forventer at det er registert en opptjeningsaktivitet med aktivitettype SYKEPENGER som " +
                        "er forut for permisjonen på skjæringstidspunktet!");

        VurderFaktaOmBeregningBekreftelse vurderFaktaOmBeregningBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderFaktaOmBeregningBekreftelse.class);
        vurderFaktaOmBeregningBekreftelse.leggTilAndelerYtelse(10000.0, Inntektskategori.ARBEIDSTAKER);
        saksbehandler.bekreftAksjonspunkt(vurderFaktaOmBeregningBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        var fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        fatterVedtakBekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(fatterVedtakBekreftelse);

        saksbehandler.ventTilFagsakLøpende();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.INNVILGET,
                "Forventer at behandlingen er innvilget og søker får støtte om foreldrepenger.");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0),
                "Forventer at hele summen utbetales til søker!");
    }


    @Test
    @DisplayName("Mor søker fødsel og mottar sykepenger og inntekter")
    @Description("Mor søker fødsel og mottar sykepenger og inntekter - opptjening automatisk godkjent")
    public void morSøkerFødselMottarSykepengerOgInntekter() {
        TestscenarioDto testscenario = opprettTestscenario("72");
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        String søkerIdent = testscenario.personopplysninger().søkerIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate startDatoForeldrepenger = fødselsdato.minusWeeks(3);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(
                søknad.build(),
                søkerAktørIdent,
                søkerIdent,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                startDatoForeldrepenger,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                        .arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);

        saksbehandler.hentFagsak(saksnummer);

        verifiser(saksbehandler.sjekkOmYtelseLiggerTilGrunnForOpptjening("SYKEPENGER"),
                "Forventer at det er registert en opptjeningsaktivitet med aktivitettype SYKEPENGER som " +
                        "er forut for permisjonen på skjæringstidspunktet!");

        VurderBeregnetInntektsAvvikBekreftelse vurderBeregnetInntektsAvvikBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderBeregnetInntektsAvvikBekreftelse.class);
        vurderBeregnetInntektsAvvikBekreftelse.leggTilInntekt((12 * 5000), 1);
        saksbehandler.bekreftAksjonspunkt(vurderBeregnetInntektsAvvikBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        saksbehandler.ventTilFagsakLøpende();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.INNVILGET);

    }
}
