package no.nav.foreldrepenger.autotest.aktoerer.innsender;

import java.util.List;

import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;

public interface Innsender {

    String sendInnSøknad(Søknad søknad, AktørId aktørId, Fødselsnummer fnr, String saksnummer);
    String sendInnSøknad(Endringssøknad søknad, AktørId aktørId, Fødselsnummer fnr, String saksnummer);
    String sendInnPapirsøknadForeldrepenger(AktørId aktørId, Fødselsnummer fnr);
    String sendInnPapirsøknadEEndringForeldrepenger(AktørId aktørId, Fødselsnummer fnr, String saksnummer);
    String sendInnPapirsøknadEngangsstønad(AktørId aktørId, Fødselsnummer fnr);
    void sendInnKlage(AktørId aktørId, Fødselsnummer fnr, String saksnummer);
    String sendInnInntektsmelding(InntektsmeldingBuilder inntektsmelding, AktørId aktørId, Fødselsnummer fnr, String saksnummer);
    String sendInnInntektsmelding(List<InntektsmeldingBuilder> inntektsmeldingBuilder, AktørId aktørId, Fødselsnummer fnr, String saksnummer);
}
