package net.kaleidos.redmine.migrator

import com.github.slugify.Slugify
import net.kaleidos.redmine.RedmineClient
import net.kaleidos.taiga.TaigaClient

abstract class AbstractMigrator<A> implements Migrator<A> {

    final TaigaClient taigaClient
    final RedmineClient redmineClient

    AbstractMigrator(final RedmineClient redmineClient, final TaigaClient taigaClient) {
        this.redmineClient = redmineClient
        this.taigaClient = taigaClient
    }

    String slugify(String possible) {
        return new Slugify().slugify(possible)
    }
}
