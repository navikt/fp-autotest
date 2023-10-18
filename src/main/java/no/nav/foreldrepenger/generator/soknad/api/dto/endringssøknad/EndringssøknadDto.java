package no.nav.foreldrepenger.generator.soknad.api.dto.endringssøknad;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.generator.soknad.api.dto.BarnDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.MottattTidspunkt;
import no.nav.foreldrepenger.generator.soknad.api.dto.SøkerDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.VedleggDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger.Situasjon;

@JsonTypeInfo(use = NAME, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = EndringssøknadForeldrepengerDto.class, name = "foreldrepenger")
})
public interface EndringssøknadDto extends MottattTidspunkt {
    Saksnummer saksnummer();
    Situasjon situasjon();
    BarnDto barn();
    SøkerDto søker();
    List<VedleggDto> vedlegg();

    default String type() {
        if (this instanceof EndringssøknadForeldrepengerDto) return "foreldrepenger";
        throw new IllegalStateException("Utvikerfeil: Kan ikke ha en annen ytelse enn fp!");
    }
}
