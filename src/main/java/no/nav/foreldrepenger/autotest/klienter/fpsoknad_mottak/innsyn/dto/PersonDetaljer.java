package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.innsyn.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.innsyn.dto.persondetaljer.AktørId;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.innsyn.dto.persondetaljer.Person;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = AktørId.class, name = "aktørId"),
    @JsonSubTypes.Type(value = Person.class, name = "person")
})
public interface PersonDetaljer {
}
