package interpromo;


import java.util.HashMap;
import java.util.Map;

public enum VoucherEnum {
    A1("A1"),
    B1("B1"),
    C1("C1"),
    Y1("Y1"),
    F1("F1");
    
    private final String token;
    private static Map<String,VoucherEnum> tokenMap;
    
    private VoucherEnum(String token){
        this.token = token;
        map(token,this);
    }
    
    private static void map(String token, VoucherEnum op){
        if (tokenMap==null) tokenMap = new HashMap<String,VoucherEnum>();
        tokenMap.put(token,op);
    }
    
    public static VoucherEnum forToken(String token){
        return tokenMap.get(token);
    }
}   
