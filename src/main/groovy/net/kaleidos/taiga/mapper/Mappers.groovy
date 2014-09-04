package net.kaleidos.taiga.mapper

import net.kaleidos.domain.Issue
import net.kaleidos.domain.Project

class Mappers {

    static map(Project project) {
        new ProjectMapper().map(project)
    }

    static map(Issue issue) {
        new IssueMapper().map(issue)
    }
}