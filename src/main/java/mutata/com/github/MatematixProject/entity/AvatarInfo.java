package mutata.com.github.MatematixProject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
/**
 * Класс, представляющий собой сущность, отображаемую в БД. В данном случае сущность аватарка.
 * Entity - Сущность, отображаемая в БД
 * Table - таблица в БД
 * Data - это сокращенная аннотация, сочетающая возможности @ToString, @EqualsAndHashCode, @Getter @Setter и @RequiredArgsConstructor
 * NoArgsConstructor - сказать lombok создавать конструктор без параметров
 */
@Entity
@Table(name = "avatars")
@Data
public class AvatarInfo {
    /**
     * Id - является id в таблице БД MySQL
     * Column - с какой колонкой в MySQL связть данное поле
     */
    @Id
    @Column(name = "username")
    private String username;
    /**
     * Изображение аватарки в виде массива байтов
     */
    @Column(name=  "avatar")
    private byte[] avatar;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "username")
    @JsonIgnore
    private User user;

    /**
     * Изображение аватарки в закодированном виде строкового литерала
     * Transient - аннотация в Spring, которая указывает, что поле или метод не должны быть сохранены в базе данных
     * JsonIgnore - исключить определённое данное свойство объекта Java из сериализации и десериализации JSON.
     */

    @Transient
    @JsonIgnore
    private String encodedAvatar;

    public AvatarInfo() {

    }
    public AvatarInfo(String username) {
        this.username = username;
    }
}
