package net.kaleidos.domain

import groovy.transform.ToString

@ToString
class Wikipage {
    Project project
    String slug
    String content
}