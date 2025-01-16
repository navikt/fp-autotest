package no.nav.foreldrepenger.generator.inntektsmelding.builders.xml;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import no.nav.foreldrepenger.autotest.util.CollectionUtils;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;
import no.seres.xsd.nav.inntektsmelding_m._20181211.EndringIRefusjon;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.Refusjon;


class RefusjonXmlMapper {

    private RefusjonXmlMapper() {
        // skjul ctor
    }

    public static Refusjon map(Inntektsmelding.Refusjon imref, ObjectFactory objectFactory) {
        Objects.requireNonNull(imref.refusjonBeløpPrMnd(), "refusjonsBelopPerMnd kan ikke være null");

        var refusjon = objectFactory.createRefusjon();
        refusjon.setRefusjonsbeloepPrMnd(objectFactory.createRefusjonRefusjonsbeloepPrMnd(imref.refusjonBeløpPrMnd()));

        if (imref.refusjonOpphørsdato() != null) {
            refusjon.setRefusjonsopphoersdato(objectFactory.createRefusjonRefusjonsopphoersdato(imref.refusjonOpphørsdato()));
        }

        if (CollectionUtils.isNotEmpty(imref.refusjonEndringList())) {
            var endringIRefusjonsListe = objectFactory.createEndringIRefusjonsListe();
            endringIRefusjonsListe.getEndringIRefusjon()
                    .addAll(imref.refusjonEndringList()
                            .stream()
                            .map(endring -> createEndringIRefusjon(objectFactory, endring.fom(), endring.beloepPrMnd()))
                            .toList());
            refusjon.setEndringIRefusjonListe(objectFactory.createRefusjonEndringIRefusjonListe(endringIRefusjonsListe));
        }

        return refusjon;
    }

    private static EndringIRefusjon createEndringIRefusjon(ObjectFactory objectFactory,
                                                           LocalDate endringsdato,
                                                           BigDecimal refusjonsbeloepPrMnd) {
        var endringIRefusjon = objectFactory.createEndringIRefusjon();
        endringIRefusjon.setEndringsdato(objectFactory.createEndringIRefusjonEndringsdato(endringsdato));
        endringIRefusjon.setRefusjonsbeloepPrMnd(objectFactory.createEndringIRefusjonRefusjonsbeloepPrMnd(refusjonsbeloepPrMnd));
        return endringIRefusjon;
    }

}
