package no.nav.foreldrepenger.autotest.fprisk;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.FpriskTestBase;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;


@Tag("fprisk")
public class FpriskTest extends FpriskTestBase {
    Logger LOG = LoggerFactory.getLogger(FpriskTest.class);
    private static final String BEHANDLINGSTEMA_FORELDREPENGER = "ab0326";

    private static final String FPRISK_TOPIC_URL = "privat-foreldrepenger-fprisk-lokal";

    @Test
    @DisplayName("Sender Kafkamelding med risikovurderingsforespørsel, venter på at vurderingen blir gjort.")
    @Description("Sender inn forespørel om risikovurderinger til FPRISK for scenario 50 over Kafka (gjennom VTP). Venter på at saken er ferdig behandlet via polling over REST.")
    public void sendRisikovurderingsforespørselOgVentPåResultat() {
        TestscenarioDto testscenario = opprettTestscenario("50");

        String soekerAktoerId = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate skjæringstidspunkt = LocalDate.now();
        LocalDate opplysningsperiodefraOgMed = LocalDate.now();
        LocalDate opplysningsperiodeTilOgMed = LocalDate.now().plusMonths(1);
        String behandlingstema = BEHANDLINGSTEMA_FORELDREPENGER;
        String annenPartAktørId = testscenario.personopplysninger().annenpartAktørIdent();
        String konsumentId = UUID.randomUUID().toString();
        RisikovurderingRequest kontraktFpriskMelding = new RisikovurderingRequest(soekerAktoerId, skjæringstidspunkt,
                opplysningsperiodefraOgMed,
                opplysningsperiodeTilOgMed, behandlingstema, annenPartAktørId, konsumentId);

        saksbehandler.sendMessageToKafkaTopic(FPRISK_TOPIC_URL,
                new RequestWrapper(UUID.randomUUID().toString(), kontraktFpriskMelding));

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.ventTilRisikoKlassefiseringsstatus(konsumentId, "IKKE_HOY");

    }

}
