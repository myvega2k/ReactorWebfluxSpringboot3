package com.autoever.myreactive.functional.router;

import com.autoever.myreactive.entity.Customer;
import com.autoever.myreactive.functional.handler.CustomerHandlerFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class CustomerRouterFunction {

    @RouterOperations({
            @RouterOperation(path = "/router/r2customers", method = RequestMethod.GET,
                    beanClass = CustomerHandlerFunction.class, beanMethod = "getCustomers"),
            @RouterOperation(path = "/router/r2customers/{id}", method = RequestMethod.GET,
                    beanClass = CustomerHandlerFunction.class, beanMethod = "getCustomer",
                    operation = @Operation(operationId = "getCustomer",
                            parameters = {@Parameter(in = ParameterIn.PATH, name = "id")})
            ),
            @RouterOperation(path = "/router/r2customers", method = RequestMethod.POST,
                    beanClass = CustomerHandlerFunction.class, beanMethod = "saveCustomer",
                    operation = @Operation(operationId = "saveCustomer",
                            requestBody = @RequestBody(content =
                            @Content(schema = @Schema(implementation = Customer.class))))
            ),
            @RouterOperation(path = "/router/r2customers/{id}", method = RequestMethod.PUT,
                    beanClass = CustomerHandlerFunction.class, beanMethod = "updateCustomer",
                    operation = @Operation(operationId = "updateCustomer",
                            parameters = {@Parameter(in = ParameterIn.PATH, name = "id")},
                            requestBody = @RequestBody(content =
                            @Content(schema = @Schema(implementation = Customer.class))))
            ),
            @RouterOperation(path = "/router/r2customers/{id}", method = RequestMethod.DELETE,
                    beanClass = CustomerHandlerFunction.class, beanMethod = "deleteCustomer",
                    operation = @Operation(operationId = "deleteCustomer",
                            parameters = {@Parameter(in = ParameterIn.PATH, name = "id")})
            )
    })
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
