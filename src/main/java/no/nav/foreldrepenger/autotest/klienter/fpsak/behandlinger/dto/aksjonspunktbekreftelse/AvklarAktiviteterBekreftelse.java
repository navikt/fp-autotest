package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@BekreftelseKode(kode="5052")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AvklarAktiviteterBekreftelse extends AksjonspunktBekreftelse {

    protected List<BeregningsaktivitetLagreDto> beregningsaktivitetLagreDtoList;

    public AvklarAktiviteterBekreftelse() {
        super();
    }

    public AvklarAktiviteterBekreftelse setSkalBrukes(boolean skalBrukes, String orgnr) {
        BeregningsaktivitetLagreDto vurdering = beregningsaktivitetLagreDtoList.stream()
                .filter(a -> a.oppdragsgiverOrg.equals(orgnr))
                .findFirst().get();
        vurdering.skalBrukes = skalBrukes;
        return this;
    }
        public AvklarAktiviteterBekreftelse godkjennOpptjeningsAktivitet(String opptjeningsAktivitetType) {
        BeregningsaktivitetLagreDto vurdering = beregningsaktivitetLagreDtoList.stream()
                .filter(aktivitet -> aktivitet.opptjeningAktivitetType.kode.equals(opptjeningsAktivitetType))
                .findFirst().get();
        vurdering.skalBrukes = true;
        return this;
    }
    public AvklarAktiviteterBekreftelse avvisOpptjeningsAktivitet(String opptjeningsAktivitetType) {
        BeregningsaktivitetLagreDto vurdering = beregningsaktivitetLagreDtoList.stream()
                .filter(aktivitet -> aktivitet.opptjeningAktivitetType.kode.equals(opptjeningsAktivitetType))
                .findFirst().get();
        vurdering.skalBrukes = false;
        return this;
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        beregningsaktivitetLagreDtoList = behandling.getBeregningsgrunnlag().getFaktaOmBeregning().getAvklarAktiviteter()
                .getAktiviteterTomDatoMapping().get(0)
                .getAktiviteter()
                .stream()
                .map(aktivitet -> new BeregningsaktivitetLagreDto(aktivitet.getArbeidsforholdType(), aktivitet.getFom(), aktivitet.getTom(),
                        aktivitet.getArbeidsgiverId(), aktivitet.getAktørId() == null ? null : aktivitet.getAktørId().getAktørId(), aktivitet.getArbeidsforholdId(), true))
                .collect(Collectors.toList());
    }
}
