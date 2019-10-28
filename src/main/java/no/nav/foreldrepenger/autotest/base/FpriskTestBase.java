package no.nav.foreldrepenger.autotest.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.foreldrepenger.autotest.aktoerer.fprisk.Saksbehandler;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.AktørId;
import no.nav.foreldrepenger.autotest.klienter.vtp.testscenario.TestscenarioKlient;
import no.nav.foreldrepenger.autotest.util.http.BasicHttpSession;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;


public class FpriskTestBase extends TestScenarioTestBase {

    protected Saksbehandler saksbehandler;
    protected TestscenarioKlient testscenarioKlient;

    public FpriskTestBase (){
        saksbehandler = new Saksbehandler();
        testscenarioKlient = new TestscenarioKlient(BasicHttpSession.session());
    }

    public static class RequestWrapper {
        protected String callId;
        protected Object request;

        public RequestWrapper(String callId, Object request){
            this.callId = callId;
            this.request = request;
        }
    }

    //TODO: Flytte ut i egne DTOer?
    public static class RisikovurderingRequest {
        protected AktoerIdDto soekerAktoerId;
        protected LocalDate skjæringstidspunkt;
        protected Opplysningsperiode opplysningsperiode;
        protected String behandlingstema;
        protected AnnenPart annenPart;
        protected String konsumentId;

        public RisikovurderingRequest(String soekerAktoerId, LocalDate skjæringstidspunkt, LocalDate opplysningsperiodefraOgMed,
                                      LocalDate opplysningsperiodeTilOgMed, String behandlingstema, String annenPartAktørId , String konsumentId ) {
            this.soekerAktoerId = new AktoerIdDto(soekerAktoerId);
            this.skjæringstidspunkt = skjæringstidspunkt;
            this.opplysningsperiode = new Opplysningsperiode(opplysningsperiodefraOgMed, opplysningsperiodeTilOgMed);
            this.behandlingstema = behandlingstema;
            this.annenPart = new AnnenPart(new AktoerIdDto(annenPartAktørId));
            this.konsumentId = konsumentId;
        }
    }


    public static class AktoerIdDto {

        @JsonProperty("aktoerId")
        protected String aktørId;

        public AktoerIdDto(String aktørId) {
            this.aktørId = aktørId;
        }

        public Optional<AktørId> get() {
            if (aktørId.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(new AktørId(aktørId));
        }
    }


    public static class Opplysningsperiode {

        private static final LocalDate TIDENES_ENDE = LocalDate.of(9999, Month.DECEMBER, 31);

        protected LocalDate fraOgMed;

        protected LocalDate tilOgMed;

        public Opplysningsperiode() {
            fraOgMed = null;
            tilOgMed = TIDENES_ENDE;
        }

        public Opplysningsperiode(LocalDate fraOgMed, LocalDate tilOgMed) {
            this.fraOgMed = fraOgMed;
            if (tilOgMed != null) {
                this.tilOgMed = tilOgMed;
            } else {
                this.tilOgMed = TIDENES_ENDE;
            }
        }

        public LocalDate getFraOgMed() {
            return fraOgMed;
        }
        public LocalDate getTilOgMed() {
            return tilOgMed;
        }
    }


    public static class AnnenPart {

        protected AktoerIdDto annenPartAktoerId;
        protected String utenlandskFnr;

        public AnnenPart(AktoerIdDto annenPartAktoerId){
            this.annenPartAktoerId = annenPartAktoerId;
            this.utenlandskFnr = null;
        }

        public AnnenPart(String utenlandskFnr){
            this.annenPartAktoerId = null;
            this.utenlandskFnr = utenlandskFnr;
        }
    }

}
