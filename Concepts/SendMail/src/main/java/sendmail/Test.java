package sendmail;

import java.io.IOException;


public class Test {
    public Test() {
        try {
            Runtime.getRuntime().exec("cmd /c start D:\\Tools\\Applications\\CallingVB.vbs");
        } catch (IOException e) {
            // TODO
        }
    }

    public static void main(String[] args) {
        Test test = new Test();
    }
}
