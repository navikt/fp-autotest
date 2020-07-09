package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto;

public class BrukerresponsDto {
    protected int behandlingId;
    protected boolean akseptertFaktagrunnlag;
    protected String kildeKanal;

    public BrukerresponsDto(int behandlingId) {
        this.behandlingId = behandlingId;
        this.kildeKanal = "MANUELL";
    }

    public void setAkseptertFaktagrunnlag(boolean akseptertFaktagrunnlag) {
        this.akseptertFaktagrunnlag = akseptertFaktagrunnlag;
    }
}
