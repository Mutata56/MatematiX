package mutata.com.github.MatematixProject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Сущность, представляющая данные аватарки пользователя.
 * <p>Отображается в таблице <code>avatars</code> и хранит как
 * бинарное изображение, так и закодированную в Base64 строку (для передачи в JSON).</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 */
@Entity
@Table(name = "avatars")
@Data
@NoArgsConstructor
public class AvatarInfo {

    /**
     * Логин пользователя — первичный ключ и одновременно связь с таблицей пользователей.
     */
    @Id
    @Column(name = "username")
    private String username;

    /**
     * Собственно бинарные данные изображения аватарки.
     */
    @Column(name = "avatar")
    private byte[] avatar;

    /**
     * Связанная сущность пользователя.
     * <p>Связь OneToOne по полю username, исключается из JSON-сериализации.</p>
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "username")
    @JsonIgnore
    private User user;

    /**
     * Закодированная версия аватарки в Base64.
     * <p>Поле не сохраняется в БД и игнорируется при сериализации JSON.</p>
     */
    @Transient
    @JsonIgnore
    private String encodedAvatar;

    /**
     * Конструктор для создания сущности с указанием логина пользователя.
     * <p>Поле avatar остаётся пустым до загрузки изображения.</p>
     *
     * @param username логин пользователя, для которого создаётся запись
     */
    public AvatarInfo(String username) {
        this.username = username;
    }
}