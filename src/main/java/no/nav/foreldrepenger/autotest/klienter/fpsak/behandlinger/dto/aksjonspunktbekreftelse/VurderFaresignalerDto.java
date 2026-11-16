package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

/**
 * Speiler VurderFaresignalerDto i fp-sak (5095 VURDER_FARESIGNALER).
 */
public class VurderFaresignalerDto extends AksjonspunktBekreftelse {

    public enum FaresignalVurdering {
        INNVIRKNING,
        INNVILGET_REDUSERT,
        INNVILGET_UENDRET,
        AVSLAG_FARESIGNAL,
        AVSLAG_ANNET,
        INGEN_INNVIRKNING
    }

    protected FaresignalVurdering faresignalVurdering;

    public VurderFaresignalerDto setFaresignalVurdering(FaresignalVurdering faresignalVurdering) {
        this.faresignalVurdering = faresignalVurdering;
        return this;
    }

    public FaresignalVurdering getFaresignalVurdering() {
        return faresignalVurdering;
    }

    @Override
    public String aksjonspunktKode() {
        return "5095";
    }

    @Override
    public String toString() {
        return "VurderFaresignalerDto{faresignalVurdering=" + faresignalVurdering + "} " + super.toString();
    }
}
