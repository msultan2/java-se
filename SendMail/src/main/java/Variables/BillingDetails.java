package Variables;

public class BillingDetails {
    String BC="00";
    String Type="XXX";
    String Target="0";
    String Actual="0";
    String Comment="XXX";
    Integer Pointer=0;
    public BillingDetails() {
    }

    public void setBC(String bC) {
        this.BC = bC;
    }

    public String getBC() {
        return BC;
    }

    public void setType(String type) {
        this.Type = type;
    }

    public String getType() {
        return Type;
    }

    public void setTarget(String target) {
        this.Target = target;
    }

    public String getTarget() {
        return Target;
    }

    public void setActual(String actual) {
        this.Actual = actual;
    }

    public String getActual() {
        return Actual;
    }

    public void setComment(String comment) {
        this.Comment = comment;
    }

    public String getComment() {
        return Comment;
    }

    public void setPointer(Integer pointer) {
        this.Pointer = pointer;
    }

    public Integer getPointer() {
        return Pointer;
    }
}

