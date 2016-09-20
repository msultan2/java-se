package xml_parser;

// File:  io/readwrite/CopyTextFile.java
// Description: Example to show text file reading and writing.
// Author: Fred Swartz
// Date  : February 2006

import Date_Functions.DateUtils;

import java.io.*;

import java.util.*;

public class CopyTextFile {

    public static void main(String[] args) {
        //... Get two file names from use.
        System.out.println("Enter a filepath to copy from, and one to copy to.");
        Scanner in = new Scanner(System.in);

        //... Create File objects.
        File inFile = new File(in.next()); // File to read from.
        File outFile = new File(in.next()); // File to write to

        //... Enclose in try..catch because of possible io exceptions.
        try {
            copyFile(inFile, outFile);

        } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        }
    }


    //=============================================================== copyFile
    // Uses BufferedReader for file input.

    public static void copyFile(File fromFile, 
                                File toFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fromFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(toFile));

        //... Loop as long as there are input lines.
        String line = null;
        while ((line = reader.readLine()) != null) {
            writer.write(line);
            writer.newLine(); // Write system dependent end of line.
        }

        //... Close reader and writer.
        reader.close(); // Close to unlock.
        writer.close(); // Close to unlock and flush to disk.
    }
    public void log(String text, Boolean append) {
        File currentPath = new File(System.getProperty("user.dir"));
        String pathSeparator = currentPath.separator;
        DateUtils dateUtils = new DateUtils();
        try {
            FileWriter fstream = 
                new FileWriter(currentPath + pathSeparator + "Log.txt", 
                               append);
            BufferedWriter out = new BufferedWriter(fstream);

            out.write(dateUtils.now("yyyy-MM-dd HH:mm:ss") + "   " + text);
            out.newLine();

            out.close();
        } catch (IOException e) { //Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

    }
    public static void fileInsertText(String filePath, String text, 
                                      Boolean append) {
        try {
            FileWriter fstream = new FileWriter(filePath, append);
            BufferedWriter out = new BufferedWriter(fstream);

            out.write(text);
            out.newLine();

            out.close();
        } catch (IOException e) { //Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

    }
    //=============================================================== copyFile2
    // Uses Scanner for file input.

    public static void copyFileWithoutString(String fromFile, String toFile, 
                                             String lineStringToEluminate) {

        try {
            //Scanner freader = new Scanner(fromFile);
            //BufferedReader freader = new BufferedReader(new File(fromFile));
            FileInputStream fstream = new FileInputStream(fromFile);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            BufferedWriter writer = new BufferedWriter(new FileWriter(toFile));

            //... Loop as long as there are input lines.
            String line = null;
            //while (freader.hasNextLine()) {
             while ((line = br.readLine()) != null)   {
                //line = freader.nextLine();
                //if (line.compareToIgnoreCase(lineStringToEluminate) != 0) {
                if (!line.contains(lineStringToEluminate)) {
                    writer.write(line);
                    writer.newLine(); // Write system dependent end of line.
                }
            }

            //... Close reader and writer.
            //freader.close(); // Close to unlock.
            in.close();
            writer.close(); // Close to unlock and flush to disk.
        } catch (IOException e) { //Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
}
