package heyoung.relay.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Enumeration;

@RestController
@Slf4j
public class ProxyController {

    @PostMapping("/proxy/**")
    public ResponseEntity<byte[]> proxy(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) byte[] body, @RequestParam(value="relayUrl") String relayUrl) throws Exception{
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(360000);
        httpRequestFactory.setConnectTimeout(360000);
        httpRequestFactory.setReadTimeout(360000);

        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);

        String originUrl = request.getRequestURI().replaceAll("/proxy", "");
        String urlStr = relayUrl+originUrl;

        URI url = new URI(urlStr);

        String originMethod = request.getMethod();
        HttpMethod method = HttpMethod.valueOf(originMethod);

        // header
        Enumeration<String> headerNames = request.getHeaderNames();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        while(headerNames.hasMoreElements()){
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);

            headers.add(headerName, headerValue);
        }

        HttpEntity<byte[]> httpEntity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, method, httpEntity, byte[].class);
    }
}
