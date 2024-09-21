# Rock, paper and scissors game

## Status

* Work in progress

## Requirements

* Java 21
* Maven (tested with 3.9.3)

## Running

1. On terminal: ```./mvnw spring-boot:run```
2. On browser: http://localhost:8080/webjars/swagger-ui/index.html
3. Then play with the swagger-ui in order to test the game

## Notes

* I focused more on the domain model. I used command, events and state approach. It's fully tested (~100%).
* For the sake of simplicity and time to deliver:
    1. Request and responses are java records from the model package. There no distinction between requests / responses
       from model classes.
    2. I decided to use a simplistic and possible naive GameEventRepository implementation. Using MultiMap from Eclipse
       Collections instead of a distributed cache like KeyDb (since Redis is not OSS anymore)
    3. I didn't write any load test.
    4. It's far from production ready.

Thanks!
