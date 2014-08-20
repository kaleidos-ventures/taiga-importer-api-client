package net.kaleidos.redmine.convert

import net.kaleidos.domain.project.Project
import groovy.transform.CompileStatic

@CompileStatic
final class Converters {

    static final Closure<Project> project() {
        new ProjectConverter().&convert
    }

}
