package com.autoever.myreactive.functional.router;

import com.autoever.myreactive.functional.handler.CustomerHandlerFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CustomerRouterFunction {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(CustomerHandlerFunction handlerFunction){

    }
}
