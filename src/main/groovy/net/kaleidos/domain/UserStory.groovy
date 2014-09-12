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

    List<Attachment> attachments

    List<History> history

    List<RolePoint> rolePoints

    @Builder(builderStrategy = SimpleStrategy)
    class RolePoint {
        String role
        String points
    }
}