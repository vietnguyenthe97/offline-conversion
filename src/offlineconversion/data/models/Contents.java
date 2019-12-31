package offlineconversion.data.models;

import java.util.ArrayList;
import java.util.List;

public class Contents {
    List<ContentElements> contentElements;

    public Contents() {
        contentElements = new ArrayList<>();
    }

    public List<ContentElements> getContentElements() {
        return contentElements;
    }

    public void setContentElements(List<ContentElements> contentElements) {
        this.contentElements = contentElements;
    }
}
