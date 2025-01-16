package no.nav.foreldrepenger.generator.inntektsmelding.builders.xml;

import java.io.IOException;
import java.io.StringWriter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;
import no.seres.xsd.nav.inntektsmelding_m._20181211.InntektsmeldingM;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;

public class InntektsmeldingXmlMapper {

    private InntektsmeldingXmlMapper() {
        // skjul ctor
    }

    public static String opprettInntektsmeldingXML(Inntektsmelding inntektsmelding) {
        return createInntektsmeldingXML(map(inntektsmelding));
    }

    private static String createInntektsmeldingXML(InntektsmeldingM inntektsmelding) {
        try (var sw = new StringWriter()) {
            var objectFactory = new ObjectFactory();
            var jaxbContext = JAXBContext.newInstance(InntektsmeldingM.class);
            var jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // Prettyprinter output
            jaxbMarshaller.marshal(objectFactory.createMelding(inntektsmelding), sw);
            return sw.toString();
        } catch (JAXBException | IOException e) {
            throw new IllegalArgumentException("Noe gikk galt ved oversetting av inntektsmelding til XML", e);
        }
    }

    private static InntektsmeldingM map(Inntektsmelding inntektsmelding) {
        var inntektsmeldingM = new InntektsmeldingM();
        var skjemainnhold = SkjemainnholdXmlMapper.map(inntektsmelding);
        inntektsmeldingM.setSkjemainnhold(skjemainnhold);
        return inntektsmeldingM;
    }
}
