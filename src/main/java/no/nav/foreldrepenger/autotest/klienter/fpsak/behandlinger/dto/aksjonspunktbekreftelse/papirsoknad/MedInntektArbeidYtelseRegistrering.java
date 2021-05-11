package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.EgenVirksomhetDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FrilansDto;

public abstract class MedInntektArbeidYtelseRegistrering extends ManuellRegistreringDto {

//    private List<ArbeidsforholdDto> arbeidsforhold;
//    private List<AndreYtelserDto> andreYtelser;

    private EgenVirksomhetDto egenVirksomhet;
    private FrilansDto frilans;

    public MedInntektArbeidYtelseRegistrering(String soknadstype) {
        super(soknadstype);
    }

    public EgenVirksomhetDto getEgenVirksomhet() {
        return egenVirksomhet;
    }

    public void setEgenVirksomhet(EgenVirksomhetDto egenVirksomhet) {
        this.egenVirksomhet = egenVirksomhet;
    }

    public FrilansDto getFrilans() {
        return frilans;
    }

    public void setFrilans(FrilansDto frilans) {
        this.frilans = frilans;
    }
}
