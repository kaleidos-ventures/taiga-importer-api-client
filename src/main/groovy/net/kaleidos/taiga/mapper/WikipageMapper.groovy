package net.kaleidos.taiga.mapper

import net.kaleidos.domain.Wikipage
import net.kaleidos.taiga.common.DateConversions

class WikipageMapper implements Mapper<Wikipage>, DateConversions {

    @Override
    Map map(Wikipage wikipage) {
        [
            slug        : wikipage.slug,
            content     : wikipage.content,
            created_date: format(wikipage.createdDate),
            modified_date: format(wikipage.createdDate),
            owner       : wikipage.owner,
            attachments : wikipage.attachments.collect { Mappers.map(it) },
        ]
    }
}
