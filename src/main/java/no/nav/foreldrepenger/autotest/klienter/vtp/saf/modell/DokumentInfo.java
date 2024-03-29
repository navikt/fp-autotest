package no.nav.foreldrepenger.autotest.klienter.vtp.saf.modell;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DokumentInfo(String dokumentInfoId,
                           String tittel,
                           String brevkode,
                           List<LogiskVedlegg> logiskeVedlegg,
                           List<Dokumentvariant> dokumentvarianter) {
}
