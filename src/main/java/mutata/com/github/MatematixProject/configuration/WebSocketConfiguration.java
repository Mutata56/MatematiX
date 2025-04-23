package mutata.com.github.MatematixProject.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;
/**
 * Класс конфигурации вебсокета веб-сайта. Используется для обмена данными в реальном времени. Обеспечивает двустороннюю связь между клиентом и сервером, используя одно TCP соединение.
 * @author Khaliullin Cyrill
 * @version 1.0.0
 *
 * Configuration - данный класс является классом конфигурации.
 * EnableWebSocketMessageBroker - включение webSocket в системе Spring.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {


    /**
     * Метод конфигурации STOMP.
     * @param config - конфигурация webSocket.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/user"); // prefix
        config.setApplicationDestinationPrefixes("/application"); // prefix
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Метод конфигурации STOMP (Задача конечных точек, где webSocket будет мониторить запросы).
     * SockJs — это JavaScript библиотека, которая обеспечивает двусторонний междоменный канал связи между клиентом и сервером.
     * Другими словами SockJs имитирует WebSocket API
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/listenToNewEvents");
        registry.addEndpoint("/listenToNewEvents").withSockJS();
    }

    /**
     * Метод для настройки преобразователя сообщений в контексте Spring MVC. Создание и установка дефолтных резолвера и преобразователя сообщений в формате JSON.
      * @param messageConverters - сообщения в контексте Spring MVC
     * @return false - указывает, что преобразователи по умолчанию НЕ НУЖНО переопределять
     */

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        // Установка дефолтного резолвера
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        // Установка дефолтного MimeType на JSON
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        // Преобразователь сообщений для сообщений в формате JSON
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        // Конфигурация нашего преобразователя
        converter.setObjectMapper(new ObjectMapper());
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);
        return false;
    }
}
