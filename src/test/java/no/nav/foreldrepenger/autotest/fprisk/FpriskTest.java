package no.nav.foreldrepenger.autotest.fprisk;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.FpriskTestBase;
import no.nav.foreldrepenger.fpmock2.kontrakter.TestscenarioDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.LocalDate;
import java.util.UUID;

@Execution(ExecutionMode.CONCURRENT)
@Tag("fprisk")
public class FpriskTest extends FpriskTestBase {
    Logger LOG = LoggerFactory.getLogger(FpriskTest.class);
    private static final String BEHANDLINGSTEMA_FORELDREPENGER = "ab0326";

    private static final String FPRISK_TOPIC_URL = "privat-foreldrepenger-fprisk";


    @Test
    @DisplayName("Sender Kafkamelding med risikovurderingsforespørsel, venter på at vurderingen blir gjort.")
    @Description("Sender inn forespørel om risikovurderinger til FPRISK for scenario 50 over Kafka (gjennom VTP). Venter på at saken er ferdig behandlet via polling over REST.")
    public void sendRisikovurderingsforespørselOgVentPåResultat() throws Exception {
        TestscenarioDto testscenario = opprettScenario("50");

        String soekerAktoerId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate skjæringstidspunkt = LocalDate.now();
        LocalDate opplysningsperiodefraOgMed = LocalDate.now();
        LocalDate opplysningsperiodeTilOgMed = LocalDate.now().plusMonths(1);
        String behandlingstema = BEHANDLINGSTEMA_FORELDREPENGER;
        String annenPartAktørId = testscenario.getPersonopplysninger().getAnnenPartAktørIdent();
        String konsumentId = UUID.randomUUID().toString();
        RisikovurderingRequest kontraktFpriskMelding = new RisikovurderingRequest(soekerAktoerId, skjæringstidspunkt, opplysningsperiodefraOgMed,
                                                            opplysningsperiodeTilOgMed, behandlingstema, annenPartAktørId, konsumentId);

        saksbehandler.sendMessageToKafkaTopic(FPRISK_TOPIC_URL, new RequestWrapper(UUID.randomUUID().toString(), kontraktFpriskMelding));

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.ventTilRisikoKlassefiseringsstatus(konsumentId,"IKKE_HOY");

    }



}
