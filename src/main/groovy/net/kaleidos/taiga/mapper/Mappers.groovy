package net.kaleidos.taiga.mapper

import net.kaleidos.domain.Attachment
import net.kaleidos.domain.History
import net.kaleidos.domain.Issue
import net.kaleidos.domain.Milestone
import net.kaleidos.domain.Project
import net.kaleidos.domain.Task
import net.kaleidos.domain.UserStory
import net.kaleidos.domain.UserStory.RolePoint
import net.kaleidos.domain.Wikilink
import net.kaleidos.domain.Wikipage

class Mappers {

    static Map map(Project project) {
        new ProjectMapper().map(project)
    }

    static Map map(Issue issue) {
        new IssueMapper().map(issue)
    }

    static Map map(Attachment attachment) {
        new AttachmentMapper().map(attachment)
    }

    static Map map(History history) {
        new HistoryMapper().map(history)
    }

    static Map map(Wikipage wikipage) {
        new WikipageMapper().map(wikipage)
    }

    static Map map(Wikilink wikilink) {
        new WikilinkMapper().map(wikilink)
    }

    static Map map(UserStory userStory) {
        new UserStoryMapper().map(userStory)
    }

    static Map map(RolePoint rolePoint) {
        new RolePointMapper().map(rolePoint)
    }

    static Map map(Milestone milestone) {
        new MilestoneMapper().map(milestone)
    }

    static Map map(Task task) {
        new TaskMapper().map(task)
    }
}