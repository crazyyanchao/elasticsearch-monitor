package casia.isi.elasticsearch.monitor.entity;

import org.springframework.core.io.FileSystemResource;

public class MailBean {

    private String subject;//主题

    private String content;//内容

    private String receiver;//接收者,以分号;间隔

    private String attachment;//附件名，以|分隔

    private FileSystemResource file;//附件

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public FileSystemResource getFile() {
        return file;
    }

    public void setFile(FileSystemResource file) {
        this.file = file;
    }
}
