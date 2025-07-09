### `Discovery Service`

# Discovery Service

The Discovery Service is a Eureka server that is responsible for service registration and discovery. 
All of the microservices in the system register with the Discovery Service. 
This allows the API Gateway to discover the location of the microservices and route requests to them.

## Registering a service

To register a service with the Discovery Service, you need to add the following to the service's `application.yml` file:

```yaml
eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URI:http://eureka-user:eureka-password@localhost:8761/eureka/}
```
