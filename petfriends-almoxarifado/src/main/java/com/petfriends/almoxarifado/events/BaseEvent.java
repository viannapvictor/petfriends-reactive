package com.petfriends.almoxarifado.events;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = EstoqueReservado.class, name = "EstoqueReservado"),
    @JsonSubTypes.Type(value = EstoqueInsuficiente.class, name = "EstoqueInsuficiente"),
    @JsonSubTypes.Type(value = ReservaConfirmada.class, name = "ReservaConfirmada"),
    @JsonSubTypes.Type(value = ReservaCancelada.class, name = "ReservaCancelada"),
    @JsonSubTypes.Type(value = ItensSeparados.class, name = "ItensSeparados")
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