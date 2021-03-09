package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Adresse {

    protected Kode adresseType;

    protected String adresselinje1;
    protected String adresselinje2;
    protected String adresselinje3;
    protected String land;
    protected String mottakerNavn;
    protected String postNummer;
    protected String poststed;
}
