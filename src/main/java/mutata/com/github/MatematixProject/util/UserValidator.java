package mutata.com.github.MatematixProject.util;

import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.entity.dto.RegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class UserValidator {

    private final RegisterValidator registerValidator;

    @Autowired
    public UserValidator(RegisterValidator registerValidator) {
        this.registerValidator = registerValidator;
    }

    public boolean isEmailAlreadyTaken(String email) {
        return registerValidator.isEmailAlreadyTaken(email);
    }

    public boolean isNameAlreadyTaken(String name) {
        return registerValidator.isNameAlreadyTaken(name);
    }
    public void validate(User user, Errors errors) {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setName(user.getName());
        registerDTO.setEmail(user.getEmail());
        registerDTO.setPassword(user.getEncryptedPassword());
        registerDTO.setPasswordAgain(user.getEncryptedPassword());
        registerValidator.validate(registerDTO,errors);
    }

}
