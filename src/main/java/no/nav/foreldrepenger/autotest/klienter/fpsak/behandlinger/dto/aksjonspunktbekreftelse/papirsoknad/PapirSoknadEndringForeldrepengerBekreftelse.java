package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;

@BekreftelseKode(kode = "5057")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PapirSoknadEndringForeldrepengerBekreftelse extends AksjonspunktBekreftelse {

    private String tema = "FODSL"; // FamilieHendelseType
    private String soknadstype = "FP"; // FagsakYtelseType
    private String soker = "MOR"; // ForeldreType // burde vært RelasjonsRolleType?
    private LocalDate mottattDato = LocalDate.now();

    private FordelingDto tidsromPermisjon = new FordelingDto();

    public PapirSoknadEndringForeldrepengerBekreftelse() {
        super();
    }

    @JsonCreator
    public PapirSoknadEndringForeldrepengerBekreftelse(String tema, String soknadstype, String soker, LocalDate mottattDato, FordelingDto tidsromPermisjon) {
        this.tema = tema;
        this.soknadstype = soknadstype;
        this.soker = soker;
        this.mottattDato = mottattDato;
        this.tidsromPermisjon = tidsromPermisjon;
    }

    public String getTema() {
        return tema;
    }

    public String getSoknadstype() {
        return soknadstype;
    }

    public String getSoker() {
        return soker;
    }

    public LocalDate getMottattDato() {
        return mottattDato;
    }

    public FordelingDto getTidsromPermisjon() {
        return tidsromPermisjon;
    }

    public void setFordeling(FordelingDto tidsromPermisjon) {
        this.tidsromPermisjon = tidsromPermisjon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PapirSoknadEndringForeldrepengerBekreftelse that = (PapirSoknadEndringForeldrepengerBekreftelse) o;
        return Objects.equals(tema, that.tema) &&
                Objects.equals(soknadstype, that.soknadstype) &&
                Objects.equals(soker, that.soker) &&
                Objects.equals(mottattDato, that.mottattDato) &&
                Objects.equals(tidsromPermisjon, that.tidsromPermisjon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tema, soknadstype, soker, mottattDato, tidsromPermisjon);
    }

    @Override
    public String toString() {
        return "PapirSoknadEndringForeldrepengerBekreftelse{" +
                "tema='" + tema + '\'' +
                ", soknadstype='" + soknadstype + '\'' +
                ", soker='" + soker + '\'' +
                ", mottattDato=" + mottattDato +
                ", tidsromPermisjon=" + tidsromPermisjon +
                "} " + super.toString();
    }
}
