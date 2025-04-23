package mutata.com.github.MatematixProject.entity.token;

import lombok.Data;
import lombok.NoArgsConstructor;
import mutata.com.github.MatematixProject.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Класс, представляющий токен, служащий для идентификации пользователя, который хочет поменять пароль на сайте.
 * Entity - Сущность, отображаемая в БД
 * Table - таблица в БД
 * Data - это сокращенная аннотация, сочетающая возможности @ToString, @EqualsAndHashCode, @Getter @Setter и @RequiredArgsConstructor
 * NoArgsConstructor - сказать lombok создавать конструктор без параметров
 */
@Entity
@Table(name = "reset_tokens")
@Data
@NoArgsConstructor
public class ResetPasswordToken implements Token {
    /**
     * Время, за которое токен перестанет существовать, дабы не хранить его вечно в БД
     */
    private static final int EXPIRATION_IN_HOURS = 4;

    /**
     * Id - является id в таблице БД MySQL
     * GeneratedValue - инкреминтировать Id-шку для каждой сущности (новой)
     * Column - с какой колонкой в MySQL связть данное поле
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Строковое изображение токена
     */

    @Column
    private String token;

    /**
     * К EXPIRATION_HOURS добавляем 3 часа, чтобы перейти из GMT+0 в GMT+3
     */

    @Column(name = "expiration_date",columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime expirationDate = LocalDateTime.now().plus(EXPIRATION_IN_HOURS + 3, ChronoUnit.HOURS);

    /**
     * Задача связи OneToOne между таблица reset_tokens и users
     */

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "username")
    private User user;

    @Override
    public String getUserName() {
        return user.getName();
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }
}
