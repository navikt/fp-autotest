package no.nav.foreldrepenger.autotest.base;

public enum Paragrafer {

    P_14_1("14-1"),
    P_14_2("14-2"),
    P_14_3("14-3"),
    P_14_4("14-4"),
    P_14_5("14-5"),
    P_14_6("14-6"),
    P_14_7("14-7"),
    P_14_8("14-8"),
    P_14_9("14-9"),
    P_14_10("14-10"),
    P_14_11("14-11"),
    P_14_12("14-12"),
    P_14_13("14-13"),
    P_14_14("14-14"),
    P_14_15("14-15"),
    P_14_16("14-16"),
    P_14_17("14-17"),
    P_14_18("14-18"),

    P_8_30("8-30"),
    P_8_35("8-35"),
    P_8_38("8-38"),
    P_8_41("8-41"),
    P_8_49("8-49"),

    P_21_3("21-3"),
    ;

    private String kode;

    Paragrafer(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }
}
