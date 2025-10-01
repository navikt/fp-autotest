package no.nav.foreldrepenger.generator.soknad.builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.AnnenInntektDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.BarnDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.FrilansDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.FødselDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.NæringDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.SvangerskapspengesøknadDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.SøkerDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.TerminDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.UtenlandsoppholdsperiodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.VedleggDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.svangerskapspenger.AvtaltFerieDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.svangerskapspenger.BarnSvpDto;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.svangerskapspenger.TilretteleggingbehovDto;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.oppslag.dkif.Målform;

public class SvangerskapspengerBuilder implements SøknadBuilder<SvangerskapspengerBuilder> {
    private LocalDateTime mottattdato;
    private SøkerDto søkerinfo;
    private BrukerRolle rolle;
    private Målform språkkode;
    private BarnSvpDto barnSvp;
    private FrilansDto frilans;
    private NæringDto egenNæring;
    private List<AnnenInntektDto> andreInntekterSiste10Mnd;
    private List<UtenlandsoppholdsperiodeDto> utenlandsopphold;
    private List<TilretteleggingbehovDto> tilretteleggingsbehov;
    private List<AvtaltFerieDto> avtaltFerie;
    private List<VedleggDto> vedlegg;

    public SvangerskapspengerBuilder(List<TilretteleggingbehovDto> tilretteleggingsbehov) {
        this.tilretteleggingsbehov = tilretteleggingsbehov;
        this.språkkode = Målform.standard();
    }

    public SvangerskapspengerBuilder medMottattdato(LocalDate mottattdato) {
        this.mottattdato = LocalDateTime.of(mottattdato, LocalTime.now());
        return this;
    }

    public SvangerskapspengerBuilder medMottattdato(LocalDateTime mottattdato) {
        this.mottattdato = mottattdato;
        return this;
    }

    public SvangerskapspengerBuilder medSøkerinfo(SøkerDto søkerinfo) {
        this.søkerinfo = søkerinfo;
        return this;
    }

    public SvangerskapspengerBuilder medSpråkkode(Målform språkkode) {
        this.språkkode = språkkode;
        return this;
    }

    public SvangerskapspengerBuilder medBarn(BarnDto barn) {
        if (barn instanceof FødselDto fødsel) {
            this.barnSvp = new BarnSvpDto(fødsel.termindato(), fødsel.fødselsdato());
        } else if (barn instanceof TerminDto termin) {
            this.barnSvp = new BarnSvpDto(termin.termindato(), null);
        } else {
            throw new IllegalStateException("Svangerskapspengesøknad støtter bare fødsel eller termin!");
        }
        return this;
    }

    public SvangerskapspengerBuilder medFrilansInformasjon(FrilansDto frilans) {
        this.frilans = frilans;
        return this;
    }

    public SvangerskapspengerBuilder medSelvstendigNæringsdrivendeInformasjon(NæringDto egenNæring) {
        this.egenNæring = egenNæring;
        return this;
    }

    public SvangerskapspengerBuilder medAndreInntekterSiste10Mnd(List<AnnenInntektDto> andreInntekterSiste10Mnd) {
        this.andreInntekterSiste10Mnd = andreInntekterSiste10Mnd;
        return this;
    }

    public SvangerskapspengerBuilder medUtenlandsopphold(List<UtenlandsoppholdsperiodeDto> utenlandsopphold) {
        this.utenlandsopphold = utenlandsopphold;
        return this;
    }

    public SvangerskapspengerBuilder medAvtaltFerie(List<AvtaltFerieDto> avtaltFerie) {
        this.avtaltFerie = avtaltFerie;
        return this;
    }

    public SvangerskapspengerBuilder medVedlegg(List<VedleggDto> vedlegg) {
        this.vedlegg = vedlegg;
        return this;
    }

    public SvangerskapspengesøknadDto build() {
        if (mottattdato == null) {
            mottattdato = LocalDateTime.now();
        }
        return new SvangerskapspengesøknadDto(
                mottattdato,
                søkerinfo,
                BrukerRolle.MOR,
                språkkode,
                barnSvp,
                frilans,
                egenNæring,
                andreInntekterSiste10Mnd,
                utenlandsopphold,
                tilretteleggingsbehov,
                avtaltFerie,
                vedlegg
        );
    }
}
