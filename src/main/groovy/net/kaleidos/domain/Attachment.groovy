package net.kaleidos.domain

import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@ToString(excludes = 'data')
@Builder(builderStrategy = SimpleStrategy)
class Attachment {
    String data
    String name
    String owner
    String description
    Date createdDate
}