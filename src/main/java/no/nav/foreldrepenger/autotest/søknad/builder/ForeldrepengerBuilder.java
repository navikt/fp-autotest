package no.nav.foreldrepenger.autotest.søknad.builder;

import no.nav.foreldrepenger.autotest.søknad.modell.BrukerRolle;
import no.nav.foreldrepenger.autotest.søknad.modell.Søknad;
import no.nav.foreldrepenger.autotest.søknad.modell.Ytelse;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.SpråkKode;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.annenforelder.AnnenForelder;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.opptjening.Opptjening;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.relasjontilbarn.RelasjonTilBarn;
import no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.Dekningsgrad;
import no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.Rettigheter;
import no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.Fordeling;

public class ForeldrepengerBuilder extends SøknadBuilder<ForeldrepengerBuilder> {

    private final Foreldrepenger foreldrepengerKladd = new Foreldrepenger();

    public ForeldrepengerBuilder(BrukerRolle brukerRolle) {
        this.medSøker(brukerRolle, SpråkKode.NB);
    }

    @Override
    protected ForeldrepengerBuilder self() {
        return this;
    }

    @Override
    protected ForeldrepengerBuilder medYtelse(Ytelse ytelse) {
        søknadKladd.setYtelse(ytelse);
        return this;
    }

    public ForeldrepengerBuilder medAnnenForelder(AnnenForelder annenForelder) {
        foreldrepengerKladd.setAnnenForelder(annenForelder);
        return this;
    }

    public ForeldrepengerBuilder medRelasjonTilBarn(RelasjonTilBarn relasjonTilBarn) {
        foreldrepengerKladd.setRelasjonTilBarn(relasjonTilBarn);
        return this;
    }

    public ForeldrepengerBuilder medRettigheter(Rettigheter rettigheter) {
        foreldrepengerKladd.setRettigheter(rettigheter);
        return this;
    }

    public ForeldrepengerBuilder medDekningsgrad(Dekningsgrad dekningsgrad) {
        foreldrepengerKladd.setDekningsgrad(dekningsgrad);
        return this;
    }

    public ForeldrepengerBuilder medOpptjening(Opptjening opptjening) {
        foreldrepengerKladd.setOpptjening(opptjening);
        return this;
    }

    public ForeldrepengerBuilder medFordeling(Fordeling fordeling) {
        foreldrepengerKladd.setFordeling(fordeling);
        return this;
    }

    public ForeldrepengerBuilder medMedlemsskap(Medlemsskap medlemsskap) {
        foreldrepengerKladd.setMedlemsskap(medlemsskap);
        return this;
    }

    @Override
    public Søknad build() {
        this.medYtelse(this.foreldrepengerKladd);
        return super.build();
    }
}
