# websocket-sleuth-test

Failing test showing Cloud Sleuth issue with Spring Boot 2.1.1 and Stomp-based web sockets. Commenting out or removing the 
`spring-cloud-starter-sleuth` dependency from `build.gradle` allows the test to pass. This issue did not exist with Spring Boot 2.0.5
