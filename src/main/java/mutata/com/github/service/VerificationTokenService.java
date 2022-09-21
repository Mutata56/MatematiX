package mutata.com.github.service;



import mutata.com.github.entity.token.Token;
import mutata.com.github.entity.User;
import mutata.com.github.entity.token.VerificationToken;
import mutata.com.github.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class VerificationTokenService implements TokenService {

    private final VerificationTokenRepository repository;


    @Autowired
    public VerificationTokenService(VerificationTokenRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = false)
    public void createVerificationToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        repository.save(verificationToken);
    }

    public Token findByToken(String token) {
        return repository.findByToken(token).orElse(null);
    }

    @Transactional(readOnly = false)
    public void delete(String token) {
        repository.deleteByToken(token);
    }

    @Transactional(readOnly = false)
    @Override
    public void save(Token token) {
        repository.save((VerificationToken) token);
    }

    @Transactional(readOnly = false)
    public void deleteExpiredSince(LocalDateTime date) {
        repository.deleteByExpirationDateLessThan(date);
    }
}
