package net.kaleidos.domain.project

import groovy.transform.ToString
import net.kaleidos.domain.issue.IssuePriority
import net.kaleidos.domain.issue.IssueStatus
import net.kaleidos.domain.issue.IssueType

@ToString
class Project {
    String name
    String description

    Long id
    Long defaultUsStatus
    Long defaultTaskStatus
    Long defaultPriority
    Long defaultSeverity
    Long defaultIssueStatus
    Long defaultIssueType

    List<IssueStatus> issueStatuses
    List<IssueType> issueTypes
    List<IssuePriority> issuePriorities
}