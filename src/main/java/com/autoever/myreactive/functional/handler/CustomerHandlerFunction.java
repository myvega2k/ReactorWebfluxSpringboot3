package com.autoever.myreactive.functional.handler;

import com.autoever.myreactive.entity.Customer;
import com.autoever.myreactive.exception.CustomAPIException;
import com.autoever.myreactive.repository.R2CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomerHandlerFunction {
    private final R2CustomerRepository customerRepository;

    private Mono<ServerResponse> response406 = ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).build();

    public Mono<ServerResponse> getCustomers(ServerRequest serverRequest){
        Flux<Customer> customerFlux = customerRepository.findAll();
        return ServerResponse.ok() //BodyBuilder
                .contentType(MediaType.APPLICATION_JSON) //BodyBuilder
                .body(customerFlux, Customer.class); //Mono<ServerResponse>
    }
    public Mono<ServerResponse> getCustomer(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return customerRepository.findById(id) //Mono<Customer>
                .flatMap(customer -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        //body(BodyInserter)
                        .body(BodyInserters.fromValue(customer)) //Mono<ServerResponse>
                ).switchIfEmpty(Mono.error(
                        new CustomAPIException("Customer Not Found with id " + id, HttpStatus.NOT_FOUND))
                );
    }

}
