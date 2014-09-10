package net.kaleidos.taiga.builder

import net.kaleidos.domain.Membership
import net.kaleidos.domain.Project

class MembershipBuilder implements TaigaEntityBuilder<Membership> {

    Membership build(Map json, Project project) {
        def membership = new Membership()

        membership.with {
            email = json.email
            role = json.role
        }

        membership
    }
}