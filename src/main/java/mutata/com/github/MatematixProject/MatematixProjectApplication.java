package mutata.com.github.MatematixProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Главный класс приложения Spring Boot для проекта MatematiX.
 * <p>Запускает встроенный контейнер приложения и конфигурирует необходимые бины.</p>
 *
 * <ul>
 *   <li>{@link SpringBootApplication} — точка входа Spring Boot, автоматически настраивает компоненты.</li>
 *   <li>Параметр <code>exclude = { SecurityAutoConfiguration.class }</code> отключает стандартную автоконфигурацию безопасности Spring Security,
 *       чтобы использовать собственную {@link mutata.com.github.MatematixProject.configuration.SecurityConfiguration}.</li>
 * </ul>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 */
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class MatematixProjectApplication {

	/**
	 * Точка входа в приложение.
	 * <p>Инициализирует Spring ApplicationContext и запускает встроенный веб-сервер.</p>
	 *
	 * @param args массив аргументов командной строки (необязательный)
	 */
	public static void main(String[] args) {
		SpringApplication.run(MatematixProjectApplication.class, args);
	}

	/**
	 * Регистрация бина {@link PasswordEncoder} для шифрования паролей пользователей.
	 * <p>Используется алгоритм BCrypt с автоматически генерируемой солью.</p>
	 * <p>Объявлен как static, чтобы бобин создавался на этапе загрузки контекста конфигурации.</p>
	 *
	 * @return экземпляр {@link BCryptPasswordEncoder}
	 */
	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
