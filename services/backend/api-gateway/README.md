# API Gateway

The API Gateway is the single entry point for all client requests. It is responsible for routing requests to the appropriate microservices. It is also responsible for authentication and authorization.

## Adding a new service

To add a new service to the API Gateway, you need to add a new route to the `application.yml` file. For example, to add a route for the `new-service`, you would add the following to the `routes` section:

```yaml
- id: new_service_route
  uri: lb://new-service
  predicates:
    - Path=/api/v1/new-service/**
  filters:
    - StripPrefix=2 #removes /api/v1 when forwarding the request
```

This will route all requests to `/api/v1/new-service/**` to the `new-service`. The `StripPrefix=2` filter will remove the `/api/v1` prefix from the request before it is forwarded to the service
