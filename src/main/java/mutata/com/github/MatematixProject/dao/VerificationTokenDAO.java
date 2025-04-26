package mutata.com.github.MatematixProject.dao;

import mutata.com.github.MatematixProject.entity.token.VerificationToken;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * DAO для работы с токенами подтверждения email (VerificationToken).
 * <p>Предоставляет методы для поиска и получения верификационных токенов
 * с поддержкой пагинации и подсчётом общего количества записей.</p>
 *
 * <p>Благодаря аннотации {@link Transactional(readOnly = true)},
 * все операции выполняются в рамках транзакции только для чтения,
 * что оптимизирует работу при выборках.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 */
@Component
@Transactional(readOnly = true)
public class VerificationTokenDAO {

    /**
     * Менеджер сущностей для выполнения JPA-запросов.
     */
    private final EntityManager entityManager;

    /**
     * Внедрение EntityManager через конструктор.
     *
     * @param entityManager JPA EntityManager
     */
    @Autowired
    public VerificationTokenDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Ищет токены подтверждения по точному совпадению ID.
     * <p>Алгоритм работы:</p>
     * <ol>
     *   <li>Формирует HQL-запрос с условием <code>WHERE id = :pattern</code>;</li>
     *   <li>Преобразует строковой pattern в Long и устанавливает параметр;</li>
     *   <li>Настраивает пагинацию с помощью setFirstResult и setMaxResults;</li>
     *   <li>Выполняет запрос COUNT для получения общего количества;</li>
     *   <li>Возвращает {@link MyResponse} с результатами и общим числом записей.</li>
     * </ol>
     *
     * @param currentPage   индекс текущей страницы (0-based)
     * @param itemsPerPage  число элементов на странице
     * @param pattern       строка, содержащая значение ID токена
     * @return контейнер {@link MyResponse} с найденными токенами и общим числом записей
     * @throws NumberFormatException если pattern не может быть преобразован в Long
     */
    public MyResponse<VerificationToken> findWhereIdLike(Integer currentPage, Integer itemsPerPage, String pattern) {
        Session session = entityManager.unwrap(Session.class);
        Query query = entityManager.createQuery(
                "SELECT verificationToken FROM VerificationToken verificationToken WHERE verificationToken.id = :pattern", VerificationToken.class);
        query.setParameter("pattern", Long.parseLong(pattern));
        query.setFirstResult(currentPage * itemsPerPage);
        query.setMaxResults(itemsPerPage);

        Query countQuery = entityManager.createQuery(
                "SELECT COUNT(verificationToken) FROM VerificationToken verificationToken WHERE verificationToken.id = :pattern", Long.class);
        countQuery.setParameter("pattern", Long.parseLong(pattern));
        long total = (Long) countQuery.getSingleResult();

        return new MyResponse<>(query.getResultList(), total);
    }

    /**
     * Ищет токены подтверждения по шаблону значения token (LIKE).
     * <p>Добавляет '%' к началу и концу pattern для поиска подстрок.</p>
     *
     * @param currentPage   индекс текущей страницы (0-based)
     * @param itemsPerPage  число элементов на странице
     * @param pattern       подстрока для поиска в поле token
     * @return контейнер {@link MyResponse} с найденными токенами и общим числом записей
     */
    public MyResponse<VerificationToken> findWhereTokenLike(Integer currentPage, Integer itemsPerPage, String pattern) {
        pattern = "%" + pattern + "%";
        Session session = entityManager.unwrap(Session.class);
        Query query = entityManager.createQuery(
                "SELECT verificationToken FROM VerificationToken verificationToken WHERE verificationToken.token LIKE :pattern", VerificationToken.class);
        query.setParameter("pattern", pattern);
        query.setFirstResult(currentPage * itemsPerPage);
        query.setMaxResults(itemsPerPage);

        Query countQuery = entityManager.createQuery(
                "SELECT COUNT(verificationToken) FROM VerificationToken verificationToken WHERE verificationToken.token LIKE :pattern", Long.class);
        countQuery.setParameter("pattern", pattern);
        long total = (Long) countQuery.getSingleResult();

        return new MyResponse<>(query.getResultList(), total);
    }

    /**
     * Ищет токены подтверждения по шаблону имени пользователя (user.name LIKE pattern).
     *
     * @param currentPage   индекс текущей страницы (0-based)
     * @param itemsPerPage  число элементов на странице
     * @param pattern       подстрока для поиска в имени пользователя
     * @return контейнер {@link MyResponse} с найденными токенами и общим числом записей
     */
    public MyResponse<VerificationToken> findWhereUserLike(Integer currentPage, Integer itemsPerPage, String pattern) {
        pattern = "%" + pattern + "%";
        Session session = entityManager.unwrap(Session.class);
        Query query = entityManager.createQuery(
                "SELECT verificationToken FROM VerificationToken verificationToken WHERE verificationToken.user.name LIKE :pattern", VerificationToken.class);
        query.setParameter("pattern", pattern);
        query.setFirstResult(currentPage * itemsPerPage);
        query.setMaxResults(itemsPerPage);

        Query countQuery = entityManager.createQuery(
                "SELECT COUNT(verificationToken) FROM VerificationToken verificationToken WHERE verificationToken.user.name LIKE :pattern", Long.class);
        countQuery.setParameter("pattern", pattern);
        long total = (Long) countQuery.getSingleResult();

        return new MyResponse<>(query.getResultList(), total);
    }
}