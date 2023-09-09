package no.nav.foreldrepenger.autotest.klienter;

import java.net.URI;

public class BaseUriProvider {
    protected static final String LOCALHOST = "http://127.0.0.1";
    public static final URI VTP_ROOT = URI.create(LOCALHOST + ":8060");
    public static final URI VTP_API_BASE = URI.create(VTP_ROOT + "/rest/api");
    public static final URI FPSAK_BASE = URI.create(LOCALHOST + ":8080/fpsak/api");
    public static final URI FPRISK_BASE = URI.create(LOCALHOST + ":8075/fprisk/api");
    public static final URI FPTILBAKE_BASE = URI.create(LOCALHOST + ":8030/fptilbake/api");
    public static final URI FPSOKNAD_MOTTAK_BASE = URI.create(LOCALHOST + ":9001/api");
    public static final URI FPOVERSIKT_BASE = URI.create(LOCALHOST + ":8889/api");

}
