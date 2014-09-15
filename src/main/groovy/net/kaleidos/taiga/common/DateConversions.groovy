package net.kaleidos.taiga.common

trait DateConversions {

    final String TAIGA_FULLDATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ"
    final String TAIGA_DATE_FORMAT = "yyyy-MM-dd"

    Date parse(String stringDate) {
        Date.parse(TAIGA_FULLDATE_FORMAT, stringDate)
    }

    Date parse(String stringDate, String format) {
        Date.parse(format, stringDate)
    }

    String format(Date date) {
        date?.format(TAIGA_FULLDATE_FORMAT)
    }

    String format(Date date, String format) {
        date?.format(format)
    }
}