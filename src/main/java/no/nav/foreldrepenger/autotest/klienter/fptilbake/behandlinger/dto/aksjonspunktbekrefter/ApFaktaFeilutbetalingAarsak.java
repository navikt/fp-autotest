package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

public class ApFaktaFeilutbetalingAarsak {

    protected ApFaktaFeilutbetalingAarsakHendelseTyper hendelseType = new ApFaktaFeilutbetalingAarsakHendelseTyper();
    protected ApFaktaFeilutbetalingAarsakHendelseTyper hendelseUndertype = new ApFaktaFeilutbetalingAarsakHendelseTyper();

    public void addGeneriskHendelser() {
        this.hendelseType.kode = "OPPTJENING_TYPE";
        this.hendelseType.navn = "§14-6 Opptjening";
        this.hendelseType.kodeverk = "HENDELSE_TYPE";

        this.hendelseUndertype.kode = "IKKE_INNTEKT";
        this.hendelseUndertype.navn = "Ikke inntekt 6 av siste 10 måneder";
        this.hendelseUndertype.kodeverk = "HENDELSE_UNDERTYPE";
    }
}
