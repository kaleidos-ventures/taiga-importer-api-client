package net.kaleidos.domain.issue

import groovy.transform.ToString
import net.kaleidos.domain.project.Project

@ToString
class Issue {
    IssueType type
    IssueStatus status
    IssuePriority priority

    String subject
    String description
    Project project
}