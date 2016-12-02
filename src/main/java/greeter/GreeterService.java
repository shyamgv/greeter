package greeter;


import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Created by Shyam on 7/28/2016.
 */

@Service
@RestController
public class GreeterService {

     @Bean(name = "GreeterRestTemplate")
     @LoadBalanced
     public RestTemplate restTemplate() {
         RestTemplate restTemplate = new RestTemplate();
         return restTemplate;
     }
    @Autowired
    @LoadBalanced
    @Qualifier("GreeterRestTemplate")
    protected RestTemplate restTemplate;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @HystrixCommand(fallbackMethod = "fallbackHello")
    public Greeting sayHello(String salutation, String name) {

        URI uri = UriComponentsBuilder.fromUriString("http://MESSAGE-GENERATION"+"/services/greetings")
                .queryParam("salutation", salutation)
                .queryParam("name", name)
                .build()
                .toUri();

        Greeting greeting = restTemplate.getForObject(uri, Greeting.class);

        return new Greeting(greeting.getMessage(),"success");
    }

    private Greeting fallbackHello(String salutation, String name) {
        Greeting greeting = new Greeting("salutation " + salutation + "name " + name,"Fallback");
        return greeting;
    }
}
