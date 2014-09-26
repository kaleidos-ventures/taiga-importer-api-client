package net.kaleidos.taiga.testdata

import net.kaleidos.domain.Issue
import net.kaleidos.domain.Project

trait IssueData {

    Issue buildBasicIssue(Project project) {
        // tag::createIssue[]
        new Issue()
            .setType('Bug')
            .setStatus('New')
            .setPriority('Normal')
            .setSeverity('Normal')
            .setSubject('The subject')
            .setDescription('The description')
            .setProject(project)
        // end::createIssue[]
    }
}