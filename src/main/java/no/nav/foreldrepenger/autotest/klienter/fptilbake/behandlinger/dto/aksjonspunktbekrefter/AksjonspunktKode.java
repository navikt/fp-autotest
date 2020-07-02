package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import no.nav.foreldrepenger.autotest.klienter.Fagsystem;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AksjonspunktKode {

    String kode();

    Fagsystem fagsystem();
}
