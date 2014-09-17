package net.kaleidos.redmine.migrator

import net.kaleidos.taiga.TaigaClient
import net.kaleidos.redmine.RedmineClient

abstract class AbstractMigrator<A> implements Migrator<A> {

    final TaigaClient taigaClient
    final RedmineClient redmineClient

    AbstractMigrator(final RedmineClient redmineClient, final TaigaClient taigaClient) {
        this.redmineClient = redmineClient
        this.taigaClient = taigaClient
    }

}
