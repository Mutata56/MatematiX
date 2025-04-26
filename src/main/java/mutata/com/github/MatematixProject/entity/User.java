package mutata.com.github.MatematixProject.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import mutata.com.github.MatematixProject.entity.token.ResetPasswordToken;
import mutata.com.github.MatematixProject.entity.token.VerificationToken;
import mutata.com.github.MatematixProject.util.Utils;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Сущность пользователя в системе.
 * <p>Представляет запись пользователя в таблице <code>users</code>.
 * Содержит данные для аутентификации, профиля, связей с токенами,
 * аватаркой, комментариями, статьями и друзьями.</p>
 *
 * <p>Валидация полей обеспечивается аннотациями
 * {@link NotNull}, {@link Pattern}, {@link Email}, {@link Length}.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    /**
     * Логин пользователя — первичный ключ.
     * <p>Не может быть пустым, только латинские буквы и цифры.</p>
     */
    @Id
    @Column(name = "username")
    @NotNull(message = "Поле не может быть пустым")
    @Pattern(regexp = "[a-zA-Z\\d]+", message = "Логин должен состоять из латинских букв и/или цифр")
    private String name;

    /**
     * Адрес электронной почты пользователя.
     * <p>Не может быть пустым, формат email и длина 5–45 символов.</p>
     */
    @Column(name = "email")
    @NotNull(message = "Поле не может быть пустым")
    @Email(message = "Неправильный формат эл. почты")
    @Length(min = 5, max = 45, message = "Длина эл. почты должна быть больше 4 и меньше 46")
    private String email;

    /**
     * Зашифрованный пароль пользователя.
     * <p>Не может быть пустым.</p>
     */
    @Column(name = "password")
    @NotNull(message = "Поле не может быть пустым")
    private String encryptedPassword;

    /**
     * Флаг блокировки пользователя: 1 — заблокирован, 0 — активен.
     */
    @Column(name = "blocked")
    private byte blocked;

    /**
     * Флаг подтверждения email: 1 — подтверждён, 0 — не подтверждён.
     */
    @Column(name = "enabled")
    private byte enabled;

    /**
     * Связь с токеном сброса пароля.
     * <p>При удалении пользователя токен удаляется каскадно.</p>
     */
    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private ResetPasswordToken resetPasswordToken;

    /**
     * Связь с токеном подтверждения email.
     * <p>При удалении пользователя токен удаляется каскадно.</p>
     */
    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private VerificationToken verificationToken;

    /**
     * Роль пользователя: ROLE_ADMIN или ROLE_USER.
     */
    @Column(name = "role")
    private String role;

    /**
     * Рейтинг пользователя (число голосов от комментариев).
     */
    @Column(name = "rating")
    private int rating;


    public int getRating() {
        return getComments().stream()
                .mapToInt(Comment::getRating)
                .sum();
    }
    /**
     * Связь с аватаркой пользователя.
     * <p>При удалении пользователя аватарка удаляется каскадно.</p>
     */
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private AvatarInfo avatarInfo;

    /**
     * Сохраняет аватарку в переданную карту для последующей сериализации.
     * <p>Ключ — логин пользователя, значение — {@link AvatarInfo}.
     * Если аватарка не null, кодирует её Base64.</p>
     *
     * @param map карта для хранения аватарок
     */
    public void storeAvatar(HashMap<String, AvatarInfo> map) {
        if (!map.containsKey(name)) {
            map.put(name, avatarInfo);
            if (avatarInfo != null) {
                avatarInfo.setEncodedAvatar(Utils.encodeAvatar(avatarInfo.getAvatar()));
            }
        }
    }

    /**
     * Комментарии, адресованные этому пользователю.
     */
    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
    private List<Comment> comments;

    /**
     * Статьи, добавленные пользователем в закладки.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_articles",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "article_id")
    )
    private List<Article> articles;

    /**
     * Список друзей пользователя.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "friend_name")
    )
    private List<User> friends;

    /**
     * Время последнего визита пользователя.
     */
    @Column(name = "last_time_online")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastTimeOnline;

    /**
     * Конструктор с основными полями для создания пользователя программно.
     *
     * @param name               логин пользователя
     * @param email              email пользователя
     * @param password           зашифрованный пароль
     * @param enabled            флаг подтверждения email
     * @param blocked            флаг блокировки
     * @param role               роль пользователя
     */
    public User(String name, String email, String password, byte enabled, byte blocked, String role) {
        this.name = name;
        this.email = email;
        this.encryptedPassword = password;
        this.enabled = enabled;
        this.blocked = blocked;
        this.role = role;
    }

    /**
     * Переопределённый метод для краткого текстового представления пользователя.
     */
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", encryptedPassword='" + encryptedPassword + '\'' +
                ", enabled=" + enabled +
                ", role='" + role + '\'' +
                '}';
    }
}