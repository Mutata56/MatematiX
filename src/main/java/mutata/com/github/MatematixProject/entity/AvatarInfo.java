package mutata.com.github.MatematixProject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "avatars")
@Data
public class AvatarInfo {

    @Id
    @Column(name = "username")
    private String username;

    @Column(name=  "avatar")
    private byte[] avatar;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "username")
    @JsonIgnore
    private User user;

    @Transient
    @JsonIgnore
    private String encodedAvatar;

    public AvatarInfo() {

    }
    public AvatarInfo(String username) {
        this.username = username;
    }
}
