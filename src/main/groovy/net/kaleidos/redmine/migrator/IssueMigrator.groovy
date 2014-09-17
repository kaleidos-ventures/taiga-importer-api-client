package net.kaleidos.redmine.migrator

import groovy.transform.InheritConstructors

import groovy.util.logging.Log4j
import com.github.slugify.Slugify

import net.kaleidos.redmine.RedmineTaigaRef

import net.kaleidos.domain.User as TaigaUser
import net.kaleidos.domain.Issue as TaigaIssue
import net.kaleidos.domain.History as TaigaHistory
import net.kaleidos.domain.Membership as TaigaMembership
import net.kaleidos.domain.Attachment as TaigaAttachment

import com.taskadapter.redmineapi.bean.Issue as RedmineIssue
import com.taskadapter.redmineapi.bean.Journal as RedmineHistory
import com.taskadapter.redmineapi.bean.Attachment as RedmineAttachment

@Log4j
@InheritConstructors
class IssueMigrator extends AbstractMigrator<TaigaIssue> {

    List<TaigaIssue> migrateIssuesByProject(final RedmineTaigaRef ref) {
        return redmineClient
            .getIssuesByProjectId(ref.redmineProjectId)
            .collect(this.&populateIssue)
            .collect(this.&createTaigaIssue.rcurry(ref.project))
            .collect(this.&save)
    }

    RedmineIssue populateIssue(final RedmineIssue basicIssue) {
        return redmineClient.findIssueById(basicIssue.id)
    }

    TaigaIssue createTaigaIssue(
        final RedmineIssue source,
        final RedmineTaigaRef ref) {

        return new TaigaIssue(
            tracker: source.tracker.name,
            status: source.status.name,
            priority: source.priorityText,
            subject: source.subject,
            description: source.with { description ?: subject },
            userMail: ref.findUserEmailByRedmineId(source.author.id),
            createdDate: source.createdOn,
            project: ref.taigaProject,
            owner: ref.findUserEmailWithRedmineId(source.author.id),
            attachments: extractIssueAttachments(source, ref),
            history: extractIssueHistory(source, ref)
        )

    }

    List<TaigaAttachment> extractIssueAttachments(
        final RedmineIssue issue,
        final RedmineTaigaRef ref) {

        return issue
            .attachments
            .collect { RedmineAttachment att ->
                new TaigaAttachment(
                    data: new URL(att.contentURL).bytes.encodeBase64(),
                    name: att.fileName,
                    description: att.description,
                    createdDate: issue.createdOn,
                    owner: ref.findUserEmailByRedmineId(att.author.id)
                )
            }
    }

    List<TaigaHistory> extractIssueHistory(
        final RedmineIssue issue,
        final RedmineTaigaRef ref) {

        return issue
            .journals
            .collect { RedmineHistory journal ->
                return new TaigaHistory(
                    user:  ref.findUserByRedmineId(journal.user.id),
                    createdAt: journal.createdOn,
                    comment: journal.notes
                )
            }
    }

    @Override
    TaigaIssue save(TaigaIssue issue) {
        return taigaClient.createIssue(issue)
    }

}
