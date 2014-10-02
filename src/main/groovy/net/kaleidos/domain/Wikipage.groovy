package net.kaleidos.domain

import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@ToString(includeNames = true)
@Builder(builderStrategy = SimpleStrategy)
// tag::wikipage[]
class Wikipage {
    Project project

    String slug
    String content
    String owner
    Date createdDate

    List<Attachment> attachments
}
// end::wikipage[]