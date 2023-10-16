package no.nav.foreldrepenger.generator.soknad.api.dto;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.oppslag.dkif.Målform;

public record SøkerDto(BrukerRolle rolle,
                       Målform språkkode,
                       boolean erAleneOmOmsorg,
                       @Valid FrilansInformasjonDto frilansInformasjon,
                       @Valid @Size(max = 15) List<NæringDto> selvstendigNæringsdrivendeInformasjon,
                       @Valid @Size(max = 15) List<AnnenInntektDto> andreInntekterSiste10Mnd)  {
    public SøkerDto {
        selvstendigNæringsdrivendeInformasjon = Optional.ofNullable(selvstendigNæringsdrivendeInformasjon).orElse(emptyList());
        andreInntekterSiste10Mnd = Optional.ofNullable(andreInntekterSiste10Mnd).orElse(emptyList());
    }
}
