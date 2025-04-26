package mutata.com.github.MatematixProject.entity.token;

import lombok.Data;
import lombok.NoArgsConstructor;
import mutata.com.github.MatematixProject.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Сущность, представляющая токен сброса пароля пользователя.
 * <p>Каждый токен хранится в таблице <code>reset_tokens</code> и уникально
 * ассоциируется с пользователем через relation OneToOne.</p>
 * <p>Токен автоматически создаётся с датой истечения <code>expirationDate</code>,
 * рассчитанной как текущее время плюс {@link #EXPIRATION_IN_HOURS} часов.
 * При этом добавляется смещение в 3 часа для перевода из GMT+0 в GMT+3.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see Token
 */
@Entity
@Table(name = "reset_tokens")
@Data
@NoArgsConstructor
public class ResetPasswordToken implements Token {

    /**
     * Время жизни токена в часах (до автоматического удаления).
     */
    private static final int EXPIRATION_IN_HOURS = 4;

    /**
     * Уникальный идентификатор токена в базе данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Строковое представление токена для передачи в ссылке сброса пароля.
     */
    @Column(nullable = false)
    private String token;

    /**
     * Дата и время истечения токена (включая смещение временной зоны).
     * <p>Значение по умолчанию: текущее время плюс {@link #EXPIRATION_IN_HOURS} часов и 3-часовое смещение.</p>
     */
    @Column(name = "expiration_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime expirationDate = LocalDateTime.now()
            .plus(EXPIRATION_IN_HOURS + 3, ChronoUnit.HOURS);

    /**
     * Связанная сущность пользователя, для которого создан токен.
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "username")
    private User user;

    /**
     * Возвращает имя пользователя, которому принадлежит токен.
     *
     * @return логин пользователя из связанной сущности {@link User}
     */
    @Override
    public String getUserName() {
        return user.getName();
    }

    /**
     * Устанавливает пользователя для данного токена сброса пароля.
     *
     * @param user сущность пользователя
     */
    @Override
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Возвращает идентификатор токена.
     *
     * @return значение поля {@link #id}
     */
    public Long getId() {
        return id;
    }
}