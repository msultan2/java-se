package Collectors;

import Unix_Executer.Unix_Execute;

import java.io.IOException;


public class Test {
    public Test() {
    }

    public static void main(String[] args) {
//        Test test = new Test();
        Unix_Execute UX_Eexe=new Unix_Execute();
        try {
            UX_Eexe.ShellExecute("ls -l");
        } catch (IOException e) {
            // TODO
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            // TODO
             System.out.println(e.getMessage());
        }
    }
}
