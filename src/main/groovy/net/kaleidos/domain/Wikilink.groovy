package net.kaleidos.domain

import groovy.transform.ToString

@ToString
class Wikilink {
    Project project
    String title
    String href
}