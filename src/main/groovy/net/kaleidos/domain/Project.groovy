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

    List<Role> roles
//    List<Membership> memberships

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