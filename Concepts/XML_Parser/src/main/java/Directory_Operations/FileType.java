package Directory_Operations;

import java.util.ArrayList;

public class FileType {
    private ArrayList files = new ArrayList();
    private ArrayList dirs = new ArrayList();
    public FileType() {
    }

    public void setFiles(ArrayList files) {
        this.files = files;
    }

    public ArrayList getFiles() {
        return files;
    }

    public void setDirs(ArrayList dirs) {
        this.dirs = dirs;
    }

    public ArrayList getDirs() {
        return dirs;
    }
}
