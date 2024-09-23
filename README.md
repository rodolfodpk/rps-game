# Rock, paper and scissors game

## Status

* Work in progress

## Requirements

* Java 21
* Maven (tested with 3.9.3)

## Running

1. On terminal, run: ```./mvnw spring-boot:run```
2. On browser, open: http://localhost:8080/webjars/swagger-ui/index.html
3. Then play with the swagger-ui in order to test the game

# Running load test

* Install https://grafana.com/docs/k6/latest/set-up/install-k6/
* On terminal, run: ```k6 run k6_script.js```

## Notes

* The domain model is fully tested (~100%).
* For the sake of simplicity and time to deliver:
    1. GameState is a model class, but also a controller response.
    2. I decided to use a simplistic and possible naive GameEventRepository implementation. Using MultiMap from Eclipse
       Collections instead of a distributed cache like KeyDb (since Redis is not OSS anymore)
    3. It's far from production ready.

Thanks!
