package net.kaleidos.redmine.testdata

import com.taskadapter.redmineapi.bean.WikiPage as RedmineWikiPage
import com.taskadapter.redmineapi.bean.WikiPageDetail as RedmineWikiPageDetail

class WikiDataProvider {

    List<RedmineWikiPage> buildWikiPageSummaryList() {
        return (1..5).collect(this.&buildWikiPageSummary)
    }

    RedmineWikiPage buildWikiPageSummary(final Integer index) {
        return new RedmineWikiPage(
            title: "WikiPage [$index]",
            createdOn: new Date(),
            updatedOn: new Date()
        )
    }

    RedmineWikiPageDetail buildFullWikiPageByTitle(final String title) {
        return new RedmineWikiPageDetail(
            title: title,
            text: "En un lugar de la Mancha de cuyo nombre no quiero acordarme..."
        )
    }

}