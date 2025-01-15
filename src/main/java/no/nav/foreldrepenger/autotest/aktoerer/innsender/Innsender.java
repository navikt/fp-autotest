package no.nav.foreldrepenger.autotest.aktoerer.innsender;

import java.util.List;

import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.SøknadDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.endringssøknad.EndringssøknadDto;
import no.nav.foreldrepenger.vtp.kontrakter.PersonhendelseDto;

public interface Innsender {
    Logger LOG = LoggerFactory.getLogger(Innsender.class);

    Saksnummer sendInnSøknad(SøknadDto søknad, AktørId aktørId, Fødselsnummer fnr, AktørId aktørIdAnnenpart, Saksnummer saksnummer);
    Saksnummer sendInnSøknad(no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.dto.SøknadDto søknad, AktørId aktørId, Fødselsnummer fnr, AktørId aktørIdAnnenpart, Saksnummer saksnummer);
    Saksnummer sendInnSøknad(EndringssøknadDto søknad, AktørId aktørId, Fødselsnummer fnr, AktørId aktørIdAnnenpart, Saksnummer saksnummer);
    Saksnummer sendInnPapirsøknadForeldrepenger(AktørId aktørId, Fødselsnummer fnr, AktørId aktørIdAnnenpart);
    Saksnummer sendInnPapirsøknadEEndringForeldrepenger(AktørId aktørId, Fødselsnummer fnr, AktørId aktørIdAnnenpart, Saksnummer saksnummer);
    Saksnummer sendInnPapirsøknadEngangsstønad(AktørId aktørId, Fødselsnummer fnr);
    void sendInnKlage(AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer);
    Saksnummer sendInnInntektsmelding(Inntektsmelding inntektsmelding, AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer);
    Saksnummer sendInnInntektsmelding(List<Inntektsmelding> inntektsmeldingBuilder, AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer);
    void sendInnHendelse(PersonhendelseDto personhendelseDto);
}
