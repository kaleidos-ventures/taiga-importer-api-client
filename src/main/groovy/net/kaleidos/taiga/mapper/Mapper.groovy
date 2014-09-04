package net.kaleidos.taiga.mapper

interface Mapper<T> {
    Map map(T o)
}