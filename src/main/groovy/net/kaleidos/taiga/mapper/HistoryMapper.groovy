package net.kaleidos.taiga.mapper

import net.kaleidos.domain.History
import net.kaleidos.taiga.common.DateConversions

class HistoryMapper implements Mapper<History>, DateConversions {

    @Override
    Map map(History history) {
        [
            user      : [history.user.email, history.user.name],
            created_at: format(history.createdAt),
            type      : 1,
            comment   : history.comment,
        ]
    }
}