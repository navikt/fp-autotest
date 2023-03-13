package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.util.IndexClasses;

public abstract class AksjonspunktBekreftelse {

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
            throw new IllegalStateException("Feil i initialisering av aksjonspunktbekreftelser", e);
        }
    }

    @SuppressWarnings("unused")
    protected AksjonspunktBekreftelse() {
        if (null == this.getClass().getAnnotation(BekreftelseKode.class)) {
            throw new RuntimeException("Kode annotation er ikke satt for " + this.getClass().getTypeName());
        }
        kode = this.getClass().getAnnotation(BekreftelseKode.class).kode();
    }

    private static AksjonspunktBekreftelse fromKode(String kode, Fagsystem gjeldendeFagsystem) throws InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        var matchende = aksjonspunktBekreftelseClasses.stream()
                .filter(klasse -> !Modifier.isAbstract(klasse.getModifiers()))
                .filter(klasse -> {
                    var annotation = klasse.getDeclaredAnnotation(BekreftelseKode.class);
                    return annotation.kode().equals(kode) && annotation.fagsystem().equals(gjeldendeFagsystem);
                })
                .toList();
        if (matchende.size() > 1) {
            throw new IllegalStateException("Finner flere enn en class som bekrefter aksjonspunkt " + kode);
        }
        if (matchende.isEmpty()) {
            throw new IllegalStateException("Finner ingen class som bekrefter aksjonspunkt " + kode);
        }
        return matchende.get(0).getConstructor().newInstance();
    }

    public static AksjonspunktBekreftelse fromAksjonspunkt(Aksjonspunkt aksjonspunkt, Fagsystem fagsystem) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        return fromKode(aksjonspunkt.getDefinisjon(), fagsystem);
    }

    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {

    }

    public String kode() {
        return kode;
    }

    public AksjonspunktBekreftelse setBegrunnelse(String begrunnelse) {
        this.begrunnelse = begrunnelse;
        return this;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": {kode:" + (kode != null ? kode : "") + ", begrunnelse:" +
                (begrunnelse != null ? begrunnelse : "") + "}";
    }
}
