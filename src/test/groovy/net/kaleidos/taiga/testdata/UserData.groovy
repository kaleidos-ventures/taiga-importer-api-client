package net.kaleidos.taiga.testdata

import net.kaleidos.domain.User

trait UserData {

    User buildBasicUser() {
        new User().setEmail('admin@admin.com').setName('The fullname')
    }
}