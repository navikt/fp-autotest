package no.nav.foreldrepenger.autotest.aktoerer.innsender;

import java.util.List;

import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;

public interface Innsender {

    long sendInnSøknad(Søknad søknad, AktørId aktørId, Fødselsnummer fnr, Long saksnummer);
    long sendInnSøknad(Endringssøknad søknad, AktørId aktørId, Fødselsnummer fnr, Long saksnummer);
    long sendInnPapirsøknadForeldrepenger(AktørId aktørId, Fødselsnummer fnr);
    long sendInnPapirsøknadEEndringForeldrepenger(AktørId aktørId, Fødselsnummer fnr, Long saksnummer);
    long sendInnPapirsøknadEngangsstønad(AktørId aktørId, Fødselsnummer fnr);
    void sendInnKlage(AktørId aktørId, Fødselsnummer fnr, Long saksnummer);

    long sendInnInntektsmelding(InntektsmeldingBuilder inntektsmelding, AktørId aktørId, Fødselsnummer fnr, Long saksnummer);
    long sendInnInntektsmelding(List<InntektsmeldingBuilder> inntektsmeldingBuilder, AktørId aktørId, Fødselsnummer fnr, Long saksnummer);
}
