package mutata.com.github.MatematixProject.dao;
import mutata.com.github.MatematixProject.entity.token.ResetPasswordToken;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * DAO для работы с токенами сброса пароля (ResetPasswordToken).
 * <p>Предоставляет методы для поиска и получения токенов сброса пароля
 * с поддержкой пагинации и подсчётом общего числа записей.</p>
 *
 * <p>По умолчанию методы работают в режиме только для чтения
 * благодаря аннотации {@link Transactional @Transactional(readOnly = true)}.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 */
@Component
@Transactional(readOnly = true)
public class ResetPasswordDAO {

    /**
     * Менеджер сущностей для выполнения запросов JPA/Hibernate.
     */
    private final EntityManager entityManager;

    /**
     * Внедрение {@link EntityManager} через конструктор.
     *
     * @param entityManager менеджер сущностей JPA
     */
    @Autowired
    public ResetPasswordDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Ищет токены сброса пароля по точному совпадению ID.
     * <p>Алгоритм работы:</p>
     * <ol>
     *   <li>Формирует HQL-запрос с условием <code>WHERE resetPasswordToken.id = :pattern</code>;</li>
     *   <li>Устанавливает параметр <code>pattern</code>, преобразуя строку в Long;</li>
     *   <li>Настраивает пагинацию: смещение <code>firstResult</code> и <code>maxResults</code>;</li>
     *   <li>Выполняет аналогичный запрос <code>COUNT</code>, чтобы получить общее число подходящих записей;</li>
     *   <li>Возвращает экземпляр {@link MyResponse} с найденными токенами и общим количеством записей.</li>
     * </ol>
     *
     * @param currentPage   индекс текущей страницы (0-based)
     * @param itemsPerPage  количество элементов на странице
     * @param pattern       строковое представление ID (должно быть числом)
     * @return контейнер {@link MyResponse} со списком найденных токенов и общим числом записей
     * @throws NumberFormatException если переданный pattern не является числовой строкой
     * @see MyResponse
     */
    public MyResponse<ResetPasswordToken> findWhereIdLike(Integer currentPage, Integer itemsPerPage, String pattern) {

        Session session = entityManager.unwrap(Session.class); // Открытие сессии
        Query query = entityManager.createQuery(
                "SELECT resetPasswordToken FROM ResetPasswordToken resetPasswordToken " +
                        "WHERE resetPasswordToken.id = :pattern", ResetPasswordToken.class);
        query.setParameter("pattern", Long.parseLong(pattern));
        query.setFirstResult(currentPage * itemsPerPage);
        query.setMaxResults(itemsPerPage);

        Query countQuery = entityManager.createQuery(
                "SELECT COUNT(resetPasswordToken) FROM ResetPasswordToken resetPasswordToken " +
                        "WHERE resetPasswordToken.id = :pattern", Long.class);
        countQuery.setParameter("pattern", Long.parseLong(pattern));
        long total = (Long) countQuery.getSingleResult();

        return new MyResponse<>(query.getResultList(), total);
    }

    /**
     * Ищет токены сброса пароля по шаблону токена (LIKE).
     * <p>Алгоритм аналогичен {@link #findWhereIdLike}, за исключением:
     * <ul>
     *   <li>Используется условие <code>token LIKE :pattern</code>;</li>
     *   <li>К переданному pattern добавляются символы '%' для поиска подстрок.</li>
     * </ul>
     * </p>
     *
     * @param currentPage   индекс текущей страницы (0-based)
     * @param itemsPerPage  количество элементов на странице
     * @param pattern       шаблон для поиска в поле token (без '%')
     * @return контейнер {@link MyResponse} со списком найденных токенов и общим числом записей
     */
    public MyResponse<ResetPasswordToken> findWhereTokenLike(Integer currentPage, Integer itemsPerPage, String pattern) {
        pattern = "%" + pattern + "%";
        Session session = entityManager.unwrap(Session.class); // Открытие сессии
        Query query = entityManager.createQuery(
                "SELECT resetPasswordToken FROM ResetPasswordToken resetPasswordToken " +
                        "WHERE resetPasswordToken.token LIKE :pattern", ResetPasswordToken.class);
        query.setParameter("pattern", pattern);
        query.setFirstResult(currentPage * itemsPerPage);
        query.setMaxResults(itemsPerPage);

        Query countQuery = entityManager.createQuery(
                "SELECT COUNT(resetPasswordToken) FROM ResetPasswordToken resetPasswordToken " +
                        "WHERE resetPasswordToken.token LIKE :pattern", Long.class);
        countQuery.setParameter("pattern", pattern);
        long total = (Long) countQuery.getSingleResult();

        return new MyResponse<>(query.getResultList(), total);
    }

    /**
     * Ищет токены сброса пароля по шаблону имени пользователя.
     * <p>Использует условие <code>user.name LIKE :pattern</code> для поиска
     * по связанному сущностному полю user.name.</p>
     *
     * @param currentPage   индекс текущей страницы (0-based)
     * @param itemsPerPage  количество элементов на странице
     * @param pattern       шаблон для поиска в имени пользователя (без '%')
     * @return контейнер {@link MyResponse} со списком найденных токенов и общим числом записей
     */
    public MyResponse<ResetPasswordToken> findWhereUserLike(Integer currentPage, Integer itemsPerPage, String pattern) {
        pattern = "%" + pattern + "%";
        Session session = entityManager.unwrap(Session.class); // Открытие сессии
        Query query = entityManager.createQuery(
                "SELECT resetPasswordToken FROM ResetPasswordToken resetPasswordToken " +
                        "WHERE resetPasswordToken.user.name LIKE :pattern", ResetPasswordToken.class);
        query.setParameter("pattern", pattern);
        query.setFirstResult(currentPage * itemsPerPage);
        query.setMaxResults(itemsPerPage);

        Query countQuery = entityManager.createQuery(
                "SELECT COUNT(resetPasswordToken) FROM ResetPasswordToken resetPasswordToken " +
                        "WHERE resetPasswordToken.user.name LIKE :pattern", Long.class);
        countQuery.setParameter("pattern", pattern);
        long total = (Long) countQuery.getSingleResult();

        return new MyResponse<>(query.getResultList(), total);
    }
}
