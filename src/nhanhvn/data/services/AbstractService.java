package nhanhvn.data.services;

import java.util.HashMap;
import java.util.Map;

public class AbstractService {
    protected final String ICPP = "icpp";
    protected final String PAGE = "page";
    private final String MAX_PRODUCT = "100";
    Map<String, String> dataMap;

    public AbstractService() {
        dataMap = new HashMap<>();
    }
}
