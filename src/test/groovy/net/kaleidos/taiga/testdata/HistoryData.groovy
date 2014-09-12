package net.kaleidos.taiga.testdata

import net.kaleidos.domain.History
import net.kaleidos.domain.User

trait HistoryData {

    History buildBasicHistory(User user, Date createdAt, String comment) {
        new History()
            .setUser(user)
            .setCreatedAt(createdAt)
            .setComment(comment)
    }

}