package no.nav.foreldrepenger.autotest.util.junit;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import no.nav.foreldrepenger.autotest.klienter.fprisk.risikovurdering.RisikovurderingJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.BehandlingerJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.FagsakJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.FordelJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.HistorikkJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.KodeverkJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.ProsesstaskJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.OkonomiJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.journalpost.JournalforingJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.kafka.KafkaJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.openam.OpenamJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.pdl.PdlLeesahJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.saf.SafJerseyKlient;

/** Denne junit-utvidelsen instansierer klientene bare _en_ gang for hver tråd som køres i parallel.
 *  Denne utvidelsen instansierer alle klientene som brukes av aktørene i FpsakTestBase.*/
public class FpsakTestBaseKlientInstansiererExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    // Fptilbake klienter
    public static no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.BehandlingerJerseyKlient behandlingerKlientFptilbake;
    public static no.nav.foreldrepenger.autotest.klienter.fptilbake.prosesstask.ProsesstaskJerseyKlient prosesstaskKlientFptilbake;
    public static OkonomiJerseyKlient okonomiKlient;

    // Fpsak klienter
    public static FordelJerseyKlient fordelKlient;
    public static FagsakJerseyKlient fagsakKlient;
    public static BehandlingerJerseyKlient behandlingerKlient;
    public static KodeverkJerseyKlient kodeverkKlient;
    public static HistorikkJerseyKlient historikkKlient;
    public static ProsesstaskJerseyKlient prosesstaskKlient;
    public static RisikovurderingJerseyKlient risikovurderingKlient;

    // Vtp Klienter
    public static PdlLeesahJerseyKlient pdlLeesahKlient;
    public static JournalforingJerseyKlient journalpostKlient;
    public static SafJerseyKlient safKlient;
    public static KafkaJerseyKlient kafkaKlient;
    public static OpenamJerseyKlient openamJerseyKlient;


    @Override
    public void beforeAll(ExtensionContext context) {
        var uniqueKey = Thread.currentThread().getName() + "-" + this.getClass().getName();
        var value = context.getRoot().getStore(GLOBAL).get(uniqueKey);
        if (value == null) {
            context.getRoot().getStore(GLOBAL).put(uniqueKey, this);
            initialiser();
        }
    }

    private void initialiser() {
        behandlingerKlientFptilbake = new no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.BehandlingerJerseyKlient();
        prosesstaskKlientFptilbake = new no.nav.foreldrepenger.autotest.klienter.fptilbake.prosesstask.ProsesstaskJerseyKlient();
        okonomiKlient = new OkonomiJerseyKlient();

        fordelKlient = new FordelJerseyKlient();
        fagsakKlient = new FagsakJerseyKlient();
        behandlingerKlient = new BehandlingerJerseyKlient();
        kodeverkKlient = new KodeverkJerseyKlient();
        historikkKlient = new HistorikkJerseyKlient();
        prosesstaskKlient = new ProsesstaskJerseyKlient();
        risikovurderingKlient = new RisikovurderingJerseyKlient();

        pdlLeesahKlient = new PdlLeesahJerseyKlient();
        journalpostKlient = new JournalforingJerseyKlient();
        safKlient = new SafJerseyKlient();
        kafkaKlient = new KafkaJerseyKlient();
        openamJerseyKlient = new OpenamJerseyKlient();
    }

    @Override
    public void close() {
    }
}
