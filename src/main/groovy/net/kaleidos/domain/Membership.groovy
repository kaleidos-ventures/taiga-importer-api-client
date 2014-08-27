package net.kaleidos.domain

import groovy.transform.ToString

@ToString
class Membership {
    Long id
    String email
    Long userId
    String token
}