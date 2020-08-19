package no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JournalpostMottak {
    private static int inkrementForEksternReferanse = 0;

    protected String saksnummer;
    protected String journalpostId;
    protected String forsendelseId;
    protected String eksternReferanseId;
    protected String behandlingstemaOffisiellKode;
    protected String dokumentTypeIdOffisiellKode;
    protected String forsendelseMottatt;
    protected String payloadXml;
    protected Integer payloadLength;
    protected String dokumentKategoriOffisiellKode;

    public JournalpostMottak(String saksnummer, String journalpostId, LocalDate forsendelseMottatt,
            String behandlingstemaOffisiellKode) {
        this(saksnummer, journalpostId, forsendelseMottatt.toString(), behandlingstemaOffisiellKode);
    }

    public JournalpostMottak(String saksnummer, String journalpostId, String forsendelseMottatt,
            String behandlingstemaOffisiellKode) {
        super();
        this.saksnummer = saksnummer;
        this.journalpostId = journalpostId;
        this.forsendelseMottatt = forsendelseMottatt;
        this.behandlingstemaOffisiellKode = behandlingstemaOffisiellKode;
        this.eksternReferanseId = lagUnikEksternReferanseId();
    }

    public String getDokumentTypeIdOffisiellKode() {
        return dokumentTypeIdOffisiellKode;
    }

    public void setDokumentTypeIdOffisiellKode(String dokumentTypeIdOffisiellKode) {
        this.dokumentTypeIdOffisiellKode = dokumentTypeIdOffisiellKode;
    }

    public String getPayloadXml() {
        return payloadXml;
    }

    public void setPayloadXml(String payloadXml) {
        this.payloadXml = payloadXml;
    }

    public Integer getPayloadLength() {
        return payloadLength;
    }

    public void setPayloadLength(Integer payloadLength) {
        this.payloadLength = payloadLength;
    }

    public void setForsendelseId(String forsendelseId) {
        this.forsendelseId = forsendelseId;
    }

    public void setDokumentKategoriOffisiellKode(String dokumentKategoriOffisiellKode) {
        this.dokumentKategoriOffisiellKode = dokumentKategoriOffisiellKode;
    }

    private String lagUnikEksternReferanseId() {
        String signifikanteSifre = String.valueOf(inkrementForEksternReferanse);
        StringBuilder sb = new StringBuilder();
        while(sb.length() + signifikanteSifre.length() < 8) {
            sb.append('0');
        }
        sb.append(signifikanteSifre);
        inkrementForEksternReferanse++;
        return "AR" + sb.toString();
    }

}
