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

import static java.awt.SystemColor.info;
/**
 * Класс, представляющий собой сущность, отображаемую в БД. В данном случае сущность "действие над комментарием".
 * Entity - Сущность, отображаемая в БД
 * Table - таблица в БД
 * Data - это сокращенная аннотация, сочетающая возможности @ToString, @EqualsAndHashCode, @Getter @Setter и @RequiredArgsConstructor
 * NoArgsConstructor - сказать lombok создавать конструктор без параметров
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    /**
     * Юзернейм пользователя. Правила валидации для данного поля.
     * Id - является id в таблице БД MySQL
     * Column - с какой колонкой в MySQL связть данное поле
     */
    @Id
    @Column(name = "username")
    @NotNull(message = "Поле не может быть пустым")
    //@Length(min = 5,max = 15,message = "Длина логина должна быть больше 4 и меньше 16")
    @Pattern(regexp = "[a-zA-Z\\d]+",message = "Логин должно состоять из латинских букв и/или цифр")
    private String name;

    /**
     * Почта пользователя. Правила валидации для данного поля.
     */

    @Column(name = "email")
    @NotNull(message = "Поле не может быть пустым")
    @Email(message = "Неправильный формат эл. почты")
    @Length(min = 5,max = 45,message = "Длина эл. почты должна быть больше 4 и меньше 46")

    private String email;

    /**
     * Пароль в зашифрованном виде. Правила валидации для данного поля.
     */

    @Column(name = "password")
    @NotNull(message = "Поле не может быть пустым")
    private String encryptedPassword;

    /**
     * Состояние "blocked" (заблокирован ли на сайте) пользователя.
     */

    @Column(name = "blocked")
    private byte blocked;

    /**
     * Состояние "enabled" (подтвердил ли пользователь почту) пользователя.
     */

    @Column(name = "enabled")
    private byte enabled;

    /**
     * Задача связи OneToOne в контексте MySQL. Выполнение операций удаления каскадно (в обоих таблицах). FetchType.EAGER - связанные данные должны быть извлечены вместе с с основной сущностью
     */

    @OneToOne(mappedBy = "user",fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)

    private ResetPasswordToken resetPasswordToken;

    /**
     * Задача связи OneToOne в контексте MySQL. Выполнение операций удаления каскадно (в обоих таблицах). FetchType.EAGER - связанные данные должны быть извлечены вместе с с основной сущностью
     */

    @OneToOne(mappedBy = "user",fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
    private VerificationToken verificationToken;

    /**
     * Роль пользователя в системе: ADMIN/User
     */
    @Column(name = "role")
    private String role;

    /**
     * Рейтинг пользователя в системе
     */

    @Column(name = "rating")
    private int rating;

    /**
     * Задача связи OneToOne в контексте MySQL. Выполнение операций удаления каскадно (в обоих таблицах). FetchType.LAZY - связанные данные должны быть извлечены ТОЛЬКО когда потребуются
     */

    @OneToOne(mappedBy = "user",fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    private AvatarInfo avatarInfo;

    /**
     * Метод установвки аватарки для пользователя. Устанавливается либо стандартная аватарка, либо выбранная пользователем (но тогда она перекодируется для экономии памяти)
     * @param map - хеш коллекция, хранящая юзернейм и соотв. информацию об аватарке. AvatarInfo либо null == стандартная аватарка, либо аватарка пользователя
     */

    public void storeAvatar(HashMap<String,AvatarInfo> map) {
        if (!map.containsKey(getName())) {
            map.put(getName(),avatarInfo);
            if(avatarInfo != null)
                avatarInfo.setEncodedAvatar(Utils.encodeAvatar(avatarInfo.getAvatar()));
        }
    }

    /**
     * Комментарии на стене пользователя.
     * Задача связи OneToMany в контексте MySQL.
     */

    @OneToMany(mappedBy = "receiver")
    private List<Comment> comments;

    /**
     * Статьи, которые пользователь добавил в закладки (избранное).
     * Задача связи ManyToMany в контексте MySQL.
     * FetchType.LAZY - связанные данные должны быть извлечены ТОЛЬКО когда потребуются
     */

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_articles",
            joinColumns = @JoinColumn(name = "name"),
            inverseJoinColumns = @JoinColumn(name = "article_id")
    )
    private List<Article> articles;

    /**
     * Друзья пользователя
     * Задача связи ManyToMany в контексте MySQL.
     * FetchType.LAZY - связанные данные должны быть извлечены ТОЛЬКО когда потребуются
     */

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="user_friends",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "friend_name")
    )
    private List<User> friends;

    /**
     * Когда человек был в сети последний раз.
     */
    @Column(name = "last_time_online")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastTimeOnline;

    public User(String name, String email, String password, byte enabled, byte activated,String role) {
        this.name = name;
        this.email = email;
        this.encryptedPassword = password;
        this.enabled = enabled;
        this.blocked = activated;
        this.role = role;
    }

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
