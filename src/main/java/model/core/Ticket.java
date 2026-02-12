package model.core;

import java.time.LocalDateTime;

public class Ticket {
    private String ticketId;
    private String plate;
    private String spotId;
    private LocalDateTime entryTime;

    public Ticket(String ticketId, String plate, String spotId, LocalDateTime entryTime) {
        this.ticketId = ticketId;
        this.plate = plate;
        this.spotId = spotId;
        this.entryTime = entryTime;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getSpotId() {
        return spotId;
    }

    public void setSpotId(String spotId) {
        this.spotId = spotId;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }
}