package net.kaleidos.taiga.builder

trait SafeJson {

    /**
     * This method is used to prevent triggering the toString method of the
     * inner class used by wslite.json.JSONObject to manage null types
     */
    def nullSafe(whatever) {
        return whatever ?: null
    }
}
