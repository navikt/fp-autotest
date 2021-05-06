package no.nav.foreldrepenger.autotest.søknad.builderV2;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.søknad.builder.ForeldrepengerBuilder;
import no.nav.foreldrepenger.autotest.søknad.modell.BrukerRolle;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Søker;

public class SøknadsbuilderForeldrepenger {

    private final Familie familie;
    private BrukerRolle brukerRolle;
    private Søker søker;

    public SøknadsbuilderForeldrepenger(Familie familie) {
        this.familie = familie;
    }

    public SøknadsbuilderForeldrepenger mor() {
        brukerRolle = BrukerRolle.MOR;
        søker = familie.mor();
        return this;
    }
    public SøknadsbuilderForeldrepenger far() {
        brukerRolle = BrukerRolle.FAR;
        søker = familie.far();
        return this;
    }
    public SøknadsbuilderForeldrepenger medmor() {
        brukerRolle = BrukerRolle.MEDMOR;
        søker = familie.medmor();
        return this;
    }


    public ForeldrepengerBuilder termin(LocalDate termindato) {
        guardIkkeValgtSøkerForSøknad();
        return SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTermin(termindato, søker.fnrAnnenpart(), brukerRolle);
    }

    public ForeldrepengerBuilder fødsel() {
        guardIkkeValgtSøkerForSøknad();
        return SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel(familie.barn().fødselsdato(), søker.fnrAnnenpart(), brukerRolle);
    }

    public ForeldrepengerBuilder fødsel(LocalDate fødselsdato) {
        guardIkkeValgtSøkerForSøknad();
        return SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel(fødselsdato, søker.fnrAnnenpart(), brukerRolle);
    }

    public ForeldrepengerBuilder adopsjon(LocalDate omsorgsovertakelsedatoen, Boolean ektefellesBarn) {
        guardIkkeValgtSøkerForSøknad();
        return SøknadForeldrepengerErketyper
                .lagSøknadForeldrepengerAdopsjon(omsorgsovertakelsedatoen, søker.fnrAnnenpart(), brukerRolle, ektefellesBarn);
    }

    public ForeldrepengerBuilder omsorgsovertagelse(LocalDate termindato) {
        guardIkkeValgtSøkerForSøknad();
        return SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTermin(termindato, søker.fnrAnnenpart(), brukerRolle);
    }

    private void guardIkkeValgtSøkerForSøknad() {
        if (søker == null || brukerRolle == null) {
            throw new UnsupportedOperationException("Du må spesifiser hvilken part som skal søke: mor, far eller medmor.");
        }
    }
}
