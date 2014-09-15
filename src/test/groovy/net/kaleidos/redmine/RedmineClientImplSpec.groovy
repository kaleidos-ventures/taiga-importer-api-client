package net.kaleidos.redmine

import spock.lang.Specification

import com.taskadapter.redmineapi.RedmineManager
import com.taskadapter.redmineapi.bean.Issue
import com.taskadapter.redmineapi.bean.IssueStatus
import com.taskadapter.redmineapi.bean.Project
import com.taskadapter.redmineapi.bean.Attachment
import com.taskadapter.redmineapi.bean.Tracker
import com.taskadapter.redmineapi.bean.Journal
import com.taskadapter.redmineapi.bean.User
import com.taskadapter.redmineapi.bean.Membership
import com.taskadapter.redmineapi.bean.WikiPage
import com.taskadapter.redmineapi.bean.WikiPageDetail

/**
 *
 **/
class RedmineClientImplSpec extends Specification {

    RedmineClient client

    void setup() {
        def config =
            new ConfigSlurper()
                .parse(new File('src/test/resources/redmine.groovy').text)
        client =
            RedmineClientFactory.newInstance(config.host, config.apiKey)
    }

    void 'Testing how to get all avaiable projects in a redmine instance'() {
        when: 'trying to get a list of all available projects'
            List<Project> projectList = client.findAllProject()
        then: 'the list should carry a list of projects'
            projectList.size() > 0
        and: 'checking the content of the first object'
            with(projectList.first()) {
                id
                identifier
                name
                description
                createdOn
                updatedOn
            }
    }

    void 'Getting all trackers of a redmine instance'() {
        when: 'trying to get all trackers created in redmine'
            List<Tracker> trackerList = client.findAllTracker()
        then: 'the list shouldnt be empty'
            trackerList.size() > 0
        and: 'checking the content of the first object'
            with(trackerList.first()) {
                id
                name
            }
    }

    void 'Getting basic all issued basic info'() {
        given: 'a project from redmine'
            Project project = client.findAllProject().first()
        when: 'trying to get all basic issue info from a given project'
            List<Issue> issueList =
                client.findAllIssueByProjectId(project.identifier)
        then: 'the list shouldnt be empty'
            issueList.size() > 0
        and: 'the content should be the expected'
            with(issueList.first()) {
                id
            }
    }


}

