package no.nav.foreldrepenger.autotest.aktoerer.innsender;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.EndringssøknadForeldrepengerDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.SøknadDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.VedleggDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.ettersendelse.YtelseType;
import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;
import no.nav.foreldrepenger.vtp.kontrakter.PersonhendelseDto;

public interface Innsender {
    Logger LOG = LoggerFactory.getLogger(Innsender.class);

    Saksnummer sendInnSøknad(SøknadDto søknad, AktørId aktørId, Fødselsnummer fnr, AktørId aktørIdAnnenpart, Saksnummer saksnummer);
    Saksnummer sendInnSøknad(EndringssøknadForeldrepengerDto søknad, AktørId aktørId, Fødselsnummer fnr, AktørId aktørIdAnnenpart, Saksnummer saksnummer);
    Saksnummer sendInnPapirsøknadForeldrepenger(AktørId aktørId, Fødselsnummer fnr, AktørId aktørIdAnnenpart);
    Saksnummer sendInnPapirsøknadEEndringForeldrepenger(AktørId aktørId, Fødselsnummer fnr, AktørId aktørIdAnnenpart, Saksnummer saksnummer);
    Saksnummer sendInnPapirsøknadEngangsstønad(AktørId aktørId, Fødselsnummer fnr);
    void sendInnKlage(AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer);
    void ettersendVedlegg(Fødselsnummer fnr, Saksnummer saksnummer, YtelseType ytelseType, VedleggDto vedlegg);
    Saksnummer sendInnInntektsmelding(Inntektsmelding inntektsmelding, AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer);
    Saksnummer sendInnInntektsmelding(List<Inntektsmelding> inntektsmeldingBuilder, AktørId aktørId, Fødselsnummer fnr, Saksnummer saksnummer);
    void sendInnHendelse(PersonhendelseDto personhendelseDto);
}
