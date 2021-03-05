package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Behandlingsresultat {

    protected Integer id;
    protected BehandlingResultatType type;
    protected Avslagsårsak avslagsarsak;
    protected Kode rettenTil;
    protected List<Kode> konsekvenserForYtelsen;
    protected String avslagsarsakFritekst;
    protected String overskrift;
    protected String fritekstbrev;
    protected SkjæringstidspunktDto skjæringstidspunkt;

    @Override
    public String toString() {
        return type.name();
    }

    public BehandlingResultatType getType() {
        return type;
    }

    public List<Kode> getKonsekvenserForYtelsen() {
        return konsekvenserForYtelsen;
    }

    public Avslagsårsak getAvslagsarsak() {
        return avslagsarsak;
    }

    public SkjæringstidspunktDto getSkjæringstidspunkt() {
        return skjæringstidspunkt;
    }
}
