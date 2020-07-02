package no.nav.foreldrepenger.autotest.erketyper;

import java.math.BigDecimal;

import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.inntektsmelding.xml.kodeliste._20180702.YtelseKodeliste;
import no.nav.inntektsmelding.xml.kodeliste._20180702.ÅrsakInnsendingKodeliste;

public class InntektsmeldingSvangerskapspengerErketyper {
    public static InntektsmeldingBuilder lagSvangerskapspengerInntektsmelding(String fnr, Integer beløp,
            String orgnummer) {
        return new InntektsmeldingBuilder()
                .medArbeidstakerFNR(fnr)
                .medBeregnetInntekt(BigDecimal.valueOf(beløp))
                .medYtelse(YtelseKodeliste.SVANGERSKAPSPENGER)
                .medAarsakTilInnsending(ÅrsakInnsendingKodeliste.NY)
                .medArbeidsgiver(orgnummer, "41925090")
                .medAvsendersystem("FS32", "1.0");
    }
}
