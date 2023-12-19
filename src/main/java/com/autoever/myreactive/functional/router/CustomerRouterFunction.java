package com.autoever.myreactive.functional.router;

import com.autoever.myreactive.functional.handler.CustomerHandlerFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class CustomerRouterFunction {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(CustomerHandlerFunction handlerFunction){
        //public static <T extends ServerResponse> RouterFunction<T>
        // route(RequestPredicate predicate, HandlerFunction<T> handlerFunction) {
        //HandlerFunction의 추상 메서드 Mono<ServerResponse> handle(ServerRequest request)
        return RouterFunctions.route(GET("/router/r2customers"), handlerFunction::getCustomers)
                .andRoute(GET("/router/r2customers/{id}"), handlerFunction::getCustomer)
                .andRoute(POST("/router/r2customers"), handlerFunction::saveCustomer)
                .andRoute(PUT("/router/r2customers/{id}"), handlerFunction::updateCustomer)
                .andRoute(DELETE("/router/r2customers/{id}"), handlerFunction::deleteCustomer);

    }
}
