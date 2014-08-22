package net.kaleidos.redmine

/**
 * This closure is used as a marker used with the
 * Spock framework's @IgnoreIf annotation
 */
class NoRedmineAvailable extends Closure<Boolean> {

    /**
     * I don't know why, the default constructor is
     * enough for using it. It's neccessary to crea
     * this constructor. How I knew it ? Checking o
     * the Spock framework's source code :P
     */
    NoRedmineAvailable(Object owner,Object thisObject) {
        super(owner, thisObject)
    }

    /**
     * The method returns true if there is no Redmine
     * environment, false otherwise
     */
    Boolean doCall() {
        return !System.getProperty('redmineAvailable')
    }

}
