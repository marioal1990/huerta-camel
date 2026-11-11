package cl.mycroft.camel.huertacamel.routes;

import java.util.Map;

import cl.mycroft.camel.huertacamel.model.Pedido;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class PedidoRestRoute extends RouteBuilder {

    @Override
    public void configure() {
        restConfiguration()
                .component("platform-http")
                .bindingMode(RestBindingMode.json);

        rest("/api/pedidos")
                .post().type(Pedido.class).to("direct:recibirPedido")
                .post("/lote").type(Pedido[].class).to("direct:recibirLote")
                .get().to("direct:listarPedidos");

        from("direct:recibirPedido")
                .routeId("recibir-pedido")
                .log("Pedido recibido de ${body.cliente}: ${body.cantidad} x ${body.producto}")
                .marshal().json()
                .convertBodyTo(String.class)
                .to(ExchangePattern.InOnly, "jms:queue:{{app.jms.cola-pedidos}}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(202))
                .setBody(constant(Map.of("estado", "RECIBIDO")));

        from("direct:listarPedidos")
                .routeId("listar-pedidos")
                .to("sql:select id, cliente, producto, cantidad, creado_en from pedidos order by id desc");

        // NUEVO: la caja grande
        from("direct:recibirLote")
                .routeId("recibir-lote")
                .process(e -> e.setProperty("total", e.getIn().getBody(Pedido[].class).length))
                .log("Lote recibido con ${exchangeProperty.total} pedidos")
                .split(body())
                .log("Pedido ${exchangeProperty.CamelSplitIndex}++ de ${exchangeProperty.CamelSplitSize}: ${body.cliente}")
                .marshal().json()
                .convertBodyTo(String.class)
                .to(ExchangePattern.InOnly, "jms:queue:{{app.jms.cola-pedidos}}")
                .end()
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(202))
                .setBody(simple("{\"estado\":\"RECIBIDO\",\"pedidos\":${exchangeProperty.total}}"))
                .unmarshal().json();
    }
}
