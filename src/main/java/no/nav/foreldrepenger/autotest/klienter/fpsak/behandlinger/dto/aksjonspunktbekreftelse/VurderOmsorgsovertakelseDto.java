package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.OmsorgsovertakelseVilkårType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;


public class VurderOmsorgsovertakelseDto extends AksjonspunktBekreftelse {

    public record OmsorgsovertakelseBarnDto(LocalDate fødselsdato, Integer barnNummer) {
    }

    private Avslagsårsak avslagskode;
    private OmsorgsovertakelseVilkårType delvilkår;
    private LocalDate omsorgsovertakelseDato;
    private List<OmsorgsovertakelseBarnDto> fødselsdatoer;
    private boolean ektefellesBarn;

    public VurderOmsorgsovertakelseDto() {
        super();
    }

    public VurderOmsorgsovertakelseDto oppfylt(OmsorgsovertakelseVilkårType delvilkår,
                                               LocalDate omsorgsovertakelseDato,
                                               boolean ektefellesBarn,
                                               LocalDate... fødselsdatoene) {
        this.delvilkår = delvilkår;
        this.omsorgsovertakelseDato = omsorgsovertakelseDato;
        this.ektefellesBarn = ektefellesBarn;
        this.fødselsdatoer = new ArrayList<>();
        for (int i = 0; i < fødselsdatoene.length; i++) {
            this.fødselsdatoer.add(new OmsorgsovertakelseBarnDto(fødselsdatoene[i], i+1));
        }
        return this;
    }

    public VurderOmsorgsovertakelseDto ikkeOppfylt(Avslagsårsak avslagsårsak,
                                                   OmsorgsovertakelseVilkårType delvilkår,
                                                   LocalDate omsorgsovertakelseDato,
                                                   boolean ektefellesBarn,
                                                   LocalDate... fødselsdatoene) {
        this.avslagskode = avslagsårsak;
        this.delvilkår = delvilkår;
        this.omsorgsovertakelseDato = omsorgsovertakelseDato;
        this.ektefellesBarn = ektefellesBarn;
        this.fødselsdatoer = new ArrayList<>();
        for (int i = 0; i < fødselsdatoene.length; i++) {
            this.fødselsdatoer.add(new OmsorgsovertakelseBarnDto(fødselsdatoene[i], i+1));
        }
        return this;
    }

    @Override
    public String aksjonspunktKode() {
        return AksjonspunktKoder.VURDER_OMSORGSOVERTAKELSEVILKÅRET;
    }
}
