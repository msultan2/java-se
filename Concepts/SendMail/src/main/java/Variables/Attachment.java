package Variables;

public class Attachment {
    String AttachmentPath;
    Boolean isFound;
    Boolean DownLoadAttachmentMail;
    public Attachment() {

    }

    public void setAttachmentPath(String attachmentPath) {
        this.AttachmentPath = attachmentPath;
    }

    public String getAttachmentPath() {
        return AttachmentPath;
    }

    public void setIsFound(Boolean isFound) {
        this.isFound = isFound;
    }

    public Boolean getIsFound() {
        return isFound;
    }

    public void setDownLoadAttachmentMail(Boolean downLoadAttachmentMail) {
        this.DownLoadAttachmentMail = downLoadAttachmentMail;
    }

    public Boolean getDownLoadAttachmentMail() {
        return DownLoadAttachmentMail;
    }
}
