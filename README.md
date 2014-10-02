# Taiga Importer API Client

[ ![Download](https://api.bintray.com/packages/kaleidos/maven/taiga-importer-api-client/images/download.svg) ](https://bintray.com/kaleidos/maven/taiga-importer-api-client/_latestVersion)

This is a library for the [Taiga](http://taiga.io) Importer API. You can use it to migrate your projects from existing tools like Redmine to Taiga.

The library is written in Groovy and it's published to [Kaleidos' Bintray](https://bintray.com/kaleidos/maven/taiga-importer-api-client/view)

If you want to use you only have to include the following Gradle dependency:

```groovy
repositories {
    maven {
        url 'http://dl.bintray.com/kaleidos/maven'
    }
}

dependencies {
    compile 'net.kaleidos:taiga-importer-api-client:1.0.1'
}
```

Or if you are using Maven:

```xml
<repository>
    <id>kaleidos-bintray</id>
    <name>Kaleidos bintray</name>
    <url>http://dl.bintray.com/kaleidos/maven</url>
</repository>

<dependency>
    <groupId>net.kaleidos</groupId>
    <artifactId>taiga-importer-api-client</artifactId>
    <version>1.0.1</version>
    <type>pom</type>
</dependency>
```

Please note that although the client library is developed with Groovy, you can use it from Java. Please check this [example project](https://github.com/taigaio/taiga-java-importer-example).
)