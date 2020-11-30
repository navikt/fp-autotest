package no.nav.foreldrepenger.autotest.søknad.modell.felles.opptjening;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.ÅpenPeriode;


@Data
@ToString(exclude = "vedlegg")
@EqualsAndHashCode(exclude = "vedlegg")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UtenlandskArbeidsforhold {

    private final CountryCode land;
    private final String arbeidsgiverNavn;
    @NotNull
    private final ÅpenPeriode periode;
    private final List<String> vedlegg;

    @Builder
    @JsonCreator
    public UtenlandskArbeidsforhold(@JsonProperty("arbeidsgiverNavn") String arbeidsgiverNavn,
                                    @JsonProperty("periode") ÅpenPeriode periode,
                                    @JsonProperty("vedlegg") List<String> vedlegg,
                                    @JsonProperty("land") CountryCode land) {
        this.arbeidsgiverNavn = arbeidsgiverNavn;
        this.periode = periode;
        this.vedlegg = Optional.ofNullable(vedlegg).orElse(emptyList());
        this.land = land;
    }

}
