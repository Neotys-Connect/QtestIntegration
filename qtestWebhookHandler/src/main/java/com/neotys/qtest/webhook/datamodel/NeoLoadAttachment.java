package com.neotys.qtest.webhook.datamodel;

import com.neotys.qtest.api.client.model.AttachmentResource;

import org.apache.commons.io.FileUtils;
import org.threeten.bp.OffsetDateTime;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class NeoLoadAttachment {
    File file;
    String tile;
    String content_type;
    String data;

    public NeoLoadAttachment(File file, String tile, String content_type) throws IOException {
        this.file = file;
        this.tile = tile;
        this.content_type = content_type;
        byte[] fileContent = FileUtils.readFileToByteArray(file);
        this.data= Base64.getEncoder().encodeToString(fileContent);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getTile() {
        return tile;
    }

    public void setTile(String tile) {
        this.tile = tile;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public AttachmentResource toAttachment()
    {
        AttachmentResource attachmentResource=new AttachmentResource();
        attachmentResource.setContentType(this.content_type);
        attachmentResource.setCreatedDate(OffsetDateTime.now());
        attachmentResource.data(this.data);
        attachmentResource.setName(this.tile);

        return attachmentResource;
    }
}
