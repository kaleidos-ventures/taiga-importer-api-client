package net.kaleidos.domain

import groovy.transform.ToString

@ToString(excludes = 'data')
class Attachment {
    String data
    String name
    String owner
}