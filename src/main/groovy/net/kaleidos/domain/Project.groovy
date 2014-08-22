package net.kaleidos.domain

import groovy.transform.ToString
import net.kaleidos.domain.IssuePriority
import net.kaleidos.domain.IssueStatus
import net.kaleidos.domain.IssueType

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

    IssueStatus findIssueStatusByName(String name) {
        issueStatuses.find { it.name == name }
    }

    IssueType findIssueTypeByName(String name) {
        issueTypes.find { it.name == name }
    }

    IssuePriority findIssuePriorityByName(String name) {
        issuePriorities.find { it.name == name }
    }
}