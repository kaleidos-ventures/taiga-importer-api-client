package net.kaleidos.domain

import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@ToString
@Builder(builderStrategy = SimpleStrategy)
class Project {
    String name
    String description
    Date createdDate

    Long id

    List<String> issueStatuses
    List<String> issueTypes
    List<String> issuePriorities
    List<String> issueSeverities

    List<String> roles
//    List<Membership> memberships

    Membership findMembershipByEmail(String email) {
        memberships.find { it.email == email }
    }

    Membership findMembershipById(Long id) {
        memberships.find { it.id == id }
    }
}