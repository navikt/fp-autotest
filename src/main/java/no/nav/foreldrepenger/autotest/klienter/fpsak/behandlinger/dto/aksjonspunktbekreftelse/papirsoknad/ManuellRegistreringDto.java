package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.FamilieHendelseType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.AnnenForelderDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.OmsorgDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.RettigheterDto;


public abstract class ManuellRegistreringDto extends AksjonspunktBekreftelse {

    private FamilieHendelseType tema;
    private String soknadstype;
    private String soker;

    private RettigheterDto rettigheter;
    private boolean oppholdINorge = true;
    private boolean harTidligereOppholdUtenlands = false;
    private boolean harFremtidigeOppholdUtenlands = false;

//    private List<UtenlandsoppholdDto> tidligereOppholdUtenlands;
//    private List<UtenlandsoppholdDto> fremtidigeOppholdUtenlands;

    private boolean erBarnetFodt = true;
    private LocalDate termindato;
    private LocalDate terminbekreftelseDato;
    private Integer antallBarnFraTerminbekreftelse = 1;
    private Integer antallBarn = 1;
    private List<LocalDate> foedselsDato = List.of(LocalDate.now().minusDays(1));

    private AnnenForelderDto annenForelder = new AnnenForelderDto();
    private String tilleggsopplysninger;

    private String språkkode = "NB";

    private String kommentarEndring;
    private boolean registrerVerge;
    private LocalDate mottattDato = LocalDate.now().minusDays(10);
    private boolean ufullstendigSoeknad;

    OmsorgDto omsorg = new OmsorgDto(antallBarn, foedselsDato);

    public ManuellRegistreringDto(String soknadstype) {
        super();
        this.soknadstype = soknadstype;
    }

    public FamilieHendelseType getTema() {
        return tema;
    }

    public void setTema(FamilieHendelseType tema) {
        this.tema = tema;
    }

    public String getSoknadstype() {
        return soknadstype;
    }

    public void setSoknadstype(String soknadstype) {
        this.soknadstype = soknadstype;
    }

    public String getSoker() {
        return soker;
    }

    public void setSoker(String soker) {
        this.soker = soker;
    }

    public RettigheterDto getRettigheter() {
        return rettigheter;
    }

    public void setRettigheter(RettigheterDto rettigheter) {
        this.rettigheter = rettigheter;
    }

    public boolean isOppholdINorge() {
        return oppholdINorge;
    }

    public void setOppholdINorge(boolean oppholdINorge) {
        this.oppholdINorge = oppholdINorge;
    }

    public boolean isHarTidligereOppholdUtenlands() {
        return harTidligereOppholdUtenlands;
    }

    public void setHarTidligereOppholdUtenlands(boolean harTidligereOppholdUtenlands) {
        this.harTidligereOppholdUtenlands = harTidligereOppholdUtenlands;
    }

    public boolean isHarFremtidigeOppholdUtenlands() {
        return harFremtidigeOppholdUtenlands;
    }

    public void setHarFremtidigeOppholdUtenlands(boolean harFremtidigeOppholdUtenlands) {
        this.harFremtidigeOppholdUtenlands = harFremtidigeOppholdUtenlands;
    }

    public boolean isErBarnetFodt() {
        return erBarnetFodt;
    }

    public void setErBarnetFodt(boolean erBarnetFodt) {
        this.erBarnetFodt = erBarnetFodt;
    }

    public LocalDate getTermindato() {
        return termindato;
    }

    public void setTermindato(LocalDate termindato) {
        this.termindato = termindato;
    }

    public LocalDate getTerminbekreftelseDato() {
        return terminbekreftelseDato;
    }

    public void setTerminbekreftelseDato(LocalDate terminbekreftelseDato) {
        this.terminbekreftelseDato = terminbekreftelseDato;
    }

    public Integer getAntallBarnFraTerminbekreftelse() {
        return antallBarnFraTerminbekreftelse;
    }

    public void setAntallBarnFraTerminbekreftelse(Integer antallBarnFraTerminbekreftelse) {
        this.antallBarnFraTerminbekreftelse = antallBarnFraTerminbekreftelse;
    }

    public Integer getAntallBarn() {
        return antallBarn;
    }

    public void setAntallBarn(Integer antallBarn) {
        this.antallBarn = antallBarn;
    }

    public List<LocalDate> getFoedselsDato() {
        return foedselsDato;
    }

    public void setFoedselsDato(List<LocalDate> foedselsDato) {
        this.foedselsDato = foedselsDato;
    }

    public AnnenForelderDto getAnnenForelder() {
        return annenForelder;
    }

    public void setAnnenForelder(AnnenForelderDto annenForelder) {
        this.annenForelder = annenForelder;
    }

    public String getTilleggsopplysninger() {
        return tilleggsopplysninger;
    }

    public void setTilleggsopplysninger(String tilleggsopplysninger) {
        this.tilleggsopplysninger = tilleggsopplysninger;
    }

    public String getSpråkkode() {
        return språkkode;
    }

    public void setSpråkkode(String språkkode) {
        this.språkkode = språkkode;
    }

    public String getKommentarEndring() {
        return kommentarEndring;
    }

    public void setKommentarEndring(String kommentarEndring) {
        this.kommentarEndring = kommentarEndring;
    }

    public boolean isRegistrerVerge() {
        return registrerVerge;
    }

    public void setRegistrerVerge(boolean registrerVerge) {
        this.registrerVerge = registrerVerge;
    }

    public LocalDate getMottattDato() {
        return mottattDato;
    }

    public void setMottattDato(LocalDate mottattDato) {
        this.mottattDato = mottattDato;
    }

    public boolean isUfullstendigSoeknad() {
        return ufullstendigSoeknad;
    }

    public void setUfullstendigSoeknad(boolean ufullstendigSoeknad) {
        this.ufullstendigSoeknad = ufullstendigSoeknad;
    }

    public OmsorgDto getOmsorg() {
        return omsorg;
    }

    public void setOmsorg(OmsorgDto omsorg) {
        this.omsorg = omsorg;
    }
}
