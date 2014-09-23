package net.kaleidos.redmine

import com.taskadapter.redmineapi.RedmineManagerFactory

final class RedmineClientFactory {

    public static RedmineClient newInstance(String host, String apiKey) {
        return new RedmineClientImpl(
            redmineManager: RedmineManagerFactory.createWithApiKey(host, apiKey)
        )
    }

}