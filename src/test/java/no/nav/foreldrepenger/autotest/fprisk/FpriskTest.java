package no.nav.foreldrepenger.autotest.fprisk;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpriskTestBase;

@Tag("fprisk")
class FpriskTest extends FpriskTestBase {

    private static final String BEHANDLINGSTEMA_FORELDREPENGER = "ab0326";

    private static final String FPRISK_TOPIC_URL = "privat-foreldrepenger-fprisk-lokal";

    @Test
    @DisplayName("Sender Kafkamelding med risikovurderingsforespørsel, venter på at vurderingen blir gjort.")
    @Description("Sender inn forespørel om risikovurderinger til FPRISK for scenario 50 over Kafka (gjennom VTP). Venter på at saken er ferdig behandlet via polling over REST.")
    void sendRisikovurderingsforespørselOgVentPåResultat() {
        var testscenario = opprettTestscenario("50");

        var soekerAktoerId = testscenario.personopplysninger().søkerAktørIdent();
        var skjæringstidspunkt = LocalDate.now();
        var opplysningsperiodefraOgMed = LocalDate.now();
        var opplysningsperiodeTilOgMed = LocalDate.now().plusMonths(1);
        var annenPartAktørId = testscenario.personopplysninger().annenpartAktørIdent();
        var konsumentId = UUID.randomUUID().toString();
        var kontraktFpriskMelding = new RisikovurderingRequest(soekerAktoerId, skjæringstidspunkt,
                opplysningsperiodefraOgMed,
                opplysningsperiodeTilOgMed, BEHANDLINGSTEMA_FORELDREPENGER, annenPartAktørId, konsumentId);

        saksbehandler.sendMessageToKafkaTopic(FPRISK_TOPIC_URL,
                new RequestWrapper(UUID.randomUUID().toString(), kontraktFpriskMelding));

        saksbehandler.ventTilRisikoKlassefiseringsstatus(konsumentId, "IKKE_HOY");
    }
}
