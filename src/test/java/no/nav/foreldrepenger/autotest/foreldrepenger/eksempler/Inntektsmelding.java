package no.nav.foreldrepenger.autotest.foreldrepenger.eksempler;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;

@Tag("eksempel")
public class Inntektsmelding extends FpsakTestBase {

    private static final Logger logger = LoggerFactory.getLogger(Inntektsmelding.class);

    @Test
    public void oppretteInntektsmeldingerBasertPåTestscenarioUtenFagsak() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");
        List<InntektsmeldingBuilder> inntektsmeldinger = makeInntektsmeldingFromTestscenario(testscenario, LocalDate.now());
        InntektsmeldingBuilder inntektsmelding = inntektsmeldinger.get(0);
        inntektsmelding.medGradering(BigDecimal.TEN, LocalDate.now().plusWeeks(3), LocalDate.now().plusWeeks(5));

        logger.debug(inntektsmelding.createInntektesmeldingXML());

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnInntektsmelding(inntektsmelding, testscenario, null);

        logger.info("Sendt inn inntektsmelding på saksnummer: {}", saksnummer);
    }


    @Test
    public void opprettInntektsmeldingEgendefinert() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");
        String orgNr = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        String fnr = testscenario.getPersonopplysninger().getSøkerIdent();
        LocalDate fpStartdato = LocalDate.now().minusDays(3);
        InntektsmeldingBuilder inntektsmeldingBuilder = lagInntektsmeldingBuilder(60000, fnr, fpStartdato, orgNr);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnInntektsmelding(inntektsmeldingBuilder, testscenario, null);
        logger.debug(inntektsmeldingBuilder.createInntektesmeldingXML());
    }


}
