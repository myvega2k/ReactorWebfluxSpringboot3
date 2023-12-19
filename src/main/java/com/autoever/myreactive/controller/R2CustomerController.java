package com.autoever.myreactive.controller;

import com.autoever.myreactive.entity.Customer;
import com.autoever.myreactive.repository.R2CustomerRepository;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@RestController
@RequestMapping("/r2customers")
public class R2CustomerController {
    private final R2CustomerRepository customerRepository;
    private final Sinks.Many<Customer> sinksMany;

    public R2CustomerController(R2CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        //Sinks.many() => Sinks.ManySpec
        //Sinks.many().multicast() => Sinks.MulticastSpec
        //Sinks.many().multicast().onBackPressureBuffer() => Sinks.Many
        sinksMany = Sinks.many().multicast().onBackpressureBuffer();
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Customer> findAllCustomers() {
        return customerRepository.findAll().delayElements(Duration.ofSeconds(1)).log();
    }

    @GetMapping("/sse")
    public Flux<ServerSentEvent<Customer>> findAllCustomerSSE() {
        return sinksMany.asFlux()
                .mergeWith(customerRepository.findAll())
                .map(customer -> ServerSentEvent.builder(customer).build())
                .doOnCancel(() -> sinksMany.asFlux().blockLast());
    }
    @PostMapping
    public Mono<Customer> saveCustomer(@RequestBody Customer customer) {
    //tryEmitNext : Try emitting a non-null element, generating an onNext signal.
        return customerRepository.save(customer)
                .doOnNext(savedCustomer -> sinksMany.tryEmitNext(savedCustomer))
                .log();
    }
}
