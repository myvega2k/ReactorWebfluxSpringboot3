package com.autoever.myreactive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class FluxMapFlatMapTest {
    List<MyCustomer> customerList = List.of(
            new MyCustomer("gildong", "gildong@gmail.com"),
            new MyCustomer("dooly", "dooly@gmail.com"));

    @Test
    public void transformUsingMap() {
        //public final <V> Flux<V> map(Function<? super T,? extends V> mapper)
        //Transform the items emitted by this Flux by applying a synchronous function to each item.
        Flux<MyCustomer> customerFlux = Flux.fromIterable(customerList)
                .map(customer -> new MyCustomer(customer.getName().toUpperCase(),
                                                customer.getEmail().toUpperCase()))
                .log();

        customerFlux.subscribe(System.out::println);

        StepVerifier.create(customerFlux)
                .expectNext(new MyCustomer("GILDONG","GILDONG@GMAIL.COM"))
                //.expectNextCount(1)
                .expectNext(new MyCustomer("DOOLY","DOOLY@GMAIL.COM"))
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap() {
        //public final <V> Flux<V> map(Function<? super T,? extends V> mapper)
        //public final <R> Flux<R> flatMap(Function<? super T,? extends Publisher<? extends R>> mapper)
        /*
        Transform the elements emitted by this Flux asynchronously into Publishers,
        then flatten these inner publishers into a single Flux through merging, which allow them to interleave.
        */

        Flux<MyCustomer> customerFlux = Flux.fromIterable(customerList)
                //.flatMap(customer -> Mono.just(new MyCustomer(customer.getName().toUpperCase(), customer.getEmail().toUpperCase())))
                .flatMap(getFunction())
                .log();
        customerFlux.subscribe(System.out::println);

        StepVerifier.create(customerFlux)
                .expectNext(new MyCustomer("GILDONG","GILDONG@GMAIL.COM"))
                .expectNext(new MyCustomer("DOOLY","DOOLY@GMAIL.COM"))
                .verifyComplete();
    }

    private Function<MyCustomer, Publisher<? extends MyCustomer>> getFunction() {
        return customer -> Mono.just(new MyCustomer(customer.getName().toUpperCase(),
                                                    customer.getEmail().toUpperCase()));
    }

    @Test
    public void stringMapFlatMap() {
        Flux<String> flux = Flux.fromArray(new String[]{"Tom", "Melissa", "Steven", "Megan"});

        System.out.println("Original Flux:");
        flux.subscribe(name -> System.out.print(name + " "));

        Flux<String> transformedFlux = flux.map(String::toUpperCase);
        System.out.println();
        System.out.println("New Flux:");
        transformedFlux.subscribe(name -> System.out.print(name + " "));

        Flux.fromArray(new String[]{"Tom", "Melissa", "Steven", "Megan"})
                //Flux<Mono<String>>
                .map(name -> Mono.just(name.concat(" modified")))
                        .subscribe(System.out::println);

        Flux.fromArray(new String[]{"Tom", "Melissa", "Steven", "Megan"})
                //Flux<String>
                .flatMap(name -> Mono.just(name.concat(" modified")))
                .subscribe(System.out::println);

    }

    @Test
    public void flatMapZipWithTest() {
        List<String> stringList = Arrays.asList(
                "Olivia",
                "Emma",
                "Ava",
                "Charlotte",
                "Sophia",
                "Amelia",
                "Isabella",
                "Mia",
                "Evelyn");

        Flux<Integer> range = Flux.range(1, Integer.MAX_VALUE);

        //1개의 글자로 쪼개서, sort, distinct, line번호와 낱글자 출력
        Flux.fromIterable(stringList)
                //word.split("")의 리턴타입 Array String[]
                .flatMap(word -> Flux.fromArray(word.split("")))
                .sort()  //정렬
                .distinct() //중복제거
                .zipWith(range, (word,line) -> line + "=" + word)
                .subscribe(System.out::println);
    }
}//FluxMapFlatMapTest

@Data
@AllArgsConstructor
@NoArgsConstructor
class MyCustomer {
    private int id;
    private String name;
    private String email;
    private List<String> phoneNumbers;

    public MyCustomer(String name, String email) {
        this.name = name;
        this.email = email;
    }
}//MyCustomer