package net.kaleidos.domain

import groovy.transform.ToString

@ToString(includeNames=true)
class Wikipage {
    Project project
    String slug
    String content
}
