package mutata.com.github.MatematixProject.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Простой REST сервис, используемый для конвертации base64String
 * Transactional(readOnly = true) - данный сервис занимается только чтением, и никак не изменяет данные БД
 */
@Service
public class MyRestService {

    private final RestTemplate restTemplate;

    @Autowired
    public MyRestService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * Конвертация картинок в формат web64
     * @param fileExtension - исходное расширение файла
     * @param base64String - строка в формате base64
     * @return изображение в формате webp64
     */
    public String convertToWebp(String fileExtension,String base64String) {
        String url = "https://webp.phip1611.dev/convert";

        // Создание хедеров
        HttpHeaders headers = new HttpHeaders();
        // Установка хедера "conent-type"
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Установка хедера "accept"
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Создание хеш карты для параметров
        Map<String, Object> map = new HashMap<>();
        map.put("fileExtension", fileExtension);
        map.put("base64String", base64String);
        // Создание запроса
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        // Отправка POST запроса
        ResponseEntity<String> response = this.restTemplate.postForEntity(url, entity, String.class);

        return response.getBody();
    }

}
