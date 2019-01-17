# websocket-sleuth-test

Failing test showing Cloud Sleuth issue with Spring Boot 2.1.1 and Stomp-based web sockets. This issue did not exist with Spring Boot 2.0.5.

Run the tests using:

    ./gradlew clean build --info

This show the following error:

    2019-01-17 10:01:14.876 ERROR [websocket-sleuth-test,86b7bbc97c2ae296,14d3361a3e31673e,false] 6022 --- [    Test worker] o.s.m.s.b.SimpleBrokerMessageHandler     : Failed to send GenericMessage [payload=byte[16], headers={spanTraceId=86b7bbc97c2ae296, spanId=86b7bbc97c2ae296, simpMessageType=MESSAGE, nativeHeaders={spanTraceId=[86b7bbc97c2ae296], spanId=[86b7bbc97c2ae296], spanSampled=[0]}, spanSampled=0, contentType=application/json;charset=UTF-8, simpDestination=/topic/foo}]
    2019-01-17 10:01:14.877 ERROR [websocket-sleuth-test,86b7bbc97c2ae296,86b7bbc97c2ae296,false] 6022 --- [    Test worker] o.s.m.s.ExecutorSubscribableChannel      : Exception from afterMessageHandled in org.springframework.cloud.sleuth.instrument.messaging.TracingChannelInterceptor@3d415f8

Commenting out or removing the 
`spring-cloud-starter-sleuth` dependency from `build.gradle` allows the test to pass.
