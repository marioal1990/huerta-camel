package cl.mycroft.camel.huertacamel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class HolaRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("timer:hola?period={{app.hola.periodo}}")
                .routeId("hola")
                .setBody(simple("Hola desde Camel ${date:now:HH:mm:ss}"))
                .log("${body}");
    }
}
