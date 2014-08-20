package net.kaleidos.redmine.convert

import groovy.transform.CompileStatic

@CompileStatic
interface Converter<I,O> {
    O convert(I input)
}
