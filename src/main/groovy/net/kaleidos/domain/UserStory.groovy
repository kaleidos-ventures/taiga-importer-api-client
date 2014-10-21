package net.kaleidos.domain

import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@ToString
@Builder(builderStrategy = SimpleStrategy)
class UserStory {
    Long ref

    String status

    String subject
    String description
    Project project

    String owner
    Date createdDate
    Date finishedDate

    List<Attachment> attachments

    List<History> history

    List<RolePoint> rolePoints

    List<String> tags

    String assignedTo

    @Builder(builderStrategy = SimpleStrategy)
    class RolePoint {
        String role
        String points
    }
}