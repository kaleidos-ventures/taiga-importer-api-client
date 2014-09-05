package net.kaleidos.taiga.common

trait DateConversions {

    private static final String TAIGA_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ"

    Date parse(String stringDate) {
        Date.parse(TAIGA_DATE_FORMAT, stringDate)
    }

    String format(Date date) {
        date?.format(TAIGA_DATE_FORMAT)
    }
}