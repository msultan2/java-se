package Variables;

public class MailConverterDetails {
    public String ConvertedMail;
    public String ConvertedName;
    public String UsedServiceName;
    public Integer SendBody;
    public Integer SendSubject;
    public String StartHour;
    public String StartMin;
    public String StartDay;
    Boolean isMailAttachemnt;
    public MailConverterDetails() {

    }

    public void setConvertedMail(String convertedMail) {
        this.ConvertedMail = convertedMail;
    }

    public String getConvertedMail() {
        return ConvertedMail;
    }

    public void setUsedServiceName(String usedServiceName) {
        this.UsedServiceName = usedServiceName;
    }

    public String getUsedServiceName() {
        return UsedServiceName;
    }

    public void setConvertedName(String convertedName) {
        this.ConvertedName = convertedName;
    }

    public String getConvertedName() {
        return ConvertedName;
    }

    public void setSendBody(Integer sendBody) {
        this.SendBody = sendBody;
    }

    public Integer getSendBody() {
        return SendBody;
    }

    public void setSendSubject(Integer sendSubject) {
        this.SendSubject = sendSubject;
    }

    public Integer getSendSubject() {
        return SendSubject;
    }

    public void setStartHour(String startHour) {
        this.StartHour = startHour;
    }

    public String getStartHour() {
        return StartHour;
    }

    public void setStartMin(String startMin) {
        this.StartMin = startMin;
    }

    public String getStartMin() {
        return StartMin;
    }

    public void setStartDay(String startDay) {
        this.StartDay = startDay;
    }

    public String getStartDay() {
        return StartDay;
    }

    public void setIsMailAttachemnt(Boolean isMailAttachemnt) {
        this.isMailAttachemnt = isMailAttachemnt;
    }

    public Boolean getIsMailAttachemnt() {
        return isMailAttachemnt;
    }
}

