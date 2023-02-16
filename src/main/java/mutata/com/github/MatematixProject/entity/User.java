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

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "username")
    @NotNull(message = "Поле не может быть пустым")
    //@Length(min = 5,max = 15,message = "Длина логина должна быть больше 4 и меньше 16")
    @Pattern(regexp = "[a-zA-Z\\d]+",message = "Логин должно состоять из латинских букв и/или цифр")
    private String name;

    @Column(name = "email")
    @NotNull(message = "Поле не может быть пустым")
    @Email(message = "Неправильный формат эл. почты")
    @Length(min = 5,max = 45,message = "Длина эл. почты должна быть больше 4 и меньше 46")

    private String email;

    @Column(name = "password")
    @NotNull(message = "Поле не может быть пустым")
    private String encryptedPassword;

    @Column(name = "blocked")
    private byte blocked;
    @Column(name = "enabled")
    private byte enabled;

    @OneToOne(mappedBy = "user",fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)

    private ResetPasswordToken resetPasswordToken;

    @OneToOne(mappedBy = "user",fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
    private VerificationToken verificationToken;
    @Column(name = "role")
    private String role;

    @Column(name = "rating")
    private int rating;

    @OneToOne(mappedBy = "user",fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    private AvatarInfo avatarInfo;

    public void storeAvatar(HashMap<String,AvatarInfo> map) {
        if (!map.containsKey(getName())) {
            map.put(getName(),avatarInfo);
            if(avatarInfo != null)
                avatarInfo.setEncodedAvatar(Utils.encodeAvatar(avatarInfo.getAvatar()));
        }
    }

    @OneToMany(mappedBy = "receiver")
    private List<Comment> comments; // Comments on the wall (profile)

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_articles",
            joinColumns = @JoinColumn(name = "name"),
            inverseJoinColumns = @JoinColumn(name = "article_id")
    )
    private List<Article> articles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="user_friends",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "friend_name")
    )
    private List<User> friends;


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
