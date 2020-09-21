package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengeErketyper.lagSøknadForeldrepengerTermin;

import java.time.LocalDate;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.ForeldrepengerBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.vtp.kontrakter.DødshendelseDto;
import no.nav.foreldrepenger.vtp.kontrakter.FødselshendelseDto;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

public class TpsFeedTest extends ForeldrepengerTestBase {

    /**
     * Obs: Sekvensnummer må synkes med fp-abonnent ,Tabell:Input_feed,
     * column:Next-url Og med fp-sak tabell: mottat_hendese som lagrer hendelser med
     * type+SEKVENSENUMMER. eks FØDSEL2 . Dersom denne hendelsen finnes fra før
     * ignoreres den av fp-sak.
     * <p>
     * <p>
     * GJELDER ALLE TESTER
     */
    @Test
    @Disabled
    public void mottaFøselshendelse() {
        TestscenarioDto testscenario = opprettTestscenario("55");
        LocalDate termindato = LocalDate.now().plusWeeks(3);
        LocalDate startDatoForeldrepenger = termindato.minusWeeks(5);
        String aktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerTermin(termindato, aktørID, SøkersRolle.MOR);

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                startDatoForeldrepenger,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                        .getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummer);

        String fødselsnummerMor = testscenario.getPersonopplysninger().getSøkerIdent();
        String fødselsnummerBarn = "12345671234";
        FødselshendelseDto fødselshendelseDto = new FødselshendelseDto();
        fødselshendelseDto.setFnrMor(fødselsnummerMor);
        fødselshendelseDto.setFnrBarn(fødselsnummerBarn);
        fødselshendelseDto.setFødselsdato(termindato.minusWeeks(4));

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        fordel.opprettHendelsePåKafka(fødselshendelseDto);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAntallHistorikkinnslag(HistorikkInnslag.BEH_OPPDATERT_NYE_OPPL, 120, 2);
        verifiserLikhet(saksbehandler.harAntallHistorikkinnslag(HistorikkInnslag.BEH_OPPDATERT_NYE_OPPL), 2);
    }

    @Test
    @Disabled
    public void mottaDødhendelse() {
        TestscenarioDto testscenario = opprettTestscenario("55");
        LocalDate termindato = LocalDate.now().plusWeeks(3);
        LocalDate startDatoForeldrepenger = termindato.minusWeeks(5);
        String aktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerTermin(termindato, aktørID, SøkersRolle.MOR);

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                startDatoForeldrepenger,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0)
                        .getArbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummer);

        String fødselsnummerMor = testscenario.getPersonopplysninger().getSøkerIdent();
        DødshendelseDto dødshendelseDto = new DødshendelseDto();
        dødshendelseDto.setFnr(fødselsnummerMor);
        dødshendelseDto.setDoedsdato(LocalDate.now());

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        fordel.opprettHendelsePåKafka(dødshendelseDto);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAntallHistorikkinnslag(HistorikkInnslag.BEH_OPPDATERT_NYE_OPPL, 120, 2);
        verifiserLikhet(saksbehandler.harAntallHistorikkinnslag(HistorikkInnslag.BEH_OPPDATERT_NYE_OPPL), 2);
    }
}
