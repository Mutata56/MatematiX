package mutata.com.github.MatematixProject.entity.dto;


import lombok.Data;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

/**
 * DTO - data transfer object - объект, используемый для передачи данных, в данном случае данных сущности пароля в случае, когда пользователь забыл пароль.
 * Data - это сокращенная аннотация, сочетающая возможности @ToString, @EqualsAndHashCode, @Getter @Setter и @RequiredArgsConstructor
 */
@Data
public class PasswordDTO {
    /**
     * Пароль не должен быть пустым, его длина должна быть больше 5 и меньше 60. Содержит содержимое поле пароль1 на JSP странице resetPassword
     */
    @NotNull(message = "Поле не может быть пустым")
    @Length(min = 5,max = 60,message = "Длина пароля должна быть больше 5 и меньше 60")
    private String password;
    /**
     * Содержит содержимое поле пароль2 на JSP странице resetPassword
     */
    @NotNull(message = "Поле не может быть пустым")
    @Length(min = 5,max = 60,message = "Длина пароля должна быть больше 4 и меньше 61")
    private String passwordAgain;
    /**
     * Токен ResetPasswordToken в строковом виде, нужный для смены пароля на JSP странице resetPassword
     */
    private String token;
}
