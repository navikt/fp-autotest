package no.nav.foreldrepenger.autotest.foreldrepenger.eksempler;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.SøknadBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.ytelse.ForeldrepengerYtelseBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.SoekersRelasjonErketyper;
import no.nav.foreldrepenger.vtp.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingBuilder;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Foreldrepenger;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.FordelingErketyper.*;

@Tag("eksempel")
public class OpprettTestscenario extends ForeldrepengerTestBase {

    @Test
    public void opprettTestScenarioFraJsonFilerIAutotest() throws Exception {
        TestscenarioDto testscenario = initialiserScenario("75");
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);

        Fordeling fordeling = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(8).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(13).minusDays(1)));

        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsdato),
                fordeling).build();
        SøknadBuilder søknad = new SøknadBuilder(
                foreldrepenger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR);

        long saksnummerMor = fordel.sendInnSøknad(
                søknad.build(),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);

        InntektsmeldingBuilder inntektsmeldinger =  lagInntektsmeldingBuilder(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpStartdatoMor,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandling();

    }

    @Test
    public void opprettTestScenarioFraJsonFilerIVTP() throws Exception {
        TestscenarioDto testscenario = opprettScenario("75");
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdatoMor = fødselsdato.minusWeeks(3);

        Fordeling fordeling = generiskFordeling(
                uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, fødselsdato.minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(8).minusDays(1)),
                uttaksperiode(STØNADSKONTOTYPE_FELLESPERIODE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(13).minusDays(1)));

        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.fødsel(1, fødselsdato),
                fordeling).build();
        SøknadBuilder søknad = new SøknadBuilder(
                foreldrepenger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR);

        long saksnummerMor = fordel.sendInnSøknad(
                søknad.build(),
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER,
                null);

        InntektsmeldingBuilder inntektsmeldinger =  lagInntektsmeldingBuilder(
                testscenario.getScenariodata().getInntektskomponentModell().getInntektsperioder().get(0).getBeløp(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                fpStartdatoMor,
                testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                testscenario.getPersonopplysninger().getSøkerIdent(),
                saksnummerMor);
        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandling();

    }

    @Test
    public void testTestscenarioRepositoryImpl() throws IOException {
        TestscenarioDto testscenario = initialiserScenario("75");

    }

}
