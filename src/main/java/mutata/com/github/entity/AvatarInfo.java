package mutata.com.github.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "avatars")
@Data
@NoArgsConstructor
public class AvatarInfo {

    @Id
    @Column(name = "username")
    private String username;

    @Column(name=  "avatar")
    private byte[] avatar;

    @Column(name = "avatar_format")
    private String avatarFormat;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "username")
    private User user;
}
