package com.autoever.myreactive.controller;

import com.autoever.myreactive.entity.Customer;
import com.autoever.myreactive.exception.CustomAPIException;
import com.autoever.myreactive.repository.R2CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
                //.doOnNext(savedCustomer -> sinksMany.tryEmitNext(savedCustomer))
                .doOnNext(sinksMany::tryEmitNext)
                .log();
    }

    @GetMapping("/{id}")
    public Mono<Customer> findCustomerById(@PathVariable Long id) {
        return customerRepository.findById(id) //Mono<Customer>
                .switchIfEmpty(Mono.error(
                                new CustomAPIException("Customer Not Found with id " + id, HttpStatus.NOT_FOUND)
                        )
                );
    }
    @GetMapping("/name/{lastName}")
    public Flux<Customer> findCustomerByName(@PathVariable String lastName){
        return customerRepository.findByLastName(lastName)
                .switchIfEmpty(Mono.error(
                        new CustomAPIException("Customer Not Found with lastName " + lastName, HttpStatus.NOT_FOUND)
                ));
    }

    @PutMapping("/{id}")
    public Mono<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customer){
        Mono<Customer> customerMono = customerRepository.findById(id)
                .flatMap(existCust -> {
                    existCust.setFirstName(customer.getFirstName());
                    existCust.setLastName(customer.getLastName());
                    return customerRepository.save(existCust);
                });
        return customerMono.switchIfEmpty(Mono.error(
                new CustomAPIException("Customer Not Found with id " + id, HttpStatus.NOT_FOUND)
        ));
    }
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCustomer(@PathVariable Long id) {
        return customerRepository.findById(id)
                .flatMap(existCust ->
                        customerRepository.delete(existCust)
                                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
