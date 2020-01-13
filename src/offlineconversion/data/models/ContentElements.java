package offlineconversion.data.models;

public class ContentElements {
    String id;
    float quantity;

    public ContentElements() {}

    public ContentElements(String id, float quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }
}
