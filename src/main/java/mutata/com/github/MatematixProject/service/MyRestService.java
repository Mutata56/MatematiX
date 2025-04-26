package mutata.com.github.MatematixProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для взаимодействия с внешним REST API,
 * выполняющий конвертацию изображений из Base64 строки
 * в формат WebP.
 * <p>Использует {@link RestTemplate} для отправки HTTP-запросов.</p>
 *
 * <p>Аннотация {@link Service} делает класс доступным
 * для автоматического обнаружения Spring и внедрения
 * зависимостей.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see RestTemplate
 */
@Service
public class MyRestService {

    /**
     * Компонент Spring для выполнения HTTP-запросов.
     */
    private final RestTemplate restTemplate;

    /**
     * Конструктор для создания RestTemplate через билдера.
     *
     * @param restTemplateBuilder билд для конфигурации RestTemplate
     */
    @Autowired
    public MyRestService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * Отправляет POST-запрос к внешнему API для конвертации
     * изображения из Base64 в WebP.
     *
     * @param fileExtension исходное расширение файла (например, "png", "jpg")
     * @param base64String  строка изображения в формате Base64
     * @return строка ответа API, содержащая конвертированный
     *         Base64-контент WebP
     * @throws org.springframework.web.client.RestClientException
     *         в случае ошибок HTTP-запроса
     */
    public String convertToWebp(String fileExtension, String base64String) {
        String url = "https://webp.phip1611.dev/convert";

        // Заголовки запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Тело запроса с параметрами
        Map<String, Object> map = new HashMap<>();
        map.put("fileExtension", fileExtension);
        map.put("base64String", base64String);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        // Выполнение POST-запроса
        ResponseEntity<String> response =
                this.restTemplate.postForEntity(url, entity, String.class);

        return response.getBody();
    }
}