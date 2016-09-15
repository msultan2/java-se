package Unix_Executer;

import java.io.IOException;
import java.io.InputStreamReader;


public class Unix_Execute {
  static public void Execute (String Command) {
    try {

        System.out.println("Executing "+Command); 
        
      Process p = Runtime.getRuntime().exec (Command);
      p.waitFor ();
      System.out.println ("Done.");      
      
    }
    catch (Exception e) {
      System.out.println ("Err: " + e.getMessage());
    }
  }
  static public void  ShellExecute(String Command) throws InterruptedException, IOException {

        System.out.println("Executing "+Command);
            // Get runtime
            java.lang.Runtime rt = java.lang.Runtime.getRuntime();
            // Start a new process: UNIX command ls
            java.lang.Process p = rt.exec(Command);
            // You can or maybe should wait for the process to complete
            p.waitFor();
//            System.out.println("Process exited with code = " + rt.exitValue());
            // Get process' output: its InputStream
            java.io.InputStream is = p.getInputStream();
            java.io.BufferedReader reader = new java.io.BufferedReader(new InputStreamReader(is));
            // And print each line
            String s = null;
            while ((s = reader.readLine()) != null) {
                System.out.println(s);
            }
            is.close();
        System.out.println ("Done.");
    }
    }