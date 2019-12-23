package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.opptjening.OpptjeningAktivitet;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@BekreftelseKode(kode="5051")
public class VurderPerioderOpptjeningBekreftelse extends AksjonspunktBekreftelse {

    protected List<OpptjeningAktivitet> opptjeningAktivitetList = new ArrayList<>();

    public VurderPerioderOpptjeningBekreftelse() {
        super();
    }
    public List<OpptjeningAktivitet> hentOpptjeningAktiviteter(String aktivitetType) {
        return opptjeningAktivitetList.stream()
                .filter(aktivitet -> aktivitet.getAktivitetType().kode.equals(aktivitetType)).collect(Collectors.toList());
    }

    public VurderPerioderOpptjeningBekreftelse godkjennOpptjening(String aktivitetType) {
        for (OpptjeningAktivitet aktivitet : hentOpptjeningAktiviteter(aktivitetType)) {
            aktivitet.vurder(true, "Godkjent", false);
        }
        return this;
    }

    public VurderPerioderOpptjeningBekreftelse avvisOpptjening(String aktivitetType) {
        for (OpptjeningAktivitet aktivitet : hentOpptjeningAktiviteter(aktivitetType)) {
            aktivitet.vurder(false, "Avvist", false);
        }
        return this;
    }

    public void godkjennOpptjening(OpptjeningAktivitet aktivitet) {
        aktivitet.vurder(true, "Godkjent", false);
    }

    public void avvisOpptjening(OpptjeningAktivitet aktivitet) {
        aktivitet.vurder(false, "Avvist", false);
    }

    public void leggTilOpptjening(OpptjeningAktivitet aktivitet) {
        aktivitet.setErManueltOpprettet(true);
        opptjeningAktivitetList.add(aktivitet);
    }

    public void godkjennAllOpptjening(){
        opptjeningAktivitetList.forEach(aktivitet -> aktivitet.vurder(true, "Godkjent", false));
    }

    @Override
    public void setFagsakOgBehandling(Fagsak fagsak, Behandling behandling) {
        super.setFagsakOgBehandling(fagsak, behandling);
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
