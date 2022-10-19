package mutata.com.github.service;

import mutata.com.github.dao.MyResponse;
import mutata.com.github.entity.token.ResetPasswordToken;
import mutata.com.github.entity.token.Token;
import mutata.com.github.entity.User;
import mutata.com.github.entity.token.VerificationToken;
import mutata.com.github.repository.ResetPasswordTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ResetPasswordTokenService implements TokenService<ResetPasswordToken> {

    private final ResetPasswordTokenRepository repository;

    @Autowired
    public ResetPasswordTokenService(ResetPasswordTokenRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = false)
    public void createResetPasswordToken(User user, String token) {
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken();
        resetPasswordToken.setToken(token);
        resetPasswordToken.setUser(user);
        repository.save(resetPasswordToken);
    }
    @Transactional(readOnly = false)
    public void deleteExpiredSince(LocalDateTime date) {
        repository.deleteByExpirationDateLessThan(date);
    }

    @Override
    public Token findByToken(String token) {
        return repository.findByToken(token).orElse(null);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String token) {
        repository.deleteByToken(token);
    }
    @Transactional(readOnly = false)
    @Override
    public void save(Token token) {
        repository.save((ResetPasswordToken) token);
    }


    public List<ResetPasswordToken> findAll() {
        return repository.findAll();
    }
    @Override
    public Page<ResetPasswordToken> findAllReturnPage(Integer currentPage, Integer itemsPerPage) {
        return repository.findAll(PageRequest.of(currentPage,itemsPerPage));
    }

    @Override
    public MyResponse<ResetPasswordToken> find(Integer currentPage, Integer itemsPerPage, String find, String findBy) {
        // TODO
        return null;
    }

    @Override
    public Page<ResetPasswordToken> findAllSortedBy(Integer currentPage, Integer itemsPerPage, String sortBy,String sortDirection) {
        // TODO
        return null;
    }

    @Override
    public MyResponse findAndSort(Integer currentPage, Integer itemsPerPage, String find, String findBy, String sortBy,String sortDirection) {
        return null; // TODO
    }

}
