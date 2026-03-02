package com.oceanview.model;

public class Guest {
    private int guestId;
    private String name;
    private String address;
    private String contact;

    public Guest() {
    }

    public Guest(int guestId, String name, String address, String contact) {
        this.guestId = guestId;
        this.name = name;
        this.address = address;
        this.contact = contact;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
