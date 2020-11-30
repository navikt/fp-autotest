package no.nav.foreldrepenger.autotest.søknad.modell.felles.opptjening;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.neovisionaries.i18n.CountryCode;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.ProsentAndel;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.ÅpenPeriode;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UtenlandskOrganisasjon extends EgenNæring {


    private String orgName;
    @JsonAlias("arbeidsland")
    private final CountryCode registrertILand;

    @Builder
    private UtenlandskOrganisasjon(
            CountryCode registrertILand,
            List<Virksomhetstype> virksomhetsTyper, ÅpenPeriode periode,
            boolean nærRelasjon, List<Regnskapsfører> regnskapsførere, boolean erNyOpprettet, boolean erVarigEndring,
            boolean erNyIArbeidslivet, long næringsinntektBrutto, LocalDate endringsDato, LocalDate oppstartsDato,
            String beskrivelseEndring, ProsentAndel stillingsprosent,
            List<String> vedlegg, String orgName) {
        super(virksomhetsTyper, periode, nærRelasjon, regnskapsførere, erNyOpprettet,
                erVarigEndring, erNyIArbeidslivet,
                næringsinntektBrutto, endringsDato, oppstartsDato, beskrivelseEndring, stillingsprosent, vedlegg);
        this.orgName = orgName;
        this.registrertILand = registrertILand;
    }

}
