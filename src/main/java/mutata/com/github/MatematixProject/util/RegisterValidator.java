package mutata.com.github.MatematixProject.util;

import mutata.com.github.MatematixProject.entity.dto.RegisterDTO;
import mutata.com.github.MatematixProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
@Component
public class RegisterValidator implements Validator {

    private final UserService userService;

    @Autowired
    public RegisterValidator(UserService userService) {
        this.userService = userService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return RegisterDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        RegisterDTO registerDTO = (RegisterDTO) target;
        String username = registerDTO.getName();
        if(isNameAlreadyTaken(username)) {
            errors.rejectValue("name","","Данный логин уже занят.");
        }
        String email = registerDTO.getEmail();
        if(isEmailAlreadyTaken(email)) {
            errors.rejectValue("email","","Данная эл. почта уже занята.");
        }
        String password = registerDTO.getPassword();
        String password2 = registerDTO.getPasswordAgain();
        if(password != null && password2 != null && !password.equals(password2))
            errors.rejectValue("password","","Пароли не совпадают.");
    }



    public boolean isEmailAlreadyTaken(String email) {
        return userService.findByEmailIgnoreCase(email) != null;
    }
    public boolean isNameAlreadyTaken(String name) {
        return userService.findByNameIgnoreCase(name) != null;
    }
}
