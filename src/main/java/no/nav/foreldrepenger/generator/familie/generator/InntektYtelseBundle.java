package no.nav.foreldrepenger.generator.familie.generator;

import java.util.List;

import no.nav.foreldrepenger.vtp.kontrakter.person.v2.ArbeidsforholdDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.InntektsperiodeDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.SkatteopplysningDto;

/**
 * Samler arbeidsforhold/inntekt/skatteopplysninger bygget av InntektYtelseGenerator, for tilkobling
 * på en PersonBuilder via .inntektytelse(...). Erstatter v1s InntektYtelseModellDto-wrapper —
 * v2-kontrakten har ingen tilsvarende nøstet type (flate lister direkte på PersonDto).
 */
public record InntektYtelseBundle(List<ArbeidsforholdDto> arbeidsforhold,
                                  List<InntektsperiodeDto> inntekt,
                                  List<SkatteopplysningDto> skatteopplysninger) {
}
