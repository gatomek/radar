# Info
* FeignClient also known as Spring Cloud OpenFeign is a Declarative REST Client in Spring Boot Web Application.

# Links
* https://www.geeksforgeeks.org/springboot/how-to-make-rest-calls-using-feignclient-in-spring-boot/
* https://medium.com/@AlexanderObregon/how-spring-boot-implements-feign-clients-for-rest-apis-8a4108fa248c
* https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/

# Backlog
* turn on actuator endpoint to monitor application (prometheus, grafana)

# Minimalising the memory / processor footprints of the application
* resign from spring-boot-starter-web
* resign from parsing aircraft api response - treat response as a string
* send aircraft api response to the amqp queue 
