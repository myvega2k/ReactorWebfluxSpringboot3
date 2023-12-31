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
                ).switchIfEmpty(getError(id));
    }

    public Mono<ServerResponse> saveCustomer(ServerRequest request) {
        Mono<Customer> unSavedCustomerMono = request.bodyToMono(Customer.class);
        return unSavedCustomerMono.flatMap(customer ->
                customerRepository.save(customer)
                        .flatMap(savedCustomer ->
                                ServerResponse.accepted() //202
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(savedCustomer)
                        )
        ).switchIfEmpty(response406);
    }

    public Mono<ServerResponse> updateCustomer(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        Mono<Customer> unUpdatedCustomerMono = request.bodyToMono(Customer.class);

        Mono<Customer> updatedCustomerMono = unUpdatedCustomerMono.flatMap(customer ->
                customerRepository.findById(id)
                        .flatMap(existCustomer -> {
                            existCustomer.setFirstName(customer.getFirstName());
                            existCustomer.setLastName(customer.getLastName());
                            return customerRepository.save(existCustomer);
                        })
        );
        return updatedCustomerMono.flatMap(customer ->
                ServerResponse.accepted()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(customer)
        ).switchIfEmpty(getError(id));
    }

    public Mono<ServerResponse> deleteCustomer(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return customerRepository.findById(id)
                .flatMap(existCustomer ->
                        ServerResponse.ok() //BodyBuilder
                                // BodyBuilder의 부모클래스 HeadersBuilder
                                // HeadersBuilder의 Mono<ServerResponse> build(Publisher<Void> voidPublisher)
                                .build(customerRepository.delete(existCustomer)))
                .switchIfEmpty(getError(id));
    }

    private Mono<ServerResponse> getError(Long id) {
        return Mono.error(
                new CustomAPIException("Customer Not Found with id " + id, HttpStatus.NOT_FOUND));
    }


}
