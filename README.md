# Rock, paper and scissors game

## Hazelcast branch

* **Status**: experimental

## Requirements

* Java 21
* Maven (tested with 3.9.3)

## Running

1. On terminal, run:
    ```bash
        ./mvnw clean spring-boot:run -Dspring-boot.run.jvmArguments="-DDEFAULT_BOUNDED_ELASTIC_ON_VIRTUAL_THREAD=true"
    ``` 
2. On browser, open: http://localhost:8080/webjars/swagger-ui/index.html
3. Then play with the swagger-ui in order to test the game

## Notes

* This is an experiment using a Hazelcast not only as a cache, but
  using [`Hazelcast EntryProcessor`](https://docs.hazelcast.org/docs/4.0/javadoc/com/hazelcast/map/EntryProcessor.html)
* docker-compose.yml works but Hazelcast keeps dying until the last app instance (using embedded hazelcast)
* kubernetes files are still very early

Thanks!
