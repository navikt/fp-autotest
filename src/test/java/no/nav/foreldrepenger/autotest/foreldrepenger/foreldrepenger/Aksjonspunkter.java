package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.SøknadBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandling;
import static no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.SøknadErketyper.foreldrepengesøknadFødselErketype;

@Tag("util")
public class Aksjonspunkter  extends ForeldrepengerTestBase {

    @Test
    @DisplayName("Midlertidig test for å sjekke aksjonspunkter")
    public void stoppAksjonspunktFoedsel() throws Exception {

        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("160");

        LocalDate fødselsdato = testscenario.getPersonopplysninger().getFødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        String søkerAktørIdent = testscenario.getPersonopplysninger().getSøkerAktørIdent();

        SøknadBuilder søknad = foreldrepengesøknadFødselErketype(søkerAktørIdent, SøkersRolle.MOR, 1, fødselsdato);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();

        Arbeidsforhold arbeidsforhold_1 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0);
        Arbeidsforhold arbeidsforhold_2 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1);
        String arbeidsgiverOrgnr_1 = arbeidsforhold_1.getArbeidsgiverOrgnr();
        String arbeidsgiverOrgnr_2 = arbeidsforhold_2.getArbeidsgiverOrgnr();

        List<Integer> inntekter = sorterteInntektsbeløp(testscenario);


        InntektsmeldingBuilder inntektsmeldingBuilder_1 = lagInntektsmeldingBuilder(inntekter.get(0), fnr,
                fpStartdato, arbeidsgiverOrgnr_1);
        inntektsmeldingBuilder_1
                .medArbeidsforholdId(arbeidsforhold_1.getArbeidsforholdId())
                .medBeregnetInntekt(BigDecimal.valueOf(101230));
        InntektsmeldingBuilder inntektsmeldingBuilder_2 = lagInntektsmeldingBuilder(inntekter.get(1), fnr,
                fpStartdato, arbeidsgiverOrgnr_2);
        inntektsmeldingBuilder_2.medArbeidsforholdId(arbeidsforhold_2.getArbeidsforholdId());

        fordel.sendInnInntektsmeldinger(Arrays.asList(inntektsmeldingBuilder_1, inntektsmeldingBuilder_2), testscenario, saksnummer);

        debugLoggBehandling(saksbehandler.valgtBehandling);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        debugLoggBehandling(saksbehandler.valgtBehandling);



    }

}
