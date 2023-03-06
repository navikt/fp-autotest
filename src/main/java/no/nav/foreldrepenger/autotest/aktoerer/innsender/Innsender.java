package no.nav.foreldrepenger.autotest.aktoerer.innsender;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.vtp.kontrakter.PersonhendelseDto;

public interface Innsender {
    Logger LOG = LoggerFactory.getLogger(Innsender.class);

    Saksnummer sendInnSøknad(Søknad søknad, AktørId aktørId, Fødselsnummer fnr, AktørId aktørIdAnnenpart, Saksnummer saksnummer);
    Saksnummer sendInnSøknad(Endringssøknad søknad, AktørId aktørId, Fødselsnummer fnr, AktørId aktørIdAnnenpart, Saksnummer saksnummer);
    Saksnummer sendInnPapirsøknadForeldrepenger(AktørId aktørId, Fødselsnummer fnr, AktørId aktørIdAnnenpart);
    Saksnummer sendInnPapirsøknadEEndringForeldrepenger(AktørId aktørId, Fødselsnummer fnr, AktørId aktørIdAnnenpart, Saksnummer saksnummer);
    Saksnummer sendInnPapirsøknadEngangsstønad(AktørId aktørId, Fødselsnummer fnr);
    void sendInnKlage(AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer);
    Saksnummer sendInnInntektsmelding(InntektsmeldingBuilder inntektsmelding, AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer);
    Saksnummer sendInnInntektsmelding(List<InntektsmeldingBuilder> inntektsmeldingBuilder, AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer);
    void sendInnHendelse(PersonhendelseDto personhendelseDto);
}
