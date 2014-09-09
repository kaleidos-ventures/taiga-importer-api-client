package net.kaleidos.taiga.builder

import net.kaleidos.domain.Project

interface TaigaEntityBuilder<T> {

    T build(Map json, Project project)

}