package com.autoever.myreactive.functional.handler;

import com.autoever.myreactive.entity.Customer;
import com.autoever.myreactive.repository.R2CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomerHandler {
    private final R2CustomerRepository customerRepository;

    public Mono<ServerResponse> getCustomers(ServerRequest serverRequest){
        Flux<Customer> customerFlux = customerRepository.findAll();
        return ServerResponse.ok() //BodyBuilder
                .contentType(MediaType.APPLICATION_JSON) //BodyBuilder
                .body(customerFlux, Customer.class); //Mono<ServerResponse>
    }

}
