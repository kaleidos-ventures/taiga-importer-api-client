package net.kaleidos.domain

import groovy.transform.ToString

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

    List<Role> roles
    List<Membership> memberships

    IssueStatus findIssueStatusByName(String name) {
        issueStatuses.find { it.name == name }
    }

    IssueStatus findIssueStatusById(Long id) {
        issueStatuses.find { it.id == id }
    }

    IssueType findIssueTypeByName(String name) {
        issueTypes.find { it.name == name }
    }

    IssueType findIssueTypeById(Long id) {
        issueTypes.find { it.id == id }
    }

    IssuePriority findIssuePriorityByName(String name) {
        issuePriorities.find { it.name == name }
    }

    IssuePriority findIssuePriorityById(Long id) {
        issuePriorities.find { it.id == id }
    }

    Role findRoleByName(String name) {
        roles.find { it.name == name }
    }

    Role findRoleById(Long id) {
        roles.find { it.id == id }
    }

    Membership findMembershipByEmail(String email) {
        memberships.find { it.email == email }
    }

    Membership findMembershipById(Long id) {
        memberships.find { it.id == id }
    }
}