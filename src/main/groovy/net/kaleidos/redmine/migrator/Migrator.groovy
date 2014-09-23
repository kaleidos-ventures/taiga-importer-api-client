package net.kaleidos.redmine.migrator

interface Migrator<A> {
    A save(A instance)
}