package net.kaleidos.taiga.testdata

import net.kaleidos.domain.Attachment

trait AttachmentData {

    Attachment buildBasicAttachment(String filename, String owner) {
        def fileBase64 = new File("src/test/resources/${filename}").bytes.encodeBase64().toString()

        return new Attachment(name: filename, data: fileBase64, owner: owner)
    }

    Attachment buildAttachmentWithOptionalData(String description, Date createdDate) {
        this.buildBasicAttachment('tux.png', 'admin@admin.com')
            .setDescription(description)
            .setCreatedDate(createdDate)
    }
}