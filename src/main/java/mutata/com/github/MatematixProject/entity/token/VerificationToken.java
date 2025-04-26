package mutata.com.github.MatematixProject.entity.token;

import lombok.Data;
import lombok.NoArgsConstructor;
import mutata.com.github.MatematixProject.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Сущность, представляющая токен для подтверждения email пользователя.
 * <p>Хранится в таблице <code>verification_tokens</code> и однозначно
 * связывается с пользователем через связь OneToOne.</p>
 * <p>При создании токена поле <code>expirationDate</code> устанавливается
 * как текущее время плюс {@link #EXPIRATION_IN_HOURS} часов и дополнительное
 * смещение в 3 часа для перехода из GMT+0 в GMT+3.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see Token
 */
@Entity
@Table(name = "verification_tokens")
@Data
@NoArgsConstructor
public class VerificationToken implements Token {

    /**
     * Время жизни токена в часах, после которого он считается просроченным.
     */
    private static final int EXPIRATION_IN_HOURS = 24;

    /**
     * Уникальный идентификатор токена в базе данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Строковое значение токена, используемое в ссылках подтверждения.
     */
    @Column(nullable = false)
    private String token;

    /**
     * Дата и время истечения действия токена.
     * <p>Инициализируется при создании экземпляра.</p>
     */
    @Column(name = "expiration_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime expirationDate = LocalDateTime.now()
            .plus(EXPIRATION_IN_HOURS + 3, ChronoUnit.HOURS);

    /**
     * Обновляет дату и время истечения токена.
     *
     * @param date новая дата истечения токена
     */
    public void setExpirationDate(LocalDateTime date) {
        this.expirationDate = date;
    }

    /**
     * Пользователь, которому принадлежит этот токен.
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "username")
    private User user;

    /**
     * Возвращает имя пользователя (логин) из связанной сущности.
     *
     * @return логин пользователя
     */
    @Override
    public String getUserName() {
        return user.getName();
    }

    /**
     * Устанавливает пользователя, для которого создаётся токен.
     *
     * @param user сущность пользователя
     */
    @Override
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Возвращает уникальный идентификатор токена.
     *
     * @return значение поля {@link #id}
     */
    public Long getId() {
        return id;
    }
}
