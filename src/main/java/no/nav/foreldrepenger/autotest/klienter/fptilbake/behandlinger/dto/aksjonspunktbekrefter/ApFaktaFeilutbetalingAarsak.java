package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

public class ApFaktaFeilutbetalingAarsak {

    protected ApFaktaFeilutbetalingAarsakHendelseTyper hendelseType = new ApFaktaFeilutbetalingAarsakHendelseTyper();
    protected ApFaktaFeilutbetalingAarsakHendelseTyper hendelseUndertype = new ApFaktaFeilutbetalingAarsakHendelseTyper();

    public void addGeneriskHendelser(String ytelseType){
        switch (ytelseType){
            case "FP":
                addGeneriskHendelserForeldrepenger();
                break;
            case "SVP":
                addGeneriskHendelserSvangerskapspenger();
                break;
            case "ES":
                addGeneriskHendelserEngangsstonad();
                break;
            default:
                throw new IllegalArgumentException(ytelseType + " er ikke en gyldig ytelseType");
        }
        this.hendelseType.kodeverk = "HENDELSE_TYPE";
        this.hendelseUndertype.kodeverk = "HENDELSE_UNDERTYPE";
    }

    private void addGeneriskHendelserForeldrepenger() {
        this.hendelseType.kode = "OPPTJENING_TYPE";
        this.hendelseType.navn = "§14-6 Opptjening";

        this.hendelseUndertype.kode = "IKKE_INNTEKT";
        this.hendelseUndertype.navn = "Ikke inntekt 6 av siste 10 måneder";
    }
    private void addGeneriskHendelserSvangerskapspenger() {
        this.hendelseType.kode = "SVP_FAKTA_TYPE";
        this.hendelseType.navn = "§14-4 Fakta om svangerskap";

        this.hendelseUndertype.kode = "SVP_IKKE_HELSEFARLIG";
        this.hendelseUndertype.navn = "Ikke helsefarlig for ventende barn";
    }
    private void addGeneriskHendelserEngangsstonad() {
        this.hendelseType.kode = "ES_ADOPSJONSVILKAARET_TYPE";
        this.hendelseType.navn = "§14-17 1. ledd Adopsjonsvilkåret";

        this.hendelseUndertype.kode = "ES_IKKE_OPPFYLT";
        this.hendelseUndertype.navn = "Adopsjonsvilkår ikke oppfylt";
    }
}
