package net.kaleidos.taiga

import net.kaleidos.taiga.testdata.TaigaTestData
import spock.lang.Specification

class TaigaSpecBase extends Specification implements TaigaTestData {

    def setup() {
        taigaClient = createAuthenticatedTaigaClient()
    }

}