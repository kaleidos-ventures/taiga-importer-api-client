package net.kaleidos.redmine.migrator

import com.taskadapter.redmineapi.bean.Attachment as RedmineAttachment
import com.taskadapter.redmineapi.bean.Issue as RedmineIssue
import com.taskadapter.redmineapi.bean.Journal as RedmineHistory
import com.taskadapter.redmineapi.bean.User as RedmineUser
import groovy.transform.InheritConstructors
import groovy.util.logging.Log4j
import net.kaleidos.domain.Attachment as TaigaAttachment
import net.kaleidos.domain.History as TaigaHistory
import net.kaleidos.domain.Issue as TaigaIssue
import net.kaleidos.domain.Project as TaigaProject
import net.kaleidos.domain.User as TaigaUser
import net.kaleidos.redmine.RedmineTaigaRef

@Log4j
@InheritConstructors
class IssueMigrator extends AbstractMigrator<TaigaIssue> {

    final String SEVERITY_NORMAL = 'Normal'

    List<TaigaIssue> migrateIssuesByProject(final RedmineTaigaRef ref) {
        return redmineClient
            .findAllIssueByProjectIdentifier(ref.redmineIdentifier)
            .collect(this.&populateIssue)
            .collect(this.&addRedmineIssueToTaigaProject.rcurry(ref.project))
            .collect(this.&save)
    }

    RedmineIssue populateIssue(final RedmineIssue basicIssue) {
        return redmineClient.findIssueById(basicIssue.id)
    }

    TaigaIssue addRedmineIssueToTaigaProject(
        final RedmineIssue source,
        final TaigaProject taigaProject) {

        RedmineUser user =
            redmineClient.findUserFullById(source.author.id)

        return new TaigaIssue(
            project: taigaProject,
            type: source.tracker.name,
            status: source.statusName,
            priority: source.priorityText,
            severity: SEVERITY_NORMAL,
            subject: source.subject,
            description: source.with { description ?: subject },
            createdDate: source.createdOn,
            owner: user.mail,
            attachments: extractIssueAttachments(source),
            history: extractIssueHistory(source)
        )

    }

    List<TaigaAttachment> extractIssueAttachments(final RedmineIssue issue) {
        return issue.attachments.collect(this.&convertToTaigaAttachment)
    }

    TaigaAttachment convertToTaigaAttachment(RedmineAttachment att) {
        RedmineUser user = redmineClient.findUserFullById(att.author.id)

        return new TaigaAttachment(
            data: new URL(att.contentURL).bytes.encodeBase64(),
            name: att.fileName,
            description: att.description,
            createdDate: att.createdOn,
            owner: user.mail
        )

    }

    List<TaigaHistory> extractIssueHistory(final RedmineIssue issue) {
        return issue.journals.collect(this.&convertToTaigaHistory)
    }

    TaigaHistory convertToTaigaHistory(RedmineHistory journal) {
        RedmineUser redmineUser = redmineClient.findUserFullById(journal.user.id)
        TaigaUser taigaUser =
            new TaigaUser(
                name: redmineUser.fullName,
                email: redmineUser.mail)

        return new TaigaHistory(
            user: taigaUser,
            createdAt: journal.createdOn,
            comment: journal.notes
        )
    }

    @Override
    TaigaIssue save(TaigaIssue issue) {
        return taigaClient.createIssue(issue)
    }

}