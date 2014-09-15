package net.kaleidos.redmine

import com.taskadapter.redmineapi.RedmineManager
import com.taskadapter.redmineapi.bean.Issue
import com.taskadapter.redmineapi.bean.IssueStatus
import com.taskadapter.redmineapi.bean.IssuePriority
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
class RedmineClientImpl implements RedmineClient {

    private RedmineManager redmineManager

    @Override
    List<Project> findAllProject() {
        return redmineManager.projects
    }

    @Override
    List<Tracker> findAllTracker(){
        return redmineManager.trackers
    }

    @Override
    List<Issue> findAllIssueByProjectId(String id){
        return redmineManager.getIssues(project_id: id)
    }

    @Override
    List<IssueStatus> findAllIssueStatus(){
        return redmineManager.getStatuses()
    }

    @Override
    List<IssuePriority> findAllIssuePriority(){
        return redmineManager.issuePriorities
    }

    @Override
    List<Membership> findAllMembershipByProjectId(String projectId){
        return redmineManager.getMemberships(projectId)
    }

    @Override
    List<WikiPage> findAllWikiPageByProjectId(String projectId){
        return redmineManager.getWikiPagesByProject(
           new Project(identifier: projectId)
        )
    }

    @Override
    Project findProjectById(String projectId){
        return redmineManager.getProjectByKey(projectId)
    }

    @Override
    Issue findIssueById(Integer issueId){
        return null
    }

    @Override
    User findUserFullById(Integer userId){
        return null
    }

}
