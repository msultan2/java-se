package sp_executer;

import java.util.ArrayList;


public class SP_Service_Parameters {
    String HostIP;
    String HostPort;
    String DB_Name;
    String UserName;
    String Password;
    String Parameters;
    String ParamterTypes;
    String SP_Name;
    public ArrayList ParameterValues=new ArrayList();

    public SP_Service_Parameters() {
    }

    public void setHostIP(String hostIP) {
        this.HostIP = hostIP;
    }

    public String getHostIP() {
        return HostIP;
    }

    public void setHostPort(String hostPort) {
        this.HostPort = hostPort;
    }

    public String getHostPort() {
        return HostPort;
    }

    public void setDB_Name(String dB_Name) {
        this.DB_Name = dB_Name;
    }

    public String getDB_Name() {
        return DB_Name;
    }

    public void setUserName(String userName) {
        this.UserName = userName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setPassword(String password) {
        this.Password = password;
    }

    public String getPassword() {
        return Password;
    }

//    public void setParameters(String parameters) {
//        this.Parameters = parameters;
//    }
//
//    public String getParameters() {
//        return Parameters;
//    }

    public void setParamterTypes(String paramterTypes) {
        this.ParamterTypes = paramterTypes;
    }

    public String getParamterTypes() {
        return ParamterTypes;
    }

    public void setSP_Name(String sP_Name) {
        this.SP_Name = sP_Name;
    }

    public String getSP_Name() {
        return SP_Name;
    }

    public void setParameters(String parameters) {
        this.Parameters = parameters;
    }

    public String getParameters() {
        return Parameters;
    }

    public void setParameterValues(ArrayList parameterValues) {
        this.ParameterValues = parameterValues;
    }

    public ArrayList getParameterValues() {
        return ParameterValues;
    }
}
