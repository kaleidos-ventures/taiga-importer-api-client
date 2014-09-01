package net.kaleidos.domain

import groovy.transform.ToString

@ToString
class Issue {
    Long id

    IssueType type
    IssueStatus status
    IssuePriority priority

    String subject
    String description
    String author
    Project project
}
