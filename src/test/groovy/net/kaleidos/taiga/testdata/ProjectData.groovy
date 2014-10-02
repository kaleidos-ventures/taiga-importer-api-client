package net.kaleidos.taiga.testdata

import net.kaleidos.domain.EstimationPoint
import net.kaleidos.domain.IssueStatus
import net.kaleidos.domain.Membership
import net.kaleidos.domain.Project
import net.kaleidos.domain.TaskStatus
import net.kaleidos.domain.UserStoryStatus

trait ProjectData {

    private static final List STATUSES = [
        [name: 'New', isClosed: false],
        [name: 'In progress', isClosed: false],
        [name: 'Ready for test', isClosed: true],
        [name: 'Closed', isClosed: true],
        [name: 'Needs Info', isClosed: false],
        [name: 'Rejected', isClosed: false]
    ]

    private static final List ESTIMATION_POINTS = [
        [name: '?', value: null],
        [name: '0', value: 0],
        [name: '1/2', value: 0.5],
        [name: '1', value: 1],
        [name: '2', value: 2],
        [name: '3', value: 3],
        [name: '5', value: 5],
        [name: '8', value: 8],
        [name: '13', value: 13]
    ]

    Project buildProject(String name, String description) {
        // tag::createProject[]
        new Project()
            .setName(name)
            .setDescription(description)
        // end::createProject[]
    }

    Project buildBasicProject() {
        this.buildProject("name ${new Date().time}", "description")
    }

    Project buildProject() {
        // tag::createProject2[]
        this.buildProject("name ${new Date().time}", "description")
            .setIssueTypes(['Bug', 'Question', 'Enhancement'])
            .setIssueStatuses(buildIssueStatuses())
            .setIssuePriorities(['Low', 'Normal', 'High'])
            .setIssueSeverities(['Minor', 'Normal', 'Important', 'Critical'])
            .setRoles(['UX', 'Front', 'Back'])
            .setMemberships([new Membership().setEmail('admin@admin.com').setRole('Back')])
            .setPoints(buildEstimationPoints())
            .setUserStoryStatuses(buildUserStoryStatuses())
            .setTaskStatuses(buildTaskStatuses())
        // end::createProject2[]
    }

    List<UserStoryStatus> buildUserStoryStatuses() {
        STATUSES.collect {
            new UserStoryStatus().setName(it.name).setIsClosed(it.isClosed)
        }
    }

    List<IssueStatus> buildIssueStatuses() {
        STATUSES.collect {
            new IssueStatus().setName(it.name).setIsClosed(it.isClosed)
        }
    }

    List<TaskStatus> buildTaskStatuses() {
        STATUSES.collect {
            new TaskStatus().setName(it.name).setIsClosed(it.isClosed)
        }
    }

    List<EstimationPoint> buildEstimationPoints() {
        ESTIMATION_POINTS.collect {
            new EstimationPoint().setName(it.name).setValue(it.value)
        }
    }
}