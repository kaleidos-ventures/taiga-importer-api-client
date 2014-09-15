package net.kaleidos.domain

import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@ToString
@Builder(builderStrategy = SimpleStrategy)
class Milestone {
    String name
    Boolean isClosed
    Date startDate
    Date endDate

    Project project
}