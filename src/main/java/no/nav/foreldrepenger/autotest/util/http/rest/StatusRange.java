package no.nav.foreldrepenger.autotest.util.http.rest;

public enum StatusRange {
    STATUS_SUCCESS(200, 299),
    STATUS_REDIRECT(300, 399),
    STATUS_CLIENT_ERROR(400, 499),
    STATUS_SERVER_ERROR(500, 599),
    STATUS_NO_SERVER_ERROR(200, 499),
    STATUS_200(200, 200);

    public int min;
    public int max;

    StatusRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public boolean inRange(int number) {
        return ((number >= min) && (number <= max));
    }

}
