package mutata.com.github.MatematixProject.service;

import mutata.com.github.MatematixProject.dao.MyResponse;
import mutata.com.github.MatematixProject.dao.VerificationTokenDAO;
import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.entity.token.Token;
import mutata.com.github.MatematixProject.entity.token.VerificationToken;
import mutata.com.github.MatematixProject.repository.VerificationTokenRepository;
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
 * Сервис для работы с токенами верификации электронной почты.
 * <p>Обеспечивает CRUD-операции и пагинацию для {@link VerificationToken}.</p>
 * <p>Все методы по умолчанию выполняются в транзакции только для чтения, если не указанно иное.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see VerificationToken
 * @see TokenService
 */
@Service
@Transactional(readOnly = true)
public class VerificationTokenService implements TokenService<VerificationToken> {

    private final VerificationTokenRepository repository;
    private final VerificationTokenDAO verificationTokenDAO;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param repository         репозиторий JPA для {@link VerificationToken}
     * @param verificationTokenDAO DAO для более сложных запросов с пагинацией и фильтрацией
     */
    @Autowired
    public VerificationTokenService(VerificationTokenRepository repository,
                                    VerificationTokenDAO verificationTokenDAO) {
        this.repository = repository;
        this.verificationTokenDAO = verificationTokenDAO;
    }

    /**
     * Создает и сохраняет новый токен верификации для заданного пользователя.
     *
     * @param user  пользователь, для которого создается токен
     * @param token строковое значение токена
     */
    @Transactional(readOnly = false)
    public void createVerificationToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        repository.save(verificationToken);
    }

    /**
     * Ищет токен верификации по его строковому представлению.
     *
     * @param token строковой литерал токена
     * @return найденный токен или {@code null}, если не найден
     */
    @Override
    public Token findByToken(String token) {
        return repository.findByToken(token).orElse(null);
    }

    /**
     * Удаляет токен верификации по его строковому представлению.
     *
     * @param token строковой литерал токена
     */
    @Transactional(readOnly = false)
    @Override
    public void delete(String token) {
        repository.deleteByToken(token);
    }

    /**
     * Сохраняет или обновляет указанный токен в базе данных.
     *
     * @param token объект токена для сохранения
     */
    @Transactional(readOnly = false)
    @Override
    public void save(Token token) {
        repository.save((VerificationToken) token);
    }

    /**
     * Удаляет все токены, срок действия которых истек до указанной даты.
     *
     * @param date момент, до которого токены считаются просроченными
     */
    @Transactional(readOnly = false)
    public void deleteExpiredSince(LocalDateTime date) {
        repository.deleteByExpirationDateLessThan(date);
    }

    /**
     * Возвращает список всех токенов верификации.
     *
     * @return список всех токенов
     */
    public List<VerificationToken> findAll() {
        return repository.findAll();
    }

    /**
     * Пагинация токенов: возвращает страницу токенов заданного размера.
     *
     * @param currentPage  номер страницы (0-based)
     * @param itemsPerPage количество элементов на странице
     * @return страница токенов
     */
    @Override
    public Page<VerificationToken> findAllReturnPage(Integer currentPage, Integer itemsPerPage) {
        return repository.findAll(PageRequest.of(currentPage, itemsPerPage));
    }

    /**
     * Поиск токенов с фильтрацией по указанному полю и значению.
     *
     * @param currentPage номер страницы (0-based)
     * @param itemsPerPage количество элементов на странице
     * @param find строка поиска (паттерн)
     * @param findBy поле для поиска: "id", "token" или "user"
     * @return контейнер {@link MyResponse} с результатами и общим количеством
     */
    @Override
    public MyResponse<VerificationToken> find(Integer currentPage, Integer itemsPerPage,
                                              String find, String findBy) {
        switch (findBy) {
            case "id":
                return verificationTokenDAO.findWhereIdLike(currentPage, itemsPerPage, find);
            case "token":
                return verificationTokenDAO.findWhereTokenLike(currentPage, itemsPerPage, find);
            case "user":
                return verificationTokenDAO.findWhereUserLike(currentPage, itemsPerPage, find);
            default:
                return new MyResponse<>(List.of(), 0);
        }
    }

    /**
     * Пагинация всех токенов с сортировкой по указанному полю.
     *
     * @param currentPage номер страницы (0-based)
     * @param itemsPerPage количество элементов на странице
     * @param sortBy поле для сортировки
     * @param sortDirection направление сортировки: "asc" или "desc"
     * @return страница отсортированных токенов
     */
    @Override
    public Page<VerificationToken> findAllSortedBy(Integer currentPage, Integer itemsPerPage,
                                                   String sortBy, String sortDirection) {
        return repository.findAll(
                PageRequest.of(currentPage, itemsPerPage,
                        "asc".equals(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC,
                        sortBy)
        );
    }

    /**
     * Поиск и сортировка токенов в памяти после фильтрации.
     *
     * @param currentPage номер страницы (0-based)
     * @param itemsPerPage количество элементов на странице
     * @param find строка поиска (паттерн)
     * @param findBy поле для поиска
     * @param sortBy поле для сортировки
     * @param sortDirection направление сортировки: "asc" или "desc"
     * @return результат поиска и сортировки в {@link MyResponse}
     */
    @Override
    public MyResponse<VerificationToken> findAndSort(Integer currentPage, Integer itemsPerPage,
                                                     String find, String findBy,
                                                     String sortBy, String sortDirection) {
        MyResponse<VerificationToken> response = find(currentPage, itemsPerPage, find, findBy);
        Comparator<VerificationToken> comparator;
        switch (sortBy) {
            case "id":
                comparator = Comparator.comparing(VerificationToken::getId);
                break;
            case "token":
                comparator = Comparator.comparing(VerificationToken::getToken);
                break;
            case "user":
                comparator = Comparator.comparing(VerificationToken::getUserName);
                break;
            default:
                return response;
        }
        response.getContent().sort(
                "asc".equals(sortDirection) ? comparator : comparator.reversed()
        );
        return response;
    }

    @Override
    public Long getCount() {
        return repository.count();
    }

    /**
     * Поиск токена по его идентификатору.
     *
     * @param id идентификатор токена
     * @return найденный токен или {@code null}, если не найден
     */
    public VerificationToken findById(Long id) {
        return id == null ? null : repository.findById(id).orElse(null);
    }
}