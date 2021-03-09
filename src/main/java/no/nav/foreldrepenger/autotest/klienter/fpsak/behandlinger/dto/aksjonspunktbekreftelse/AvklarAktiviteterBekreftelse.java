package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.OpptjeningAktivitetType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@BekreftelseKode(kode = "5052")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AvklarAktiviteterBekreftelse extends AksjonspunktBekreftelse {

    protected List<BeregningsaktivitetLagreDto> beregningsaktivitetLagreDtoList;

    public AvklarAktiviteterBekreftelse() {
        super();
    }

    public AvklarAktiviteterBekreftelse setSkalBrukes(boolean skalBrukes, String orgnr) {
        BeregningsaktivitetLagreDto vurdering = beregningsaktivitetLagreDtoList.stream()
                .filter(a -> a.oppdragsgiverOrg.equals(orgnr))
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
}
