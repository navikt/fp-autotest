package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunkt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties (ignoreUnknown = true)
public class AksjonspunktDto {
    public AksjonspunktDefinisjon definisjon;
    public boolean erAktivt;
    public boolean kanLoses;

}
