package net.kaleidos.taiga.builder

import net.kaleidos.domain.User

class UserBuilder {

    User build(Map json) {
        def user = new User()

        user.with {
            id = json.id
            email = json.email
        }

        user
    }
}