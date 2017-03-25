package OneStream;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    SongRepository songRepository;
    @Autowired
    PlaylistRepository playlistRepository;

    public static void main(String[] args) {
        System.setProperty("http.keepAlive", "false");
        SpringApplication.run(Application.class, args);
    }


    // CORS
    @Bean
    FilterRegistrationBean corsFilter(
            @Value("${tagit.origin:https://api-7328501912465276845-942591.appspot.com}") String origin) {
        return new FilterRegistrationBean(new Filter() {
            @Override
            public void init(FilterConfig filterConfig) throws ServletException {}
            public void doFilter(ServletRequest req, ServletResponse res,
                                 FilterChain chain) throws IOException, ServletException {
                HttpServletRequest request = (HttpServletRequest) req;
                HttpServletResponse response = (HttpServletResponse) res;
                String method = request.getMethod();
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Methods",
                        "POST,GET,PUT,DELETE");
                response.setHeader(
                        "Access-Control-Allow-Headers",
                        "*");
                if ("OPTIONS".equals(method)) {
                    response.setStatus(HttpStatus.OK.value());
                }
                else {
                    chain.doFilter(req, res);
                }
            }
            @Override
            public void destroy() {}
        });
    }

    @Override
    public void run(String... strings) throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/META-INF/application-context.xml");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(httpRequestFactory());
    }

    public HttpComponentsClientHttpRequestFactory httpRequestFactory() {
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient());
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(5000);
        return requestFactory;
    }

    public HttpClient httpClient() {
        HttpClientBuilder httpClient = HttpClients.custom().useSystemProperties();
        httpClient.setConnectionReuseStrategy(NoConnectionReuseStrategy.INSTANCE);
        return httpClient.build();
    }
}

