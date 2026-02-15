package no.nav.foreldrepenger.generator.familie;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.autotest.aktoerer.innsender.ApiMottak;
import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.kontrakter.felles.typer.AktørId;
import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;
import no.nav.foreldrepenger.vtp.kontrakter.DødfødselhendelseDto;
import no.nav.foreldrepenger.vtp.kontrakter.DødshendelseDto;
import no.nav.foreldrepenger.vtp.kontrakter.FødselshendelseDto;
import no.nav.foreldrepenger.vtp.kontrakter.PersonhendelseDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.PersonDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.Rolle;
import no.nav.foreldrepenger.vtp.kontrakter.v2.TilordnetIdentDto;

public class Familie {

    private final List<PersonDto> parter;
    private final Map<UUID, TilordnetIdentDto> identer;
    private final Innsender innsender;
    private static final String ENDRINGSTYPE_OPPRETTET = "OPPRETTET";

    private Mor mor;
    private Far far;
    private Mor medmor;

    public Familie(List<PersonDto> parter, List<TilordnetIdentDto> identer, SaksbehandlerRolle saksbehandlerRolle) {
        this.parter = parter;
        this.identer = identer.stream().collect(Collectors.toMap(TilordnetIdentDto::id, Function.identity()));
        this.innsender = new ApiMottak(saksbehandlerRolle);
    }


    public Mor mor() {
        if (mor == null) {
            var morPerson = parter.stream()
                    .filter(p -> p.rolle().equals(no.nav.foreldrepenger.vtp.kontrakter.v2.Rolle.MOR))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Ingen mor i familien"));
            var morIdent = identer.get(morPerson.id());
            var annenpartIdent = parter.stream()
                    .filter(p -> p.rolle().equals(Rolle.FAR) || p.rolle().equals(Rolle.MEDMOR))
                    .findFirst()
                    .map(ap -> identer.get(ap.id()));
            mor = new Mor(
                    new Fødselsnummer(morIdent.fnr()),
                    new AktørId(morIdent.aktørId()),
                    annenpartIdent.map(TilordnetIdentDto::aktørId).map(AktørId::new).orElse(null),
                    morPerson,
                    identer,
                    innsender);
        }
        return mor;

    }

    public Mor medmor() {
        if (medmor == null) {
            var medmorPerson = parter.stream()
                    .filter(p -> p.rolle().equals(Rolle.MEDMOR))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Ingen medmor i familien"));
            var morIdent = identer.get(medmorPerson.id());
            var annenpartIdent = parter.stream()
                    .filter(p -> p.rolle().equals(Rolle.MOR))
                    .findFirst()
                    .map(ap -> identer.get(ap.id()));
            medmor = new Mor(
                    new Fødselsnummer(morIdent.fnr()),
                    new AktørId(morIdent.aktørId()),
                    annenpartIdent.map(TilordnetIdentDto::aktørId).map(AktørId::new).orElse(null),
                    medmorPerson,
                    identer,
                    innsender);
        }
        return medmor;
    }

    public Far far() {
        if (far == null) {
            var farPerson = parter.stream()
                    .filter(p -> p.rolle().equals(Rolle.FAR))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Ingen far i familien"));
            var morIdent = identer.get(farPerson.id());
            var annenpartIdent = parter.stream()
                    .filter(p -> p.rolle().equals(Rolle.MOR))
                    .findFirst()
                    .map(ap -> identer.get(ap.id()));
            far = new Far(
                    new Fødselsnummer(morIdent.fnr()),
                    new AktørId(morIdent.aktørId()),
                    annenpartIdent.map(TilordnetIdentDto::aktørId).map(AktørId::new).orElse(null),
                    farPerson,
                    identer,
                    innsender);
        }
        return far;
    }

    public Barn barn() {
        var barn = parter.stream().filter(p -> p.rolle().equals(Rolle.BARN)).toList();
        if (barn.isEmpty()) {
            throw new IllegalStateException("Barn er enda ikke født for familie");
        }
        return new Barn(barn.stream().map(PersonDto::fødselsdato).findFirst().orElse(null));
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
                Optional.ofNullable(farEllerMedmor).map(a -> a.fødselsnummer().value()).orElse(null),
                null, fødselsdato);
        sendInnHendelse(fødselshendelseDto);
    }

    private void sendInnHendelse(PersonhendelseDto personhendelseDto) {
        innsender.sendInnHendelse(personhendelseDto);
    }

}
