package com.parsingVAST.Model;

import lombok.Data;

@Data
public class TrackingEvent {

    private String eventType;  // Type of tracking event (e.g., start, complete)
    private String url;        // URL for tracking the event
}
