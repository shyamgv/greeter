#Greeting Microservice Sample

This application is a microservice that calls another microservice called Message-Generation. It acts as ResourceServer from the SSO implementation perspective.
Below are the different dependencies and tiles we used in this application.

    Hystrix
    Spring Boot Application
    RestController
    SSO (ResourceServer)
    Ribbon (LB)
    Register with Service Registry

To spin up a Eureka Server instance in your local pls refer to https://spring.io/blog/2015/07/14/microservices-with-spring

#Building and Deploying without using pipeline

1) Go to the project root
$ mvn clean package
2) (Local) Set the active profile as dev in local so that the application looks into application-dev.properties for configuration and then run the app using IDE or command line
Go to (Run/debug Configurations -> Select GreeterApplication -> Environment Variables)
    SPRING_PROFILES_ACTIVE=dev
$mvn spring-boot:run
3) (Cloud) On PCF - set the following environment variables
* SPRING_PROFILES_ACTIVE
* CF_TARGET

For ease all these are set using the manifest.yml. If you want to set it manually then use the command like below from CF command line.

    $ cf set-env greeter CF_TARGET https://api.sys.xxxx.xxxx.com

Create Service:
===============
Config-Server, Service Registry and SSO we need to create services in our PCF Space.

    $ cf create-service p-service-registry standard service-registry
    $ cf create-service p-circuit-breaker-dashboard standard circuit-breaker-dashboard

Bind Services
=============
Bind the created services to this application
    $ cf bind-service greeter service-registry
    $ cf bind-service greeter circuit-breaker-dashboard
    
For ease its included in the manifest.yml.
# Trying It Out

This is a microservice configured to be called usign ZuulProxy's end point which routes the request. Because this is
a ResourceServer it expects token to be given to it to be able to access the end-points in this 
Hit the below end-point:
https://zuulproxy.apps.xxx.xxxx.com/services/hello where zuulproxy is the host name given in manifest.yml and /services/hello is the end point in the greeting application.
Note: Because of the SSO implementation it would automatically redirect and get the auth code and use that to fetch the token and establish a session. When hit for the first time after deployment it will take you to Authorize screen for the permissions to be accepted. Once you authorize it would take you to the response screen.
