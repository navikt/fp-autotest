package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

public class ApFaktaFeilutbetalingAarsak {

    protected String hendelseType;
    protected String hendelseUndertype;

    public void addGeneriskHendelser(String ytelseType) {
        switch (ytelseType) {
            case "FP" -> addGeneriskHendelserForeldrepenger();
            case "SVP" -> addGeneriskHendelserSvangerskapspenger();
            case "ES" ->addGeneriskHendelserEngangsstonad();
            default ->throw new IllegalArgumentException(ytelseType + " er ikke en gyldig ytelseType");
        }
    }

    private void addGeneriskHendelserForeldrepenger() {
        this.hendelseType = "OPPTJENING_TYPE";

        this.hendelseUndertype = "IKKE_INNTEKT";
    }

    private void addGeneriskHendelserSvangerskapspenger() {
        this.hendelseType = "SVP_FAKTA_TYPE";

        this.hendelseUndertype = "SVP_IKKE_HELSEFARLIG";
    }

    private void addGeneriskHendelserEngangsstonad() {
        this.hendelseType = "ES_ADOPSJONSVILKAARET_TYPE";

        this.hendelseUndertype = "ES_IKKE_OPPFYLT";
    }
}
