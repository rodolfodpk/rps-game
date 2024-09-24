# Rock, paper and scissors game

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

## Running load test

* Install https://grafana.com/docs/k6/latest/set-up/install-k6/
* On terminal, run:
    ```bash
        k6 run k6_script.js
    ```
   Then you will see something like:
```

          /\      |‾‾| /‾‾/   /‾‾/   
     /\  /  \     |  |/  /   /  /    
    /  \/    \    |     (   /   ‾‾\  
   /          \   |  |\  \ |  (‾)  | 
  / __________ \  |__| \__\ \_____/ .io

  execution: local
     script: k6_script.js
     output: -

  scenarios: (100.00%) 1 scenario, 1000 max VUs, 2m45s max duration (incl. graceful stop):
           * default: Up to 1000 looping VUs for 2m15s over 4 stages (gracefulRampDown: 30s, gracefulStop: 30s)


running (2m15.6s), 0000/1000 VUs, 496224 complete and 0 interrupted iterations
default ✓ [======================================] 0000/1000 VUs  2m15s

     ✓ Game started successfully
     ✓ Move made successfully
     ✓ Game ended successfully

     checks.........................: 100.00% ✓ 1488672      ✗ 0      
     data_received..................: 309 MB  2.3 MB/s
     data_sent......................: 219 MB  1.6 MB/s
     http_req_blocked...............: avg=14.46µs  min=0s       med=1µs    max=237.89ms p(90)=2µs      p(95)=4µs     
     http_req_connecting............: avg=10.4µs   min=0s       med=0s     max=236.56ms p(90)=0s       p(95)=0s      
     http_req_duration..............: avg=7.21ms   min=115µs    med=767µs  max=625.44ms p(90)=12.12ms  p(95)=33.63ms 
       { expected_response:true }...: avg=7.21ms   min=115µs    med=767µs  max=625.44ms p(90)=12.12ms  p(95)=33.63ms 
     http_req_failed................: 0.00%   ✓ 0            ✗ 1488672
     http_req_receiving.............: avg=150.53µs min=2µs      med=7µs    max=565.15ms p(90)=25µs     p(95)=51µs    
     http_req_sending...............: avg=35.58µs  min=1µs      med=3µs    max=587.98ms p(90)=10µs     p(95)=19µs    
     http_req_tls_handshaking.......: avg=0s       min=0s       med=0s     max=0s       p(90)=0s       p(95)=0s      
     http_req_waiting...............: avg=7.03ms   min=107µs    med=749µs  max=539.29ms p(90)=11.63ms  p(95)=32.13ms 
     http_reqs......................: 1488672 10979.458387/s
     iteration_duration.............: avg=73.89ms  min=446.54µs med=2.92ms max=2.56s    p(90)=151.34ms p(95)=546.66ms
     iterations.....................: 496224  3659.819462/s
     vus............................: 999     min=1          max=999  
     vus_max........................: 1000    min=1000       max=1000 
```

## Notes

* The code is fully tested (~100%).
* It has
  an [`ArchUnit`](https://www.archunit.org/use-cases) [`ArchUnitTest`](./src/test/java/com/rpsg/ArchUnitTest.java) to
  assert basic architecture
* It has a [`GameE2ETest`](./src/test/java/com/rpsg/GameE2ETest.java) to assert basic API features
* It has a [`K6`](https://k6.io/) [`Load test`](./k6_script.js)
* For the sake of simplicity and time to deliver:
    * Ideally, I should have representations classes (API responses) for [`GameEvent`](./src/main/java/com/rpsg/model/GameEvent.java) events.  
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
