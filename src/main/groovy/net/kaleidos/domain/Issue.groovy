package net.kaleidos.domain

import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@ToString
@Builder(builderStrategy = SimpleStrategy)
class Issue {
    Long id

    String type
    String status
    String priority
    String severity

    String subject
    String description
    String author
    Project project

    List<Attachment> attachments
}