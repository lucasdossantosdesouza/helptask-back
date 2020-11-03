package com.helptask.entity;

public enum StatusEnum {
    New,
    Assigned,
    Resolved,
    Aproved,
    Disaproved,
    Closed;

    public static StatusEnum getStatus(String status){
        switch(status){
            case "New" : return New;
            case "Assigned" : return Assigned;
            case "Resolved" : return Resolved;
            case "Aproved" : return Aproved;
            case "Disaproved" : return Disaproved;
            case "Closed" : return Closed;
            default: return New;
        }
    }
}
