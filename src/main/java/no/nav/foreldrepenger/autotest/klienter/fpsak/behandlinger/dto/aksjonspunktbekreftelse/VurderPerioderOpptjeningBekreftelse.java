package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.opptjening.OpptjeningAktivitet;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@BekreftelseKode(kode="5051")
public class VurderPerioderOpptjeningBekreftelse extends AksjonspunktBekreftelse {

    protected List<OpptjeningAktivitet> opptjeningAktivitetList = new ArrayList<>();

    public VurderPerioderOpptjeningBekreftelse() {
        super();
    }

    public VurderPerioderOpptjeningBekreftelse godkjennAllOpptjening() {
        opptjeningAktivitetList.forEach(aktivitet -> aktivitet.vurder(true, "Godkjent", false));
        return this;
    }

    public VurderPerioderOpptjeningBekreftelse godkjennOpptjening(String aktivitetType) {
        for (OpptjeningAktivitet aktivitet : hentOpptjeningAktiviteter(aktivitetType)) {
            godkjennOpptjening(aktivitet);
        }
        return this;
    }

    public VurderPerioderOpptjeningBekreftelse avvisOpptjening(String aktivitetType) {
        for (OpptjeningAktivitet aktivitet : hentOpptjeningAktiviteter(aktivitetType)) {
            avvisOpptjening(aktivitet);
        }
        return this;
    }

    public VurderPerioderOpptjeningBekreftelse leggTilOpptjening(OpptjeningAktivitet aktivitet) {
        aktivitet.setErManueltOpprettet(true);
        opptjeningAktivitetList.add(aktivitet);
        return this;
    }

    private void godkjennOpptjening(OpptjeningAktivitet aktivitet) {
        aktivitet.vurder(true, "Godkjent", false);
    }
    private void avvisOpptjening(OpptjeningAktivitet aktivitet) {
        aktivitet.vurder(false, "Avvist", false);
    }
    private List<OpptjeningAktivitet> hentOpptjeningAktiviteter(String aktivitetType) {
        return opptjeningAktivitetList.stream()
                .filter(aktivitet -> aktivitet.getAktivitetType().kode.equals(aktivitetType)).collect(Collectors.toList());
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        if(behandling.getOpptjening().getOpptjeningAktivitetList() == null) {
            return;
        }

        for (OpptjeningAktivitet opptjeningAktivitet : behandling.getOpptjening().getOpptjeningAktivitetList()) {
            opptjeningAktivitet.setOriginalFom(opptjeningAktivitet.getOpptjeningFom());
            opptjeningAktivitet.setOriginalTom(opptjeningAktivitet.getOpptjeningTom());
            opptjeningAktivitet.setOppdragsgiverOrg(opptjeningAktivitet.getOppdragsgiverOrg());
            opptjeningAktivitet.setArbeidsforholdRef(opptjeningAktivitet.getArbeidsforholdRef());
            opptjeningAktivitet.setAktivitetType(opptjeningAktivitet.getAktivitetType());
            opptjeningAktivitetList.add(opptjeningAktivitet);
        }
    }
}
