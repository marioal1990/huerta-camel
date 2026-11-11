package cl.mycroft.camel.huertacamel.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Pedido {

    private String cliente;
    private String producto;
    private int cantidad;
}
