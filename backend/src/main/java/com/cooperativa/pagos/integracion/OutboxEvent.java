package com.cooperativa.pagos.integracion;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name="outbox_events")
public class OutboxEvent {
    @Id private UUID id;
    private UUID aggregateId;
    private String eventType;
    @Column(columnDefinition="text") private String payload;
    private boolean published;
    private OffsetDateTime createdAt;
    private OffsetDateTime publishedAt;

    public static OutboxEvent of(UUID aggregateId, String type, String payload) {
        OutboxEvent e=new OutboxEvent();
        e.id=UUID.randomUUID(); e.aggregateId=aggregateId; e.eventType=type; e.payload=payload;
        e.published=false; e.createdAt=OffsetDateTime.now(); return e;
    }
    public UUID getId(){return id;} public UUID getAggregateId(){return aggregateId;}
    public String getEventType(){return eventType;} public String getPayload(){return payload;}
    public boolean isPublished(){return published;}
    public void markPublished(){published=true; publishedAt=OffsetDateTime.now();}
}
