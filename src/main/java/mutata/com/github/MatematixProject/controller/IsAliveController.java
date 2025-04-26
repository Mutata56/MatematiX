package mutata.com.github.MatematixProject.controller;

import mutata.com.github.MatematixProject.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Контроллер для отслеживания активности пользователя.
 * <p>Обрабатывает запросы по префиксу <code>/isAlive</code> и позволяет:
 * <ul>
 *   <li>Устанавливать метку последнего отпинга пользователя (метод setAlive).</li>
 *   <li>Проверять, активен ли текущий или указанный пользователь (методы isAlive и isUserAlive).</li>
 * </ul>
 * Информация хранится через <code>UserUtils</code> в базе данных для последующего отображения статуса online/offline.
 * </p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 */
@Controller
@RequestMapping("/isAlive")
public class IsAliveController {

    /**
     * Утилита для работы с активностью пользователей (чтение/запись последнего пинга).
     */
    private final UserUtils userUtils;

    /**
     * Внедрение зависимости <code>UserUtils</code> через конструктор.
     *
     * @param userUtils сервис для управления метками активности пользователей
     */
    @Autowired
    public IsAliveController(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    /**
     * Устанавливает в базе отметку о том, что текущий пользователь активен.
     * <p>В качестве идентификатора берётся имя из контекста Spring Security.</p>
     *
     * @return <code>true</code>, если отметка успешно сохранена, <code>false</code> — иначе
     */
    @GetMapping("/setAlive")
    public @ResponseBody boolean setAlive() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userUtils.setAlive(username);
    }

    /**
     * Проверяет активность текущего пользователя.
     * <p>Вызывает метод <code>isUserAlive</code> передавая имя пользователя из контекста.</p>
     *
     * @return <code>true</code>, если пользователь активен, <code>false</code> — иначе
     */
    @GetMapping("/isAlive")
    public @ResponseBody boolean isAlive() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return isUserAlive(username);
    }

    /**
     * Проверяет активность указанного пользователя по имени.
     * <p>Определяет, был ли последний пинг пользователя в пределах заданного интервала
     * (настраиваемый в UserUtils).</p>
     *
     * @param username имя пользователя для проверки активности (опционально)
     * @return <code>true</code>, если пользователь считается активным, <code>false</code> — иначе
     */
    @GetMapping("/isUserAlive")
    public @ResponseBody boolean isUserAlive(@RequestParam(required = false) String username) {
        return userUtils.isAlive(username);
    }
}
