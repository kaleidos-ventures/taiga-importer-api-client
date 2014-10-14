package net.kaleidos.domain

import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@ToString
@Builder(builderStrategy = SimpleStrategy)
// tag::project[]
class Project {
    String name
    String description
    Date createdDate
    String slug

    Long id

    List<IssueStatus> issueStatuses
    List<String> issueTypes
    List<String> issuePriorities
    List<String> issueSeverities

    List<String> roles
    List<Membership> memberships

    List<UserStoryStatus> userStoryStatuses
    List<EstimationPoint> points

    List<TaskStatus> taskStatuses
}
// end::project[]