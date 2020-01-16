package no.nav.foreldrepenger.autotest.base;

import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.SvangerskapspengerBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.erketyper.MedlemskapErketyper;
import no.nav.inntektsmelding.xml.kodeliste._20180702.YtelseKodeliste;
import no.nav.inntektsmelding.xml.kodeliste._20180702.ÅrsakInnsendingKodeliste;
import no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Tilrettelegging;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class SvangerskapspengerTestBase extends FpsakTestBase {

    protected SvangerskapspengerBuilder lagSvangerskapspengerSøknad(String søkerAktørId, SøkersRolle søkersRolle, LocalDate termin, List<Tilrettelegging> tilretteleggingListe) {
        return new SvangerskapspengerBuilder(søkerAktørId, søkersRolle)
                .medTermindato(termin)
                .medTilretteleggingListe(tilretteleggingListe)
                .medMedlemskap(MedlemskapErketyper.medlemskapNorge());
    }

    protected InntektsmeldingBuilder lagSvangerskapspengerInntektsmelding(String fnr, Integer beløp, String orgnummer) {
        InntektsmeldingBuilder inntektsmelding = new InntektsmeldingBuilder()
                .medArbeidstakerFNR(fnr)
                .medBeregnetInntekt(BigDecimal.valueOf(beløp))
                .medYtelse(YtelseKodeliste.SVANGERSKAPSPENGER)
                .medAarsakTilInnsending(ÅrsakInnsendingKodeliste.NY)
                .medArbeidsgiver(orgnummer, "41925090")
                .medAvsendersystem("FS32", "1.0");
        return inntektsmelding;
    }



}
