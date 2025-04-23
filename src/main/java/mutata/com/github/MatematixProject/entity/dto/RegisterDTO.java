package mutata.com.github.MatematixProject.entity.dto;

import lombok.Data;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
/**
 * DTO - data transfer object - объект, используемый для передачи данных, в данном случае данных для регистрацации.
 * Data - это сокращенная аннотация, сочетающая возможности @ToString, @EqualsAndHashCode, @Getter @Setter и @RequiredArgsConstructor
 */
@Data
public class RegisterDTO {
    /**
     * Имя, почта и пароль с его повторным вводом с соотв. валидацией каждого поля
     */
    @NotNull(message = "Поле не может быть пустым")
    @Length(min = 5,max = 15,message = "Длина логина должна быть больше 4 и меньше 16")
    @Pattern(regexp = "[a-zA-Z\\d]+",message = "Логин должно состоять из латинских букв и/или цифр")
    private String name;


    @NotNull(message = "Поле не может быть пустым")
    @Email(message = "Неправильный формат эл. почты")
    @Length(min = 5,max = 45,message = "Длина эл. почты должна быть больше 4 и меньше 46")

    private String email;
    @NotNull(message = "Поле не может быть пустым")
    @Length(min = 5,max = 60,message = "Длина пароля должна быть больше 4 и меньше 61")

    private String password;

    @NotNull(message = "Поле не может быть пустым")
    @Length(min = 5,max = 60,message = "Длина пароля должна быть больше 4 и меньше 61")
    private String passwordAgain;
}
