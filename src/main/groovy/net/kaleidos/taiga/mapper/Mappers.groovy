package net.kaleidos.taiga.mapper

import net.kaleidos.domain.Attachment
import net.kaleidos.domain.History
import net.kaleidos.domain.Issue
import net.kaleidos.domain.Project
import net.kaleidos.domain.Wikilink
import net.kaleidos.domain.Wikipage

class Mappers {

    static map(Project project) {
        new ProjectMapper().map(project)
    }

    static map(Issue issue) {
        new IssueMapper().map(issue)
    }

    static map(Attachment attachment) {
        new AttachmentMapper().map(attachment)
    }

    static map(History history) {
        new HistoryMapper().map(history)
    }

    static map(Wikipage wikipage) {
        new WikipageMapper().map(wikipage)
    }

    static map(Wikilink wikilink) {
        new WikilinkMapper().map(wikilink)
    }
}