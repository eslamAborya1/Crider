package com.NTG.Cridir.Websocket;

public class ProviderLocationMessage {
    private Long providerId;
    private double lat;
    private double lng;

    // Getters & Setters
    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }
}
