package cl.mycroft.camel.huertacamel.routes;

import cl.mycroft.camel.huertacamel.model.Pedido;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class PedidoMensajeriaRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("jms:queue:{{app.jms.cola-pedidos}}")
                .routeId("jms-a-kafka")
                .log("JMS → Kafka: ${body}")
                .to("kafka:{{app.kafka.topico-pedidos}}");

        from("kafka:{{app.kafka.topico-pedidos}}?groupId=pedidos-db")
                .routeId("kafka-a-postgres")
                .unmarshal().json(JsonLibrary.Jackson, Pedido.class)
                .to("sql:insert into pedidos (cliente, producto, cantidad) "
                        + "values (:#${body.cliente}, :#${body.producto}, :#${body.cantidad})")
                .log("Pedido guardado en PostgreSQL");
    }
}
