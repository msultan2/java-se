package Loggers;

public class StartTime {
    private String CurrentTime = new String();
    public StartTime() {
    }

    public void setCurrentTime(String currentTime) {
        this.CurrentTime = currentTime;
    }

    public String getCurrentTime() {
        return CurrentTime;
    }
}
