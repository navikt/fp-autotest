package no.nav.foreldrepenger.autotest.aktoerer.innsender;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.generator.familie.AktørId;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;
import no.nav.foreldrepenger.kontrakter.fpsoknad.EndringssøknadForeldrepengerDto;
import no.nav.foreldrepenger.kontrakter.fpsoknad.Fødselsnummer;
import no.nav.foreldrepenger.kontrakter.fpsoknad.Saksnummer;
import no.nav.foreldrepenger.kontrakter.fpsoknad.SøknadDto;
import no.nav.foreldrepenger.kontrakter.fpsoknad.ettersendelse.YtelseType;
import no.nav.foreldrepenger.kontrakter.fpsoknad.vedlegg.VedleggDto;
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
    Saksnummer sendInnInntektsmeldingUtenForespørsel(Inntektsmelding inntektsmelding, LocalDate startDato, AktørId aktørId, Fødselsnummer fødselsnummer, Saksnummer saksnummer, boolean registrertIAareg);
    void sendInnHendelse(PersonhendelseDto personhendelseDto);

}
