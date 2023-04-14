package no.nav.foreldrepenger.generator.soknad.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.NorskForelder;
import no.nav.foreldrepenger.common.innsending.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsending.mappers.V3ForeldrepengerDomainMapper;
import no.nav.foreldrepenger.generator.soknad.erketyper.SøknadForeldrepengerErketyper;

@Tag("internal")
class MapperForJSONTilXMLTest {

    @Test
    void sjekkerMapping() {
        var fødselsdato = LocalDate.now().minusWeeks(3);
        var søknadJson = SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTermin(fødselsdato, BrukerRolle.MOR)
                .medOpptjening(no.nav.foreldrepenger.generator.soknad.erketyper.OpptjeningErketyper.frilansOpptjening())
                .medAnnenForelder(new NorskForelder(new Fødselsnummer("12345678910"), null));

        var mapper = new V3ForeldrepengerDomainMapper(fnr -> new AktørId("111111111111"));
        var søknadXML = mapper.tilXML(søknadJson.build(), new AktørId("22222222222"), SøknadEgenskap.INITIELL_FORELDREPENGER);
        assertThat(søknadXML).isNotNull();
    }
}
