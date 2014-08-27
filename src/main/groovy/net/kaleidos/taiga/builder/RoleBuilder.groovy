package net.kaleidos.taiga.builder

import net.kaleidos.domain.Role

class RoleBuilder {

    Role build(Map json) {
        def role = new Role()

        role.with {
            id = json.id
            name = json.name
        }

        role
    }
}