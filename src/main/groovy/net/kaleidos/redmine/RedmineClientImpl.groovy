package net.kaleidos.redmine

import com.taskadapter.redmineapi.RedmineManager
import com.taskadapter.redmineapi.bean.Issue
import com.taskadapter.redmineapi.bean.IssuePriority
import com.taskadapter.redmineapi.bean.IssueStatus
import com.taskadapter.redmineapi.bean.Membership
import com.taskadapter.redmineapi.bean.Project
import com.taskadapter.redmineapi.bean.Tracker
import com.taskadapter.redmineapi.bean.User
import com.taskadapter.redmineapi.bean.Version
import com.taskadapter.redmineapi.bean.WikiPage
import com.taskadapter.redmineapi.bean.WikiPageDetail

class RedmineClientImpl implements RedmineClient {

    private RedmineManager redmineManager

    @Override
    List<Project> findAllProject() {
        return redmineManager.projects
    }

    @Override
    List<Tracker> findAllTracker() {
        return redmineManager.trackers
    }

    @Override
    List<Issue> findAllIssueByProjectIdentifier(String identifier) {
        return redmineManager.getIssues(project_id: identifier)
    }

    @Override
    List<IssueStatus> findAllIssueStatus() {
        return redmineManager.getStatuses()
    }

    @Override
    List<IssuePriority> findAllIssuePriority() {
        return redmineManager.issuePriorities
    }

    @Override
    List<Membership> findAllMembershipByProjectIdentifier(String projectIdentifier) {
        return redmineManager.getMemberships(projectIdentifier)
    }

    @Override
    List<WikiPage> findAllWikiPageByProjectIdentifier(String projectIdentifier) {
        return redmineManager.getWikiPagesByProject(
            new Project(identifier: projectIdentifier)
        )
    }

    @Override
    WikiPageDetail findCompleteWikiPageByProjectIdentifierAndTitle(String projectIdentifier, String wikiTitle) {
        return redmineManager
            .getWikiPageDetailByProjectAndTitle(
            new Project(identifier: projectIdentifier),
            wikiTitle
        )
    }

    @Override
    Project findProjectByIdentifier(String projectIdentifier) {
        return redmineManager.getProjectByKey(projectIdentifier)
    }

    @Override
    Issue findIssueById(Integer issueId) {
        return redmineManager.getIssueById(issueId)
    }

    @groovy.transform.Memoized
    User findUserFullById(Integer userId){
        return redmineManager.getUserById(userId)
    }

    @Override
    List<Version> findAllVersionByProjectId(Integer projectId) {
        return redmineManager.getVersions(projectId)
    }
}
