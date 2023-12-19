package com.autoever.myreactive.functional.router;

import com.autoever.myreactive.functional.handler.CustomerHandlerFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class CustomerRouterFunction {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(CustomerHandlerFunction handlerFunction){
        //public static <T extends ServerResponse> RouterFunction<T>
        // route(RequestPredicate predicate, HandlerFunction<T> handlerFunction) {
        //HandlerFunction의 추상메서드 Mono<ServerResponse> handle(ServerRequest request)
        return RouterFunctions.route(GET("/router/r2customers"), handlerFunction::getCustomers) ;

    }
}