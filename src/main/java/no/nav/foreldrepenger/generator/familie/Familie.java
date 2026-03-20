package no.nav.foreldrepenger.generator.familie;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.aktoerer.innsender.ApiMottak;
import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;
import no.nav.foreldrepenger.vtp.kontrakter.hendelser.DødfødselhendelseDto;
import no.nav.foreldrepenger.vtp.kontrakter.hendelser.DødshendelseDto;
import no.nav.foreldrepenger.vtp.kontrakter.hendelser.FødselshendelseDto;
import no.nav.foreldrepenger.vtp.kontrakter.hendelser.PersonhendelseDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.PersonDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.personopplysninger.Rolle;

public class Familie {


    private static final Logger LOG = LoggerFactory.getLogger(Familie.class);

    private final List<PersonDto> parter;
    private final Innsender innsender;
    private static final String ENDRINGSTYPE_OPPRETTET = "OPPRETTET";

    private Mor mor;
    private Far far;
    private Mor medmor;

    public Familie(List<PersonDto> parter, SaksbehandlerRolle saksbehandlerRolle) {
        this.parter = parter;
        this.innsender = new ApiMottak(saksbehandlerRolle);
        initMor();
        initFar();
        initMedmor();
        LOG.info("Familie opprettet med mor: {}, far/medmor: {}", mor != null ? mor.ident() : "Ingen mor",
                far != null ? far.ident() : medmor != null ? medmor.ident() : "Ingen far/medmor");
    }

    private void initMedmor() {
        var medmorPerson = parter.stream().filter(p -> p.personopplysninger().rolle().equals(Rolle.MEDMOR)).findFirst();
        if (medmorPerson.isEmpty()) {
            return;
        }
        var annenpartIdent = parter.stream().filter(p -> p.personopplysninger().rolle().equals(Rolle.MOR)).findFirst().map(ap -> Ident.fra(ap.personopplysninger().fnr()));
        medmor = new Mor(Ident.fra(medmorPerson.get().personopplysninger().fnr()), annenpartIdent.orElse(null), medmorPerson.get(), innsender);
    }

    private void initFar() {
        var farPerson = parter.stream().filter(p -> p.personopplysninger().rolle().equals(Rolle.FAR)).findFirst();
        if (farPerson.isEmpty()) {
            return;
        }
        var annenpartIdent = parter.stream().filter(p -> p.personopplysninger().rolle().equals(Rolle.MOR)).findFirst().map(ap -> Ident.fra(ap.personopplysninger().fnr()));
        far = new Far(Ident.fra(farPerson.get().personopplysninger().fnr()), annenpartIdent.orElse(null), farPerson.get(), innsender);
    }

    private void initMor() {
        var morPerson = parter.stream().filter(p -> p.personopplysninger().rolle().equals(Rolle.MOR)).findFirst();
        if (morPerson.isEmpty()) {
            return;
        }
        var annenpartIdent = parter.stream()
                .filter(p -> p.personopplysninger().rolle().equals(Rolle.FAR) || p.personopplysninger().rolle().equals(Rolle.MEDMOR))
                .findFirst()
                .map(ap -> Ident.fra(ap.personopplysninger().fnr()));
        mor = new Mor(Ident.fra(morPerson.get().personopplysninger().fnr()), annenpartIdent.orElse(null), morPerson.get(), innsender);
    }

    public Mor mor() {
        if (mor == null) {
            throw new IllegalStateException("Ingen mor i familien");
        }
        return mor;
    }

    public Mor medmor() {
        if (medmor == null) {
            throw new IllegalStateException("Ingen medmor i familien");
        }
        return medmor;
    }

    public Far far() {
        if (far == null) {
            throw new IllegalStateException("Ingen far i familien");
        }
        return far;
    }

    public Barn barn() {
        var barn = parter.stream().filter(p -> p.personopplysninger().rolle().equals(Rolle.BARN)).toList();
        if (barn.isEmpty()) {
            throw new IllegalStateException("Barn er enda ikke født for familie");
        }
        return new Barn(barn.stream().map(p -> p.personopplysninger().fødselsdato()).findFirst().orElse(null));
    }

    public void sendInnDødshendelse(Fødselsnummer fnr, LocalDate dødsdato) {
        sendInnHendelse(new DødshendelseDto(ENDRINGSTYPE_OPPRETTET, null, fnr.value(), dødsdato));
    }

    public void sendInnDødfødselhendelse(LocalDate dødfødselsdato) {
        sendInnHendelse(new DødfødselhendelseDto(ENDRINGSTYPE_OPPRETTET, null, mor.fødselsnummer().value(), dødfødselsdato));
    }

    public void sendInnFødselshendelse(LocalDate fødselsdato) {
        var farEllerMedmor = far != null ? far : medmor;
        var fødselshendelseDto = new FødselshendelseDto(ENDRINGSTYPE_OPPRETTET, null,
                Optional.ofNullable(mor).map(m -> m.fødselsnummer().value()).orElse(null),
                Optional.ofNullable(farEllerMedmor).map(a -> a.fødselsnummer().value()).orElse(null), null, fødselsdato);
        sendInnHendelse(fødselshendelseDto);
    }

    private void sendInnHendelse(PersonhendelseDto personhendelseDto) {
        innsender.sendInnHendelse(personhendelseDto);
    }
}
