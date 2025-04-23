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
 * Контроллер, нужный для отслеживания активности пользователя (находится ли он в сети?). Данный контроллер перехватывает все запросы по адресу "/isAlive" и устанавливает соотв. значение в БД для отображения активности пользователя.
 * @author Khaliullin Cyrill
 * @version 1.0.0
 *
 * Controller - данный класс является контроллером (предназначен для непосредственной обработки запросов от клиента и возвращения результатов).
 * RequestMapping - обрабытывает все запросы с префиксом /isAlive/...
 */
@RequestMapping("/isAlive")
@Controller
public class IsAliveController {

    private  final UserUtils userUtils;

    @Autowired
    public IsAliveController(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    /**
     * Метод, отвечающий за установку последнего онлайна пользователя.
     * @return не играет роли
     */
    @GetMapping("/setAlive")
    public @ResponseBody boolean setAlive() {
        return userUtils.setAlive(SecurityContextHolder.getContext().getAuthentication().getName()); // Установка в БД последнего онлайна пользователя
    }

    /**
     * Метод, перенаправляющий на isUserAlive. Нужен т.к. не принимает никаких аргументов, но в свою очередь передаёт аргумент в виде имени текущего пользователя.
     * @return
     */
    @GetMapping("/isAlive")
    public @ResponseBody boolean isAlive() {
        return isUserAlive(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    /**
     * Метод, отвечает за определения последней активности пользоавтеля (последний  *пинг* по данному запросу)
     * @param username
     * @return Подключен ли пользоавтель к сайту
     */
    @GetMapping("/isUserAlive")
    public @ResponseBody boolean isUserAlive(@RequestParam(required = false ) String username) {
        return userUtils.isAlive(username);
    }
}
