package mutata.com.github.entity.token;

import lombok.Data;
import lombok.NoArgsConstructor;
import mutata.com.github.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "verification_tokens")
@Data
@NoArgsConstructor
public class VerificationToken implements Token {
    private static final int EXPIRATION_IN_HOURS = 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column
    private String token;

    // Add 3 hours in order to change GMT+0 TO GMT+3
    @Column(name = "expiration_date",columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime expirationDate = LocalDateTime.now().plus(EXPIRATION_IN_HOURS + 3,ChronoUnit.HOURS);

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "username")
    private User user;

    @Override
    public void setUser(User user) {
        this.user = user;
    }
}
