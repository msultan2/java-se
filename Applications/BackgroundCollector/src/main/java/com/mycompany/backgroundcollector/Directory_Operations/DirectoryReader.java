package Directory_Operations;

import java.io.File;

import java.util.ArrayList;


public class DirectoryReader {
    public FileType getDirsFiles(String directoryPath){
        FileType allDirsFiles = new FileType();
        ArrayList allFiles = new ArrayList();
        ArrayList allDirs = new ArrayList();
        
        File folder = new File(directoryPath);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
          if (listOfFiles[i].isFile()) {
            //System.out.println("File " + listOfFiles[i].getName());
            allFiles.add(listOfFiles[i].getName());
          } else if (listOfFiles[i].isDirectory()) {
            //System.out.println("Directory " + listOfFiles[i].getName());
            allDirs.add(listOfFiles[i].getName());
          }
        }
        allDirsFiles.setDirs(allDirs);
        allDirsFiles.setFiles(allFiles);
        return allDirsFiles;
        }
    public void dirExists(String dirPath){
        // Create a directory; all ancestor directories must exist
        File file = new File(dirPath);
        String string = file.separator;
        boolean success = (new File(dirPath)).mkdir();
        if (!success){
            // Directory Exists, Do nothing
        }
        else{
            success = (new File(dirPath)).mkdirs();
        }
    }
public boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }
  public static void main(String[] args) {

    File folder = new File("c:/");
    File[] listOfFiles = folder.listFiles();

    for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile()) {
        System.out.println("File " + listOfFiles[i].getName());
      } else if (listOfFiles[i].isDirectory()) {
        System.out.println("Directory " + listOfFiles[i].getName());
      }
    }
  }

} 