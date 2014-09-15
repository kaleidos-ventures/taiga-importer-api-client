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
 * This interface exposes all public Redmine methods needed by the migrator.
 * Because the  RedmineManger class is (obviously) a class, it's harder to mock it up, and
 * consequently, harder to test and maintain.
 *
 * That's why we've created this interface to
 * make easier to isolate Redmine interaction from Taiga interaction.
 *
 **/
interface RedmineClient {

    /**
     * find all projects from the configured redmine instance
     **/
    List<Project> findAllProject()

    /**
     * find all trackers from the configured redmine instance
     **/
    List<Tracker> findAllTracker()

    /**
     * find all basic issues info from the project passed as parameter
     **/
    List<Issue> findAllIssueByProjectId(String id)

    /**
     * find all possible issue statuses info from the configured redmine instance
     **/
    List<IssueStatus> findAllIssueStatus()

    /**
     * find all possible issue priorities from the redmine instance
     **/
    List<IssuePriority> findAllIssuePriority()

    /**
     * find all membership from a given project
     **/
    List<Membership> findAllMembershipByProjectId(String projectId)

    /**
     * Find all basic information of wiki pages found in a project
     */
    List<WikiPage> findAllWikiPageByProjectId(String projectId)

    /**
     * Find full information of a given project
     */
    Project findProjectById(String projectId)

    /**
     * Find full information of a given issue
     **/
    Issue findIssueById(Integer issueId)

    /**
     * Find full information of a given user
     */
    User findUserFullById(Integer userId)

}
