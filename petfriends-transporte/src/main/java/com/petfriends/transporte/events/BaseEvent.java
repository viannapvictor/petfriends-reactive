package com.petfriends.transporte.events;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = EntregaAgendada.class, name = "EntregaAgendada"),
    @JsonSubTypes.Type(value = TransporteIniciado.class, name = "TransporteIniciado"),
    @JsonSubTypes.Type(value = EntregaConcluida.class, name = "EntregaConcluida")
})
public class BaseEvent<T> {

    public final T id;

    public BaseEvent() {
        this.id = null;
    }

    public BaseEvent(T id) {
        this.id = id;
    }

    public T getAggregateId() {
        return id;
    }
}