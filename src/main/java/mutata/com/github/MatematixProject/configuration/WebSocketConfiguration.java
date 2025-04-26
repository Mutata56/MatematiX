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
 * Конфигурация WebSocket для приложения.
 * <p>Настраивает брокер сообщений, конечные точки (endpoints) для STOMP и
 * преобразователи сообщений в формате JSON. Обеспечивает двустороннюю связь
 * между клиентом и сервером через WebSocket.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    /**
     * Настраивает брокер сообщений STOMP с префиксами.
     *
     * @param config реестр настройки брокера сообщений
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Включаем встроенный простой брокер с префиксом /user
        config.enableSimpleBroker("/user");
        // Устанавливаем префикс назначения для методов контроллера
        config.setApplicationDestinationPrefixes("/application");
        // Префикс для отправки сообщений конкретным пользователям
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Регистрирует конечные точки STOMP для подключения клиентов.
     * <p>Добавляет endpoint "/listenToNewEvents" и разрешает fallback через SockJS
     * для поддержки клиентов, не работающих с чистым WebSocket.</p>
     *
     * @param registry реестр конечных точек STOMP
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Обычное подключение
        registry.addEndpoint("/listenToNewEvents");
        // Подключение с поддержкой SockJS (fallback для браузеров без WebSocket)
        registry.addEndpoint("/listenToNewEvents").withSockJS();
    }

    /**
     * Настраивает преобразователи сообщений для обработки JSON.
     * <p>Добавляет MappingJackson2MessageConverter с дефолтным разрешителем контента,
     * настроенным на MIME тип application/json.</p>
     *
     * @param messageConverters список доступных конвертеров сообщений, которые можно настроить
     * @return false, чтобы не переопределять конвертеры по умолчанию, а лишь добавить свой
     */
    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        // Создаем резолвер контента и устанавливаем JSON по умолчанию
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);

        // Создаем конвертер JSON и настраиваем его
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(new ObjectMapper());
        converter.setContentTypeResolver(resolver);

        // Добавляем наш конвертер в список
        messageConverters.add(converter);

        // Возвращаем false, чтобы поддержать другие конвертеры по умолчанию
        return false;
    }
}
