package mutata.com.github.entity.dto;


import lombok.Data;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

@Data
public class PasswordDTO {

    @NotNull(message = "Поле не может быть пустым")
    @Length(min = 5,max = 45,message = "Длина пароля должна быть больше 5 и меньше 60")
    private String password;

    private String passwordAgain;

    private String token;
}
