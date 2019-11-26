package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad;

import org.opensaml.xmlsec.encryption.Public;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GraderingPeriodeDto {

    public String periodeForGradering;
    public LocalDate periodeFom;
    public LocalDate periodeTom;

    public BigDecimal prosentandelArbeid;
    public String arbeidsgiverIdentifikator;
    public boolean erArbeidstaker;
    public boolean erFrilanser;
    public boolean erSelvstNæringsdrivende;
    public String arbeidskategoriType = "ARBEIDSTAKER";
    public boolean skalGraderes = true;

    public GraderingPeriodeDto(String periodeType,
                               LocalDate fom, LocalDate tom,
                               BigDecimal prosentandelArbeid,
                               String arbeidsgiverIdentifikator,
                               boolean erArbeidstaker,
                               boolean erFrilanser,
                               boolean erSelvstNæringsdrivende,
                               String arbeidskategoriType) {
        periodeForGradering = periodeType;
        periodeFom = fom;
        periodeTom = tom;
        this.prosentandelArbeid = prosentandelArbeid;
        this.arbeidsgiverIdentifikator = arbeidsgiverIdentifikator;
        this.erArbeidstaker = erArbeidstaker;
        this.erFrilanser = erFrilanser;
        this.erSelvstNæringsdrivende = erSelvstNæringsdrivende;
        this.arbeidskategoriType= arbeidskategoriType;
    }

}
