package mutata.com.github.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import mutata.com.github.entity.token.ResetPasswordToken;
import mutata.com.github.entity.token.VerificationToken;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "username")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
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


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_article",
            joinColumns = @JoinColumn(name = "name"),
            inverseJoinColumns = @JoinColumn(name = "article_id")
    )
    private List<Article> articles;

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
