package mutata.com.github.MatematixProject.service;

import mutata.com.github.MatematixProject.dao.MyResponse;
import mutata.com.github.MatematixProject.dao.ResetPasswordDAO;
import mutata.com.github.MatematixProject.entity.token.ResetPasswordToken;
import mutata.com.github.MatematixProject.entity.token.Token;
import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.entity.token.VerificationToken;
import mutata.com.github.MatematixProject.repository.ResetPasswordTokenRepository;
import mutata.com.github.MatematixProject.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
/**
 * Сервис, связанный с ResetPasswordToken
 * @see ResetPasswordToken
 * Transactional(readOnly = true) - данный сервис занимается только чтением, и никак не изменяет данные БД
 */

@Service
@Transactional(readOnly = true)
public class ResetPasswordTokenService implements TokenService<ResetPasswordToken> {

    private final ResetPasswordTokenRepository repository;

    private final ResetPasswordDAO resetPasswordDAO;

    @Autowired
    public ResetPasswordTokenService(ResetPasswordTokenRepository repository, ResetPasswordDAO resetPasswordDAO) {
        this.repository = repository;
        this.resetPasswordDAO = resetPasswordDAO;
    }

    /**
     * Создание токена типа ResetPasswordToken
     * @param user - пользователь, владеющий токеном
     * @param token - строковое представление токена
     */
    @Transactional(readOnly = false)
    public void createResetPasswordToken(User user, String token) {
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken();
        resetPasswordToken.setToken(token);
        resetPasswordToken.setUser(user);
        repository.save(resetPasswordToken);
    }

    /**
     * Удаление токена которые считаюстя expired на момент date
     * @param date  - удалить все токены, чьи expiration date идут ДО даты
     */

    @Transactional(readOnly = false)
    public void deleteExpiredSince(LocalDateTime date) {
        repository.deleteByExpirationDateLessThan(date);
    }

    /**
     * Найти токен по его строковому литералу
     * @param token - строкой литерал токена
     * @return - найденный токен
     */

    @Override
    public Token findByToken(String token) {
        return repository.findByToken(token).orElse(null);
    }

    /**
     * Удалить токен по строковому литералу
     * @param token - строкой литерал токена
     */

    @Override
    @Transactional(readOnly = false)
    public void delete(String token) {
        repository.deleteByToken(token);
    }

    /**
     * Сохранить данный токен
     * @param token - токен, который нужно сохранить
     */

    @Transactional(readOnly = false)
    @Override
    public void save(Token token) {
        repository.save((ResetPasswordToken) token);
    }

    /**
     * Найти все токены
     */

    public List<ResetPasswordToken> findAll() {
        return repository.findAll();
    }

    /**
     * Найти все токены, вернуть currentPage страницу с количеством itemsPerPage сущностями.
     * @return страница с токенами в количестве itemsPerPage на странице по номеру currentPage
     */

    @Override
    public Page<ResetPasswordToken> findAllReturnPage(Integer currentPage, Integer itemsPerPage) {
        return repository.findAll(PageRequest.of(currentPage,itemsPerPage));
    }


    @Override
    public MyResponse<ResetPasswordToken> find(Integer currentPage, Integer itemsPerPage, String find, String findBy) {
        switch (findBy) {
            case "id":
                return resetPasswordDAO.findWhereIdLike(currentPage,itemsPerPage,find);
            case "token":
                return resetPasswordDAO.findWhereTokenLike(currentPage,itemsPerPage,find);
            case "user":
                return resetPasswordDAO.findWhereUserLike(currentPage,itemsPerPage,find);

        }
        return null;
    }

    @Override
    public Page<ResetPasswordToken> findAllSortedBy(Integer currentPage, Integer itemsPerPage, String sortBy,String sortDirection) {
        return repository.findAll(PageRequest.of(currentPage,itemsPerPage,"asc".equals(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC,sortBy));
    }

    @Override
    public MyResponse findAndSort(Integer currentPage, Integer itemsPerPage, String find, String findBy, String sortBy,String sortDirection) {
        MyResponse<ResetPasswordToken> response = find(currentPage,itemsPerPage,find,findBy);
        switch (sortBy) {
            case "id":
                response.getContent().sort("asc".equals(sortDirection) ? Comparator.comparing(ResetPasswordToken::getId) : Comparator.comparing(ResetPasswordToken::getId).reversed());
            case "token":
                response.getContent().sort("asc".equals(sortDirection) ? Comparator.comparing(ResetPasswordToken::getToken) : Comparator.comparing(ResetPasswordToken::getToken).reversed());
            case "user":
                response.getContent().sort("asc".equals(sortDirection) ? Comparator.comparing(ResetPasswordToken::getUserName) : Comparator.comparing(ResetPasswordToken::getUserName).reversed());
        }
        return response;
    }

    public ResetPasswordToken findById(Long id) {
        return id == null ? null : repository.findById(id).orElse(null);
    }
}
