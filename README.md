# This is my submition to your challenge.

## Status

* Work in progress

## Requirements

* Java 21
* Maven

## Build

* Just run './mvnw clean install'

## Notes

* I still have to finish the Controller and e2e tests. So far, it's a WIP.
* I focused on the domain model. I used command, events and state approach. It's almost fully tested (~90%).
* For the sake of simplicity (and time to deliver), I decided to use a MultiMap from Eclipse Collections instead a
  distributed cache like KeyDb (since Redis is not OSS anymore)

Thanks!
