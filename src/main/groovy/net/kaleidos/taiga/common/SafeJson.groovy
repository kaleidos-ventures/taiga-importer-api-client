package net.kaleidos.taiga.common

trait SafeJson {

    /**
     * This method is used because wslite.json.JSONObject returns
     * "null" instead of a null object
     */
    def nullSafe(whatever) {
        return (whatever.toString() == "null" ? null : whatever)
    }
}