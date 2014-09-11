package net.kaleidos.taiga.testdata

import net.kaleidos.domain.Membership

trait MembershipData {

    Membership buildBasicMembership(String email, String role) {
        new Membership().setEmail(email).setRole(role)
    }

}