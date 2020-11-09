package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.mottak.dto;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Kvittering {

    private static final Logger LOG = LoggerFactory.getLogger(Kvittering.class);
    private final String referanseId;
    private final LocalDateTime mottattDato;
    private LocalDate førsteDag;
    private final LeveranseStatus leveranseStatus;
    private String journalId;
    private String saksNr;
    private byte[] pdf;
    private LocalDate førsteInntektsmeldingDag;
    private byte[] infoskrivPdf;


    public boolean erVellykket() {
        return leveranseStatus.erVellykket();
    }

}
