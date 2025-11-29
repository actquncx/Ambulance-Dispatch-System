package model;

public class Brigade {
    private int id;
    private String status; // 'free' або 'in_transit'
    private String currentAddress;

    public Brigade(int id, String status, String currentAddress) {
        this.id = id;
        this.status = status;
        this.currentAddress = currentAddress;
    }

    public int getId() { return id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCurrentAddress() { return currentAddress; }
}
