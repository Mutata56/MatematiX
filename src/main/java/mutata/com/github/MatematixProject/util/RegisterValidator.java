package mutata.com.github.MatematixProject.util;

import mutata.com.github.MatematixProject.entity.dto.RegisterDTO;
import mutata.com.github.MatematixProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Компонент Spring для валидации данных при регистрации пользователя.
 * <p>Проверяет уникальность логина и электронной почты, а также совпадение паролей.</p>
 *
 * @see mutata.com.github.MatematixProject.entity.dto.RegisterDTO
 * @see org.springframework.validation.Validator
 */
@Component
public class RegisterValidator implements Validator {

    /**
     * Сервис пользователей для проверки существующих логинов и почт.
     */
    private final UserService userService;

    /**
     * Конструктор для внедрения зависимости UserService.
     *
     * @param userService сервис для работы с пользователями
     */
    @Autowired
    public RegisterValidator(UserService userService) {
        this.userService = userService;
    }

    /**
     * Определяет, поддерживает ли валидатор указанный класс.
     *
     * @param clazz класс, который проверяется на поддержку валидатором
     * @return true, если clazz равен RegisterDTO
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return RegisterDTO.class.equals(clazz);
    }

    /**
     * Выполняет валидацию объекта RegisterDTO.
     * <ul>
     *   <li>Проверяет, что логин ещё не занят.</li>
     *   <li>Проверяет, что почта ещё не занята.</li>
     *   <li>Проверяет совпадение пароля и подтверждения пароля.</li>
     * </ul>
     *
     * @param target объект для валидации (ожидается RegisterDTO)
     * @param errors контейнер, куда записываются ошибки валидации
     */
    @Override
    public void validate(Object target, Errors errors) {
        RegisterDTO registerDTO = (RegisterDTO) target;
        String username = registerDTO.getName();
        if (isNameAlreadyTaken(username)) {
            errors.rejectValue("name", "", "Данный логин уже занят.");
        }

        String email = registerDTO.getEmail();
        if (isEmailAlreadyTaken(email)) {
            errors.rejectValue("email", "", "Данная эл. почта уже занята.");
        }

        String password = registerDTO.getPassword();
        String password2 = registerDTO.getPasswordAgain();
        if (password != null && password2 != null && !password.equals(password2)) {
            errors.rejectValue("password", "", "Пароли не совпадают.");
        }
    }

    /**
     * Проверяет, существует ли пользователь с указанной почтой.
     *
     * @param email адрес электронной почты для проверки
     * @return true, если почта уже зарегистрирована
     */
    public boolean isEmailAlreadyTaken(String email) {
        return userService.findByEmailIgnoreCase(email) != null;
    }

    /**
     * Проверяет, существует ли пользователь с указанным именем.
     *
     * @param name логин для проверки
     * @return true, если имя уже зарегистрировано
     */
    public boolean isNameAlreadyTaken(String name) {
        return userService.findByNameIgnoreCase(name) != null;
    }
}
