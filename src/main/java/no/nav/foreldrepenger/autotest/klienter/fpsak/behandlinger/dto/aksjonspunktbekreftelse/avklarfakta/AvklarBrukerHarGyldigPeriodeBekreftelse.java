package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import java.util.List;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.MedlemskapManuellVurderingType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.medlem.BekreftedePerioderDto;

@BekreftelseKode(kode = "5021")
public class AvklarBrukerHarGyldigPeriodeBekreftelse extends BekreftedePerioderMalDto {

    public AvklarBrukerHarGyldigPeriodeBekreftelse() {
        super();
    }

    public AvklarBrukerHarGyldigPeriodeBekreftelse setVurdering(MedlemskapManuellVurderingType medlem,
                                                                List<BekreftedePerioderDto> medlemskapPerioder) {
        for (BekreftedePerioderDto bekreftedePerioderDto : medlemskapPerioder) {
            bekreftedePerioderDto.setMedlemskapManuellVurderingType(medlem);
            bekreftedePerioder.add(bekreftedePerioderDto);
        }
        return this;
    }
}
