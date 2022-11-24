package no.nav.foreldrepenger.autotest.klienter;

import java.net.URI;

public class BaseUriProvider {
    public static final URI AZURE_ROOT = URI.create("http://host.docker.internal:8085");
    public static final URI VTP_ROOT = URI.create("http://localhost:8060");
    public static final URI VTP_BASE = URI.create("http://127.0.0.1:8060/rest/api");
    public static final URI FPSAK_BASE = URI.create("http://127.0.0.1:8080/fpsak/api");
    public static final URI FPRISK_BASE = URI.create("http://127.0.0.1:8075/fprisk/api");
    public static final URI FPTILBAKE_BASE = URI.create("http://127.0.0.1:8030/fptilbake/api");
    public static final URI FPSOKNAD_MOTTAK_BASE = URI.create("http://127.0.0.1:9001/api");

}
