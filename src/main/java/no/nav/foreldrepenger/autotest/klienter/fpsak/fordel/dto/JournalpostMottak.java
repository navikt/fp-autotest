package no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class JournalpostMottak {
    private static int inkrementForEksternReferanse = 0;

    private String saksnummer;
    private String journalpostId;
    private String forsendelseId;
    private String eksternReferanseId;
    private String behandlingstemaOffisiellKode;
    private String dokumentTypeIdOffisiellKode;
    private String forsendelseMottatt;
    private String payloadXml;
    private Integer payloadLength;
    private String dokumentKategoriOffisiellKode;

    public JournalpostMottak(String saksnummer, String journalpostId, LocalDate forsendelseMottatt,
            String behandlingstemaOffisiellKode) {
        this(saksnummer, journalpostId, forsendelseMottatt.toString(), behandlingstemaOffisiellKode);
    }

    public JournalpostMottak(String saksnummer, String journalpostId, String forsendelseMottatt,
            String behandlingstemaOffisiellKode) {
        this.saksnummer = saksnummer;
        this.journalpostId = journalpostId;
        this.forsendelseMottatt = forsendelseMottatt;
        this.behandlingstemaOffisiellKode = behandlingstemaOffisiellKode;
    }

    @JsonCreator
    public JournalpostMottak(String saksnummer, String journalpostId, String forsendelseId,
                             String behandlingstemaOffisiellKode, String dokumentTypeIdOffisiellKode,
                             String forsendelseMottatt, String payloadXml, Integer payloadLength,
                             String dokumentKategoriOffisiellKode) {
        this.saksnummer = saksnummer;
        this.journalpostId = journalpostId;
        this.forsendelseId = forsendelseId;
        this.behandlingstemaOffisiellKode = behandlingstemaOffisiellKode;
        this.dokumentTypeIdOffisiellKode = dokumentTypeIdOffisiellKode;
        this.forsendelseMottatt = forsendelseMottatt;
        this.payloadXml = payloadXml;
        this.payloadLength = payloadLength;
        this.dokumentKategoriOffisiellKode = dokumentKategoriOffisiellKode;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    public String getForsendelseId() {
        return forsendelseId;
    }

    public void setForsendelseId(String forsendelseId) {
        this.forsendelseId = forsendelseId;
    }

    public String getBehandlingstemaOffisiellKode() {
        return behandlingstemaOffisiellKode;
    }

    public String getForsendelseMottatt() {
        return forsendelseMottatt;
    }

    public String getDokumentKategoriOffisiellKode() {
        return dokumentKategoriOffisiellKode;
    }

    public void setDokumentKategoriOffisiellKode(String dokumentKategoriOffisiellKode) {
        this.dokumentKategoriOffisiellKode = dokumentKategoriOffisiellKode;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JournalpostMottak that = (JournalpostMottak) o;
        return Objects.equals(saksnummer, that.saksnummer) &&
                Objects.equals(journalpostId, that.journalpostId) &&
                Objects.equals(forsendelseId, that.forsendelseId) &&
                Objects.equals(behandlingstemaOffisiellKode, that.behandlingstemaOffisiellKode) &&
                Objects.equals(dokumentTypeIdOffisiellKode, that.dokumentTypeIdOffisiellKode) &&
                Objects.equals(forsendelseMottatt, that.forsendelseMottatt) &&
                Objects.equals(payloadXml, that.payloadXml) &&
                Objects.equals(payloadLength, that.payloadLength) &&
                Objects.equals(dokumentKategoriOffisiellKode, that.dokumentKategoriOffisiellKode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(saksnummer, journalpostId, forsendelseId, behandlingstemaOffisiellKode, dokumentTypeIdOffisiellKode, forsendelseMottatt, payloadXml, payloadLength, dokumentKategoriOffisiellKode);
    }

    @Override
    public String toString() {
        return "JournalpostMottak{" +
                "saksnummer='" + saksnummer + '\'' +
                ", journalpostId='" + journalpostId + '\'' +
                ", forsendelseId='" + forsendelseId + '\'' +
                ", behandlingstemaOffisiellKode='" + behandlingstemaOffisiellKode + '\'' +
                ", dokumentTypeIdOffisiellKode='" + dokumentTypeIdOffisiellKode + '\'' +
                ", forsendelseMottatt='" + forsendelseMottatt + '\'' +
                ", payloadXml='" + payloadXml + '\'' +
                ", payloadLength=" + payloadLength +
                ", dokumentKategoriOffisiellKode='" + dokumentKategoriOffisiellKode + '\'' +
                '}';
    }

    public void setEksternReferanseId(String eksternReferanseId) {
        this.eksternReferanseId = eksternReferanseId;
    }

    public String lagUnikEksternReferanseId() {
        inkrementForEksternReferanse++;
        return "AR" + String.format("%08d", inkrementForEksternReferanse);
    }

}
