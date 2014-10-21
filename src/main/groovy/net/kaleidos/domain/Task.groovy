package net.kaleidos.domain

import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@ToString
@Builder(builderStrategy = SimpleStrategy)
class Task {
    Long ref

    String status

    String subject
    String description
    Project project

    String owner
    Date createdDate
    Date finishedDate

    UserStory userStory
    Milestone milestone

    List<Attachment> attachments

    List<History> history

    List<String> tags

    String assignedTo
}