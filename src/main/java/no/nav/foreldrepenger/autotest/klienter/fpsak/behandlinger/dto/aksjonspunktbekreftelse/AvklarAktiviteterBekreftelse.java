package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.OpptjeningAktivitetType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.common.domain.ArbeidsgiverIdentifikator;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AvklarAktiviteterBekreftelse extends AksjonspunktBekreftelse {

    protected List<BeregningsaktivitetLagreDto> beregningsaktivitetLagreDtoList;

    public AvklarAktiviteterBekreftelse() {
        super();
    }

    public AvklarAktiviteterBekreftelse setSkalBrukes(boolean skalBrukes, ArbeidsgiverIdentifikator orgnr) {
        BeregningsaktivitetLagreDto vurdering = beregningsaktivitetLagreDtoList.stream()
                .filter(a -> a.oppdragsgiverOrg != null && a.oppdragsgiverOrg.equalsIgnoreCase(orgnr.value()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Fant ingen beregningsaktivitet med orgnummer " + orgnr));
        vurdering.skalBrukes = skalBrukes;
        return this;
    }

    public AvklarAktiviteterBekreftelse godkjennOpptjeningsAktivitet(OpptjeningAktivitetType opptjeningsAktivitetType) {
        var vurdering = hentBeregningsaktivitetMedOpptjenignsaktivitetstype(opptjeningsAktivitetType);
        vurdering.skalBrukes = true;
        return this;
    }

    public AvklarAktiviteterBekreftelse avvisOpptjeningsAktivitet(OpptjeningAktivitetType opptjeningsAktivitetType) {
        var vurdering = hentBeregningsaktivitetMedOpptjenignsaktivitetstype(opptjeningsAktivitetType);
        vurdering.skalBrukes = false;
        return this;
    }

    private BeregningsaktivitetLagreDto hentBeregningsaktivitetMedOpptjenignsaktivitetstype(OpptjeningAktivitetType opptjeningsAktivitetType) {
        return beregningsaktivitetLagreDtoList.stream()
                    .filter(aktivitet -> aktivitet.opptjeningAktivitetType.equals(opptjeningsAktivitetType))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Fant ikke beregningsaktivitet med opptjeningsaktivetetstype " + opptjeningsAktivitetType));
    }

    @Override
    public String aksjonspunktKode() {
        return "5052";
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
                        aktivitet.getArbeidsgiverIdent(),
                        aktivitet.getAktørId() == null ? null : aktivitet.getAktørId().getAktørId(),
                        aktivitet.getArbeidsforholdId(), true))
                .toList();
    }
}
