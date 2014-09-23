package net.kaleidos.redmine

import com.taskadapter.redmineapi.bean.Issue
import com.taskadapter.redmineapi.bean.IssuePriority
import com.taskadapter.redmineapi.bean.IssueStatus
import com.taskadapter.redmineapi.bean.Membership
import com.taskadapter.redmineapi.bean.Project
import com.taskadapter.redmineapi.bean.Tracker
import com.taskadapter.redmineapi.bean.User
import com.taskadapter.redmineapi.bean.Version
import com.taskadapter.redmineapi.bean.WikiPage
import spock.lang.Specification

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
                client.findAllIssueByProjectIdentifier(project.identifier)
        then: 'the list shouldnt be empty'
            issueList.size() > 0
        and: 'the content should be the expected'
            with(issueList.first()) {
                id
            }
    }

    void 'Getting all issue statuses from a redmine instance'() {
        when: 'trying to get all issue statuses'
            List<IssueStatus> issueStatusList =
                client.findAllIssueStatus()
        then: 'the list shouldnt be empty'
            issueStatusList.size() > 0
        and: 'checking mandatory firstl from a random object'
            with(issueStatusList.first()) {
                id
                name
            }
    }

    void 'Getting all issue priorities from a redmine instance'() {
        when: 'trying to get all issue priorities'
            List<IssuePriority> issuePriorityList =
                client.findAllIssuePriority()
        then: 'the list should be empty'
            issuePriorityList.size() > 0
        and: 'checking first object'
            with(issuePriorityList.first()) {
                id
                name
            }
    }

    void 'Getting all memberships from a given project'() {
        given: 'a redmine project'
            Project project = client.findAllProject().first()
        when: 'trying to get all memberships from a given project'
            List<Membership> membershipList =
                client.findAllMembershipByProjectIdentifier(project.identifier)
        then: 'the list shouldnt be empty'
            membershipList.size() > 0
        and: 'object should have some mandatory data'
            with(membershipList.first()) {
                id
                user
                roles
            }
    }

    void 'Getting all basic wiki page info from a given project'() {
        given: 'a redmine project'
            Project project = client.findAllProject().first()
        when: 'trying to get all wiki pages from it'
            List<WikiPage> wikiPageList =
                client.findAllWikiPageByProjectIdentifier(project.identifier)
        then: 'we should get a list of wiki pages'
            wikiPageList.size() > 0
        and: 'basic fields are present'
            with(wikiPageList.first()) {
                title
                version
                createdOn
                updatedOn
            }
    }

    void 'Getting a specific redmine project by its id'() {
        given: 'a list of projects'
            List<Project> allAvailableProjects =
                client.findAllProject()
        when: 'getting a project from the list'
            Project project =
                client.findProjectByIdentifier(allAvailableProjects.first().identifier)
        then: 'I should be able to retrieve it again successfully'
            with(project) {
                id
                name
                description
            }
    }

    void 'Getting a specific redmine issue by id'() {
        given: 'a list of available projects'
            List<Project> allAvailableProjects = client.findAllProject()
        when: 'getting all issues'
            List<Issue> issueList =
                client.findAllIssueByProjectIdentifier(
                    allAvailableProjects
                        .first()
                        .identifier)
        and: 'getting specific info of one of them'
            Issue issue =
                client.findIssueById(issueList.first().id)
        then: 'we should be able to access extended fields'
            with(issue) {
                id
                subject
                author
                tracker
                priorityId
                priorityText
                statusId
                createdOn
                updatedOn
            }
    }

    void 'Getting full information about a given user'() {
        given: 'a list of memberships of a given project'
            List<Membership> projectMembershipList =
                client.findAllMembershipByProjectIdentifier(
                    client.findAllProject().first().identifier
                )
        when: 'getting the first available user'
            User user = client.findUserFullById(projectMembershipList.first().user.id)
        then: 'we should be able to get his/her email'
            with(user) {
                user.mail
            }
    }

    void 'Getting all versions of a project'() {
        given: 'a project'
            Project project = client.findAllProject().first()

        when: 'getting the versions'
            List<Version> versions = client.findAllVersionByProjectId(project.id)

        then: 'we get all the versions'
            versions.size() > 0
    }
}