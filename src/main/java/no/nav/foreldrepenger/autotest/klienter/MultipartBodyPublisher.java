package no.nav.foreldrepenger.autotest.klienter;

import no.nav.foreldrepenger.autotest.util.ReadFileFromClassPathHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MultipartBodyPublisher {
    private final String boundary;
    private final ByteArrayOutputStream outputStream;

    public MultipartBodyPublisher() {
        this.boundary = UUID.randomUUID().toString();
        this.outputStream = new ByteArrayOutputStream();
    }

    public String getContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }


    public void addFile(String fieldName, String pathFile) {
        byte[] fileBytes = ReadFileFromClassPathHelper.readFileBytes(pathFile);
        String header = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"dummy.pdf\"\r\n"
                + "Content-Type: application/pdf\r\n\r\n";
        writeToOutputStream(header.getBytes(StandardCharsets.UTF_8));
        writeToOutputStream(fileBytes);
        writeToOutputStream("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    public byte[] build() {
        writeToOutputStream(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        return outputStream.toByteArray();
    }


    private void writeToOutputStream(byte[] header) {
        try {
            outputStream.write(header);
        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke skrive til outputstream", e);
        }

    }
}
