package JavaBean;

public class FileDescribe {
    String fileName;
    String fileType;
    Long fileSize;

    public FileDescribe(){}

    public FileDescribe(String fileName, String fileType, Long fileSize) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return "FileDescribe{" +
                "fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                '}';
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}
