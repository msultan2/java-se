package ucip;

public class ServiceOffering {
    public ServiceOffering() {
    }
    private Integer serviceOfferingID;
    private Boolean serviceOfferingActiveFlag;


    public void setServiceOfferingID(Integer serviceOfferingID) {
        this.serviceOfferingID = serviceOfferingID;
    }

    public Integer getServiceOfferingID() {
        return serviceOfferingID;
    }

    public void setServiceOfferingActiveFlag(Boolean serviceOfferingActiveFlag) {
        this.serviceOfferingActiveFlag = serviceOfferingActiveFlag;
    }

    public Boolean getServiceOfferingActiveFlag() {
        return serviceOfferingActiveFlag;
    }
}
