package no.nav.foreldrepenger.autotest.søknad.modell.felles.opptjening;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.ProsentAndel;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.ÅpenPeriode;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Orgnummer;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NorskOrganisasjon extends EgenNæring {

    private final Orgnummer orgNummer;
    private final String orgName;

    @Builder
    private NorskOrganisasjon(List<Virksomhetstype> virksomhetsTyper, ÅpenPeriode periode,
            boolean nærRelasjon, List<Regnskapsfører> regnskapsførere, boolean erNyOpprettet, boolean erVarigEndring,
            boolean erNyIArbeidslivet, long næringsinntektBrutto, LocalDate endringsDato, LocalDate oppstartsDato,
            String beskrivelseEndring, ProsentAndel stillingsprosent, List<String> vedlegg,
            Orgnummer orgNummer, String orgName) {
        super(virksomhetsTyper, periode, nærRelasjon, regnskapsførere, erNyOpprettet,
                erVarigEndring, erNyIArbeidslivet,
                næringsinntektBrutto, endringsDato, oppstartsDato, beskrivelseEndring, stillingsprosent, vedlegg);
        this.orgName = orgName;
        this.orgNummer = orgNummer;
    }
}
