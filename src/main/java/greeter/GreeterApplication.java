package greeter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.security.Principal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Shyam on 7/28/2016.
 */

@SpringBootApplication
@EnableCircuitBreaker
@RestController
@RequestMapping(value = "/services")
//@EnableHystrix
@EnableHystrixDashboard
@EnableDiscoveryClient
public class GreeterApplication {

    private static Logger log = LoggerFactory.getLogger(GreeterApplication.class);

    @Autowired
    private GreeterService greeterService;

    @RequestMapping(value = "/hello", method = RequestMethod.GET, produces = "application/json")
    public Greeting hello(@RequestParam(value = "salutation", defaultValue = "Hello") String salutation, @RequestParam(value = "name", defaultValue = "Sai") String name, Principal principal) {
        return greeterService.sayHello(salutation, name);
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET, produces = "application/json")
    public Map user(Principal principal) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", principal.getName());
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) principal;
        map.put("credentials", oAuth2Authentication.getCredentials());
        map.put("Auth2Request", oAuth2Authentication.getOAuth2Request());
        //return oAuth2Authentication.getUserAuthentication();
        map.put("Authorities", oAuth2Authentication.getAuthorities());
		map.put("Details", oAuth2Authentication.getDetails());
		map.put("Class", oAuth2Authentication.getClass());
        map.put("authentication", oAuth2Authentication.getUserAuthentication());

        return map;
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        log.info("The time is now {}", dateFormat.format(new Date()));
    }

    public static void main(String[] args) {
        log.debug("Starting Greeter Application");
        SpringApplication.run(GreeterApplication.class, args);
    }

}
