package net.kaleidos.taiga.builder

interface TaigaEntityBuilder<T> {

    T build(Map json)

}