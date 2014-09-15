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
        return null
    }

    @Override
    List<IssuePriority> findAllPriority(){
        return null
    }

    @Override
    List<Membership> findAllMembershipByProjectId(String projectId){
        return null
    }

    @Override
    List<WikiPage> findAllWikiPageByProjectId(String projectId){
        return null
    }

    @Override
    Project findProjectById(String projectId){
        return null
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
