# Rock, paper and scissors game

## Status

* Work in progress

## Requirements

* Java 21
* Maven (tested with 3.9.3)

## Test

* Just run ```./mvnw clean verify```

## Notes

* I still have to finish the API, Controller and E2E tests. So far, it's a WIP.
* I focused on the domain model. I used command, events and state approach. It's fully tested (~100%).
* For the sake of simplicity (and time to deliver), I decided to use a MultiMap from Eclipse Collections instead of a
  distributed cache like KeyDb (since Redis is not OSS anymore)

Thanks!
