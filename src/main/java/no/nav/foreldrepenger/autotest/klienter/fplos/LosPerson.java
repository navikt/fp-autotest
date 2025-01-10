package no.nav.foreldrepenger.autotest.klienter.fplos;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;

import java.time.LocalDate;

record LosPerson(String navn, String kjønn, Fødselsnummer fødselsnummer, LocalDate fødselsdato, String diskresjonskode) { }
