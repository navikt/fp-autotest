package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeid.Arbeidsforhold;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.fpmock2.dokumentgenerator.foreldrepengesoknad.erketyper.TilretteleggingsErketyper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@BekreftelseKode(kode="5091")
public class AvklarFaktaFødselOgTilrettelegging extends AksjonspunktBekreftelse {
    private LocalDate termindato;
    private LocalDate fødselsdato;
    private List<SvpArbeidsforhold> bekreftetSvpArbeidsforholdList;


    public AvklarFaktaFødselOgTilrettelegging(Fagsak fagsak, Behandling behandling){
        super(fagsak, behandling);
    }


    public class SvpArbeidsforhold{

        protected Long tilretteleggingId;
        protected LocalDate tilretteleggingBehovFom;
        protected List<SvpTilretteleggingDato> tilretteleggingDatoer = new ArrayList<>();
        protected String arbeidsgiverNavn;
        protected String arbeidsgiverIdent;
        protected String opplysningerOmRisiko;
        protected String opplysningerOmTilrettelegging;
        protected Boolean kopiertFraTidligereBehandling;
        protected LocalDateTime mottattTidspunkt;
        protected String begrunnelse;

    }

    public class SvpTilretteleggingDato {
        protected LocalDate fom;
        protected TilretteleggingsErketyper type;
        protected BigDecimal stillingsprosent;
    }

}
