package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.AnnenForelderDto;

@BekreftelseKode(kode = "5012")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PapirSoknadEngangstonadBekreftelse extends AksjonspunktBekreftelse {
    private String tema = "FODSL";
    private String soknadstype = "ES";
    private LocalDate mottattDato = LocalDate.now().minusDays(10);

    private boolean harFremtidigOppholdUtlands = false;
    private boolean harTidligereOppholdUtenlands = false;
    private boolean oppholdINorge = true;

    private String soker = "MOR";
    private boolean erBarnetFodt = true;
    private Integer antallBarn = 1;
    private List<LocalDate> foedselsDato = Collections.singletonList(LocalDate.now().minusWeeks(1));
    private AnnenForelderDto annenForelder = new AnnenForelderDto();

    public PapirSoknadEngangstonadBekreftelse() {
        super();
    }

    @JsonCreator
    public PapirSoknadEngangstonadBekreftelse(String tema, String soknadstype, LocalDate mottattDato,
                                              boolean harFremtidigOppholdUtlands, boolean harTidligereOppholdUtenlands,
                                              boolean oppholdINorge, String soker, boolean erBarnetFodt,
                                              Integer antallBarn, List<LocalDate> foedselsDato,
                                              AnnenForelderDto annenForelder) {
        this.tema = tema;
        this.soknadstype = soknadstype;
        this.mottattDato = mottattDato;
        this.harFremtidigOppholdUtlands = harFremtidigOppholdUtlands;
        this.harTidligereOppholdUtenlands = harTidligereOppholdUtenlands;
        this.oppholdINorge = oppholdINorge;
        this.soker = soker;
        this.erBarnetFodt = erBarnetFodt;
        this.antallBarn = antallBarn;
        this.foedselsDato = foedselsDato;
        this.annenForelder = annenForelder;
    }

    public String getTema() {
        return tema;
    }

    public String getSoknadstype() {
        return soknadstype;
    }

    public LocalDate getMottattDato() {
        return mottattDato;
    }

    public boolean isHarFremtidigOppholdUtlands() {
        return harFremtidigOppholdUtlands;
    }

    public boolean isHarTidligereOppholdUtenlands() {
        return harTidligereOppholdUtenlands;
    }

    public boolean isOppholdINorge() {
        return oppholdINorge;
    }

    public String getSoker() {
        return soker;
    }

    public boolean isErBarnetFodt() {
        return erBarnetFodt;
    }

    public Integer getAntallBarn() {
        return antallBarn;
    }

    public List<LocalDate> getFoedselsDato() {
        return foedselsDato;
    }

    public AnnenForelderDto getAnnenForelder() {
        return annenForelder;
    }

    public PapirSoknadEngangstonadBekreftelse setSøker(SøkersRolle søker) {
        this.soker = søker.name();
        return this;
    }

    public PapirSoknadEngangstonadBekreftelse setFoedselsDato(LocalDate foedselsDato) {
        this.foedselsDato = Collections.singletonList(foedselsDato);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PapirSoknadEngangstonadBekreftelse that = (PapirSoknadEngangstonadBekreftelse) o;
        return harFremtidigOppholdUtlands == that.harFremtidigOppholdUtlands &&
                harTidligereOppholdUtenlands == that.harTidligereOppholdUtenlands &&
                oppholdINorge == that.oppholdINorge &&
                erBarnetFodt == that.erBarnetFodt &&
                Objects.equals(tema, that.tema) &&
                Objects.equals(soknadstype, that.soknadstype) &&
                Objects.equals(mottattDato, that.mottattDato) &&
                Objects.equals(soker, that.soker) &&
                Objects.equals(antallBarn, that.antallBarn) &&
                Objects.equals(foedselsDato, that.foedselsDato) &&
                Objects.equals(annenForelder, that.annenForelder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tema, soknadstype, mottattDato, harFremtidigOppholdUtlands, harTidligereOppholdUtenlands, oppholdINorge, soker, erBarnetFodt, antallBarn, foedselsDato, annenForelder);
    }

    @Override
    public String toString() {
        return "PapirSoknadEngangstonadBekreftelse{" +
                "tema='" + tema + '\'' +
                ", soknadstype='" + soknadstype + '\'' +
                ", mottattDato=" + mottattDato +
                ", harFremtidigOppholdUtlands=" + harFremtidigOppholdUtlands +
                ", harTidligereOppholdUtenlands=" + harTidligereOppholdUtenlands +
                ", oppholdINorge=" + oppholdINorge +
                ", soker='" + soker + '\'' +
                ", erBarnetFodt=" + erBarnetFodt +
                ", antallBarn=" + antallBarn +
                ", foedselsDato=" + foedselsDato +
                ", annenForelder=" + annenForelder +
                "} " + super.toString();
    }
}
