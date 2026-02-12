package no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak;

import java.time.LocalDate;
import java.util.Set;

import no.nav.foreldrepenger.autotest.klienter.fplos.dto.KøSortering;
import no.nav.foreldrepenger.autotest.klienter.fplos.dto.Periodefilter;
import no.nav.foreldrepenger.autotest.klienter.fplos.dto.SakslisteIdDto;
import no.nav.foreldrepenger.autotest.klienter.fplos.dto.SakslisteLagreDto;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;

public class Oppgavestyrer extends Saksbehandler {
    public static final String DEFAULT_AVDELING = "4867";

    public Oppgavestyrer() {
        super(SaksbehandlerRolle.OPPGAVESTYRER);
    }

    public SakslisteIdDto opprettSaksliste() {
        var sakslisteIdDto = fplosKlient.opprettNySaksliste(DEFAULT_AVDELING);
        var nySaksliste = new SakslisteLagreDto(
                DEFAULT_AVDELING,
                sakslisteIdDto.sakslisteId(),
                "A001 - Oppgavestyring grunnleggende",
                new SakslisteLagreDto.SorteringDto(KøSortering.OPPRBEH, Periodefilter.FAST_PERIODE, null, null, LocalDate.now().minusMonths(1), null),
                Set.of(),
                Set.of(),
                new SakslisteLagreDto.AndreKriterieDto(Set.of(), Set.of())
        );
        fplosKlient.endreEksistrendeSaksliste(nySaksliste);
        return sakslisteIdDto;
    }

}
