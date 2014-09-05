package net.kaleidos.taiga.builder

import net.kaleidos.domain.Project
import net.kaleidos.domain.User
import net.kaleidos.taiga.common.DateConversions

class UserBuilder implements DateConversions {

    User build(Map json, Project project) {
        def user = new User()

        user.with {
            email = json.user[0]
            name = json.user[1]
        }

        user
    }
}