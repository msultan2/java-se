package drs;

public class Statements {
    private String insertStatement = new String();
    private String updateStatement = new String();
    private String LoadDataInFile = new String();
    public Statements() {
    }

    public void setInsertStatement(String insertStatement) {
        this.insertStatement = insertStatement;
    }

    public String getInsertStatement() {
        return insertStatement;
    }

    public void setUpdateStatement(String updateStatement) {
        this.updateStatement = updateStatement;
    }

    public String getUpdateStatement() {
        return updateStatement;
    }

    public void setLoadDataInFile(String loadDataInFile) {
        this.LoadDataInFile = loadDataInFile;
    }

    public String getLoadDataInFile() {
        return LoadDataInFile;
    }
}

