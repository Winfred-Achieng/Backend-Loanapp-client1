package com.winfred.loan_app.config;

import com.winfred.loan_app.model.SmsRequestModel;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;


@Component
public class SmsHelper {

    @Autowired
    Environment env;

    @Value("${africastalking.apiKey}")
    String sms_key;
    @Value("${africastalking.username}")
    String sms_username;
    @Value("${SMS_URL}")
    String url;

    public String sendSms(SmsRequestModel payload){
        final Logger log= LoggerFactory.getLogger(SmsHelper.class);
        try{
            TrustStrategy acceptingTrustStrategy = (x509Certificates, s) -> true;
            SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf)
                    .disableRedirectHandling()
                    .build();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);
            RestTemplate template =new RestTemplate(requestFactory);
            HttpHeaders headers = new HttpHeaders();
            headers.add("apikey",sms_key);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("username",sms_username);
            map.add("to",payload.getPhoneNumber());
            map.add("message",payload.getMessage());
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
            ResponseEntity<String> response =
                    template.exchange(url, HttpMethod.POST, entity, String.class);
            log.info("get the sms response"+response.getBody());
            return response.getBody();

        }catch(Exception el){
            el.printStackTrace();
            return  el.getMessage();
        }


    }
}
