package nhanhvn.data.services;

import java.util.LinkedHashMap;
import java.util.Map;

public class AbstractService {
    protected final String ICPP = "icpp";
    protected final String PAGE = "page";
    private final String MAX_PRODUCT = "100";
    Map<String, Object> dataMap;

    public AbstractService() {
        dataMap = new LinkedHashMap<>();
        dataMap.put(ICPP, MAX_PRODUCT);
    }
}
