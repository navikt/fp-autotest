package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@BekreftelseKode(kode = "5052")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AvklarAktiviteterBekreftelse extends AksjonspunktBekreftelse {

    private List<BeregningsaktivitetLagreDto> beregningsaktivitetLagreDtoList;

    public AvklarAktiviteterBekreftelse() {
        super();
    }

    public AvklarAktiviteterBekreftelse(@JsonProperty("beregningsaktivitetLagreDtoList") List<BeregningsaktivitetLagreDto> beregningsaktivitetLagreDtoList) {
        this.beregningsaktivitetLagreDtoList = beregningsaktivitetLagreDtoList;
    }

    public AvklarAktiviteterBekreftelse setSkalBrukes(boolean skalBrukes, String orgnr) {
        BeregningsaktivitetLagreDto vurdering = beregningsaktivitetLagreDtoList.stream()
                .filter(a -> a.getOppdragsgiverOrg().equals(orgnr))
                .findFirst().get();
        vurdering.setSkalBrukes(skalBrukes);
        return this;
    }

    public AvklarAktiviteterBekreftelse godkjennOpptjeningsAktivitet(String opptjeningsAktivitetType) {
        BeregningsaktivitetLagreDto vurdering = beregningsaktivitetLagreDtoList.stream()
                .filter(aktivitet -> aktivitet.getOpptjeningAktivitetType().kode.equals(opptjeningsAktivitetType))
                .findFirst().get();
        vurdering.setSkalBrukes(true);
        return this;
    }

    public AvklarAktiviteterBekreftelse avvisOpptjeningsAktivitet(String opptjeningsAktivitetType) {
        BeregningsaktivitetLagreDto vurdering = beregningsaktivitetLagreDtoList.stream()
                .filter(aktivitet -> aktivitet.getOpptjeningAktivitetType().kode.equals(opptjeningsAktivitetType))
                .findFirst().get();
        vurdering.setSkalBrukes(false);
        return this;
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        beregningsaktivitetLagreDtoList = behandling.getBeregningsgrunnlag().getFaktaOmBeregning()
                .getAvklarAktiviteter()
                .getAktiviteterTomDatoMapping().get(0)
                .getAktiviteter()
                .stream()
                .map(aktivitet -> new BeregningsaktivitetLagreDto(aktivitet.getArbeidsforholdType(), aktivitet.getFom(),
                        aktivitet.getTom(),
                        aktivitet.getArbeidsgiverId(),
                        aktivitet.getAktørId() == null ? null : aktivitet.getAktørId().getAktørId(),
                        aktivitet.getArbeidsforholdId(), true))
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvklarAktiviteterBekreftelse that = (AvklarAktiviteterBekreftelse) o;
        return Objects.equals(beregningsaktivitetLagreDtoList, that.beregningsaktivitetLagreDtoList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beregningsaktivitetLagreDtoList);
    }

    @Override
    public String toString() {
        return "AvklarAktiviteterBekreftelse{" +
                "beregningsaktivitetLagreDtoList=" + beregningsaktivitetLagreDtoList +
                '}';
    }
}
