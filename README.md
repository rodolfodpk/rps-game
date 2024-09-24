# Rock, paper and scissors game

## Requirements

* Java 21
* Maven (tested with 3.9.3)

## Running

1. On terminal,
   run:```./mvnw clean spring-boot:run -Dspring-boot.run.jvmArguments="-DDEFAULT_BOUNDED_ELASTIC_ON_VIRTUAL_THREAD=true"```
2. On browser, open: http://localhost:8080/webjars/swagger-ui/index.html
3. Then play with the swagger-ui in order to test the game

## Running load test

* Install https://grafana.com/docs/k6/latest/set-up/install-k6/
* On terminal, run: ```k6 run k6_script.js```

## Notes

* The code is fully tested (~100%).
* It has
  an [`ArchUnit`](https://www.archunit.org/use-cases) [`ArchUnitTest`](./src/test/java/com/rpsg/ArchUnitTest.java)
  to assert basic architecture
* For the sake of simplicity and time to deliver:
    * [`GameEventRepository`](./src/main/java/com/rpsg/model/GameEventRepository.java)
        * [`Implementation`](./src/main/java/com/rpsg/repository/GameEventCaffeineRepository.java) is
          using [`Caffeine`](https://github.com/ben-manes/caffeine) instead of a distributed cache
          like [`KeyDb`](https://docs.keydb.dev/).
        * API is not based on Reactor, even though it's only implementation does not block IO (RAM storage).
        * Just in case, [`GameController`](./src/main/java/com/rpsg/controller/GameController.java) is consuming
          handlers
          as a blocking API. Just in order to enable me try other repository implementation.
    * It's far from production ready: errors, distributed store, persistence, circuit breakers, observability, etc

Thanks!
