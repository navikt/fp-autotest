package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.util.IndexClasses;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public abstract class AksjonspunktBekreftelse {

    private static final Logger logger = LoggerFactory.getLogger(AksjonspunktBekreftelse.class);

    @JsonProperty("@type")
    protected String kode;
    protected String begrunnelse;

    private static final List<Class<? extends AksjonspunktBekreftelse>> aksjonspunktBekreftelseClasses;
    static {
        try {
            IndexClasses index = IndexClasses.getIndexFor(
                    AksjonspunktBekreftelse.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            aksjonspunktBekreftelseClasses = Collections.unmodifiableList(
                    index.getSubClassesWithAnnotation(AksjonspunktBekreftelse.class, BekreftelseKode.class));
        } catch (URISyntaxException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @SuppressWarnings("unused")
    public AksjonspunktBekreftelse() {
        if (null == this.getClass().getAnnotation(BekreftelseKode.class)) {
            throw new RuntimeException("Kode annotation er ikke satt for " + this.getClass().getTypeName());
        }
        kode = this.getClass().getAnnotation(BekreftelseKode.class).kode();
    }

    public static AksjonspunktBekreftelse fromKode(String kode) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

        for (Class<? extends AksjonspunktBekreftelse> klasse : aksjonspunktBekreftelseClasses) {

            BekreftelseKode annotation = klasse.getDeclaredAnnotation(BekreftelseKode.class);

            if (Modifier.isAbstract(klasse.getModifiers())) {
                continue; // trenger trenger ikke skjekke klasser som er abstrakte
            } else if (annotation == null) {
                logger.warn("Aksjonspunkt mangler annotasjon='{}'", klasse.getName());
            } else if (annotation.kode().equals(kode)) {
                return klasse.getConstructor().newInstance();
            }
        }

        return null;
    }

    public static AksjonspunktBekreftelse fromAksjonspunkt(Aksjonspunkt aksjonspunkt)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        return fromKode(aksjonspunkt.getDefinisjon().kode);
    }

    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {

    }

    public AksjonspunktBekreftelse setBegrunnelse(String begrunnelse) {
        this.begrunnelse = begrunnelse;
        return this;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s : %s", this.getClass().getSimpleName(), kode != null ? kode : "",
                begrunnelse != null ? begrunnelse : "");
    }
}
