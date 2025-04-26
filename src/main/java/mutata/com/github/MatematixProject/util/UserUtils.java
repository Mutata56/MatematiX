package mutata.com.github.MatematixProject.util;

import mutata.com.github.MatematixProject.entity.AvatarInfo;
import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.service.AvatarInfoService;
import mutata.com.github.MatematixProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Утилитный компонент для работы с пользователями и их аватарами.
 * Включает методы для проверки онлайн-статуса, установки времени последней активности,
 * а также загрузки и кодирования аватарок пользователей.
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 */
@Component
public class UserUtils {

    private final UserService userService;
    private final AvatarInfoService avatarInfoService;
    /** Количество миллисекунд, соответствующее пяти минутам */
    private static final int FIVE_MINUTES_IN_MS = 300_000; // 5 * 60 * 1000
    /** Количество миллисекунд, соответствующее одному часу */
    private static final int HOUR_IN_MS = 3_600_000; // 60 * 1000 * 60

    @Autowired
    public UserUtils(UserService userService, AvatarInfoService avatarInfoService) {
        this.userService = userService;
        this.avatarInfoService = avatarInfoService;
    }

    /**
     * Проверяет, находится ли пользователь в сети (онлайн).
     * Параметр username сравнивается с текущим аутентифицированным пользователем;
     * если не совпадает, проверяется время последней активности в БД.
     *
     * @param username имя пользователя, чей онлайн проверяется
     * @return true, если пользователь онлайн или является текущим авторизованным;
     *         false в противном случае или при отсутствии данных
     */
    public boolean isAlive(String username) {
        if (username == null) {
            return false;
        }
        // Текущий пользователь всегда считается онлайн
        if (username.equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            return true;
        }
        User targetUser = userService.findByNameIgnoreCase(username);
        Date lastOnline = targetUser.getLastTimeOnline();
        if (lastOnline == null) {
            return false;
        }
        // Сравнение текущего времени с временем последней активности (+ сдвиг GMT)
        long adjustedLast = lastOnline.getTime() - 3 * HOUR_IN_MS;
        return (new Date().getTime() - adjustedLast) < FIVE_MINUTES_IN_MS;
    }

    /**
     * Устанавливает пользователю время последней активности (онлайн) в текущий момент.
     * Добавляет смещение для перехода из GMT+0 в GMT+3.
     *
     * @param username имя пользователя, для которого обновляется время активности
     * @return всегда true (результат операции сохранения)
     */
    public boolean setAlive(String username) {
        User user = userService.findByNameIgnoreCase(username);
        if (user != null) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(new Date());
            calendar.add(Calendar.HOUR_OF_DAY, 3); // Сдвиг GMT+0 → GMT+3
            user.setLastTimeOnline(calendar.getTime());
            userService.saveWithoutPasswordEncryption(user);
        }
        return true;
    }

    /**
     * Загружает аватар пользователя и добавляет информацию в модель и карту.
     * Кодирует аватар в Base64 при необходимости.
     *
     * @param username имя пользователя, чей аватар загружается
     * @param map      коллекция username → AvatarInfo для хранения загруженных аватаров
     * @param model    MVC-модель для передачи флагов во View
     * @param prefix   префикс для атрибута наличия аватара ("My" или пустая строка)
     */
    public void loadAvatar(String username, HashMap<String, AvatarInfo> map, Model model, String prefix) {
        AvatarInfo info = avatarInfoService.findByName(username).orElse(null);
        if (info != null && info.getEncodedAvatar() == null) {
            info.setEncodedAvatar(Utils.encodeAvatar(info.getAvatar()));
        }
        map.putIfAbsent(username, info);
        model.addAttribute(String.format("has%sAvatar", StringUtils.capitalize(prefix)), info != null);
    }

    /**
     * Загружает аватар пользователя в модель.
     * Возвращает строку Base64 с изображением или null.
     *
     * @param username имя пользователя
     * @param model    MVC-модель для передачи аватара во View
     * @param prefix   префикс для атрибута наличия аватара ("My" или пустая строка)
     */
    public void loadAvatar(String username, Model model, String prefix) {
        AvatarInfo info = avatarInfoService.findByName(username).orElse(null);
        boolean hasAvatar = info != null;
        model.addAttribute(String.format("has%sAvatar", StringUtils.capitalize(prefix)), hasAvatar);
        model.addAttribute("theAvatar", hasAvatar ? Utils.encodeAvatar(info.getAvatar()) : null);
    }
}