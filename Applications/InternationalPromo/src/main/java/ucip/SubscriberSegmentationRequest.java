package ucip;

public class SubscriberSegmentationRequest extends BasicUCIPRequest {
    public SubscriberSegmentationRequest() {
    }
    private ServiceOffering[] serviceOfferings;

    public void setServiceOfferings(ServiceOffering[] serviceOfferings) {
        this.serviceOfferings = serviceOfferings;
    }

    public ServiceOffering[] getServiceOfferings() {
        return serviceOfferings;
    }
}
