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

* I focused on the domain model. I used command, events and state approach. It's fully tested (~100%).
* For the sake of simplicity (and time to deliver), I decided to use a MultiMap from Eclipse Collections instead of a
  distributed cache like KeyDb (since Redis is not OSS anymore)

Thanks!
