package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

class RefusjonskravPrArbeidsgiverVurderingDto {

    protected String arbeidsgiverId;
    protected boolean skalUtvideGyldighet;

    public RefusjonskravPrArbeidsgiverVurderingDto(String arbeidsgiverId, boolean skalUtvideGyldighet) {
        this.arbeidsgiverId = arbeidsgiverId;
        this.skalUtvideGyldighet = skalUtvideGyldighet;
    }
}
