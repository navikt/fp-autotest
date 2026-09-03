package no.nav.foreldrepenger.generator.familie.generator;

import java.util.List;

import no.nav.foreldrepenger.vtp.kontrakter.person.v2.ArbeidsforholdDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.InntektsperiodeDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.RegistrertNæringsvirksomhetDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.SkatteopplysningDto;

/**
 * Samler arbeidsforhold, inntekt, skatteopplysninger og Brreg-data bygget av InntektGenerator, for tilkobling
 * på en PersonBuilder via .inntekt(...). Erstatter v1s InntektYtelseModellDto-wrapper —
 * v2-kontrakten har ingen tilsvarende nøstet type (flate lister direkte på PersonDto).
 */
public record InntektYtelseBundle(List<ArbeidsforholdDto> arbeidsforhold,
                                  List<InntektsperiodeDto> inntekt,
                                  List<SkatteopplysningDto> skatteopplysninger,
                                  List<RegistrertNæringsvirksomhetDto> registrerteNæringsvirksomheter) {
}
