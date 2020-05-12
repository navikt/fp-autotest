package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Personopplysning;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@BekreftelseKode(kode="5054")
public class AvklarFaktaForeldreansvarFPBrekreftelse extends AksjonspunktBekreftelse {

    protected Integer antallBarn;

    protected LocalDate omsorgsovertakelseDato;
    protected LocalDate foreldreansvarDato;

    protected List<OmsorgovertakelseBarn> barn= new ArrayList<>();
    protected List<OmsorgovertakelseForelder> foreldre= new ArrayList<>();

    public AvklarFaktaForeldreansvarFPBrekreftelse() {
        super();
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        antallBarn = behandling.getSoknad().getAntallBarn();
        omsorgsovertakelseDato = behandling.getSoknad().getOmsorgsovertakelseDato();

        foreldre.add(new OmsorgovertakelseForelder(behandling.getPersonopplysning()));

        if(behandling.getSoknad().getAdopsjonFodelsedatoer() != null){
            Map<Integer, LocalDate> fodselsdatoer = behandling.getSoknad().getAdopsjonFodelsedatoer();

            for(int i = 0; i < fodselsdatoer.size(); i++){
                barn.add(new OmsorgovertakelseBarn(fodselsdatoer.get(i+1), "SAKSBEH", (i+1)));
            }
        }
        else{
            for(int i = 0; i < behandling.getSoknad().getAntallBarn(); i++){
                barn.add(new OmsorgovertakelseBarn(behandling.getSoknad().getFodselsdatoer().get(i+1), "SAKSBEH", (i+1)));
            }
        }
    }


    public class OmsorgovertakelseForelder
    {
        public int id;
        public LocalDate dodsdato;
        public boolean erMor;
        public String navn;
        public String opplysningsKilde;
        public String aktorId;

        public OmsorgovertakelseForelder(boolean erMor, String navn){
            this.erMor = erMor;
            this.navn = navn;
        }

        public OmsorgovertakelseForelder(Personopplysning person){
            id = person.getId();
            aktorId = "" + person.getAktoerId();
            dodsdato = person.getDoedsdato();
            erMor = person.getNavBrukerKjonn().kode.equals("K");
            navn = person.getNavn();
            opplysningsKilde = "TPS";
        }
    }

    public class OmsorgovertakelseBarn
    {
        public LocalDate fodselsdato;
        public String opplysningsKilde;
        public int nummer;
        public String aktorId;
        public String navn = "";

        public OmsorgovertakelseBarn(LocalDate fodselsdato, String opplysningsKilde, int nummer){
            this.fodselsdato = fodselsdato;
            this.opplysningsKilde = opplysningsKilde;
            this.nummer = nummer;
        }
    }
}
