package mutata.com.github.MatematixProject.dao;

import mutata.com.github.MatematixProject.entity.token.ResetPasswordToken;
import mutata.com.github.MatematixProject.entity.token.VerificationToken;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@Component
@Transactional(readOnly = true)
public class ResetPasswordDAO {
    private final EntityManager entityManager;

    @Autowired
    public ResetPasswordDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    public MyResponse<ResetPasswordToken> findWhereIdLike(Integer currentPage, Integer itemsPerPage, String pattern) {

        Session session = entityManager.unwrap(Session.class); // Открытие сессии
        Query query = entityManager.createQuery("SELECT resetPasswordToken FROM ResetPasswordToken resetPasswordToken WHERE resetPasswordToken.id = :pattern", ResetPasswordToken.class); // MySQL запрос для получения пользователей с именем, подходящим под паттерн, указание типа результата посредством User.class
        query.setParameter("pattern",Long.parseLong(pattern)); // Установка параметра (паттерн)
        query.setFirstResult((currentPage) * itemsPerPage); // Первые учитываемые результаты будут идти с номера currentPage * itemsPerPage
        query.setMaxResults(itemsPerPage); // Всего может быть itemsPerPage результатов
        Query countQuery = entityManager.createQuery("SELECT COUNT(resetPasswordToken) FROM ResetPasswordToken resetPasswordToken WHERE resetPasswordToken.id = :pattern",Long.class); // MySQL запрос для подсчёта кол-во пользователей с именем, подходяящим под паттерн, указание типа результата посредством Long.class
        countQuery.setParameter("pattern",Long.parseLong(pattern)); // Установка параметра (паттерн)
        long total = (Long) countQuery.getSingleResult();
        return new MyResponse<ResetPasswordToken>(query.getResultList(),total); // Возвращение DAO<User>
    }

    public MyResponse<ResetPasswordToken> findWhereTokenLike(Integer currentPage, Integer itemsPerPage, String pattern) {
        pattern = "%" + pattern + "%";
        Session session = entityManager.unwrap(Session.class); // Открытие сессии
        Query query = entityManager.createQuery("SELECT resetPasswordToken FROM ResetPasswordToken resetPasswordToken WHERE resetPasswordToken.token LIKE :pattern", ResetPasswordToken.class); // MySQL запрос для получения пользователей с именем, подходящим под паттерн, указание типа результата посредством User.class
        query.setParameter("pattern",pattern); // Установка параметра (паттерн)
        query.setFirstResult((currentPage) * itemsPerPage); // Первые учитываемые результаты будут идти с номера currentPage * itemsPerPage
        query.setMaxResults(itemsPerPage); // Всего может быть itemsPerPage результатов
        Query countQuery = entityManager.createQuery("SELECT COUNT(resetPasswordToken) FROM ResetPasswordToken resetPasswordToken WHERE resetPasswordToken.token LIKE :pattern",Long.class); // MySQL запрос для подсчёта кол-во пользователей с именем, подходяящим под паттерн, указание типа результата посредством Long.class
        countQuery.setParameter("pattern",pattern); // Установка параметра (паттерн)
        long total = (Long) countQuery.getSingleResult();
        return new MyResponse<ResetPasswordToken>(query.getResultList(),total); // Возвращение DAO<User>
    }

    public MyResponse<ResetPasswordToken> findWhereUserLike(Integer currentPage, Integer itemsPerPage, String pattern) {
        pattern = "%" + pattern + "%";
        Session session = entityManager.unwrap(Session.class); // Открытие сессии
        Query query = entityManager.createQuery("SELECT resetPasswordToken FROM ResetPasswordToken resetPasswordToken WHERE resetPasswordToken.user.name LIKE :pattern", ResetPasswordToken.class); // MySQL запрос для получения пользователей с именем, подходящим под паттерн, указание типа результата посредством User.class
        query.setParameter("pattern",pattern); // Установка параметра (паттерн)
        query.setFirstResult((currentPage) * itemsPerPage); // Первые учитываемые результаты будут идти с номера currentPage * itemsPerPage
        query.setMaxResults(itemsPerPage); // Всего может быть itemsPerPage результатов
        Query countQuery = entityManager.createQuery("SELECT COUNT(resetPasswordToken) FROM ResetPasswordToken resetPasswordToken WHERE resetPasswordToken.user.name LIKE :pattern",Long.class); // MySQL запрос для подсчёта кол-во пользователей с именем, подходяящим под паттерн, указание типа результата посредством Long.class
        countQuery.setParameter("pattern",pattern); // Установка параметра (паттерн)
        long total = (Long) countQuery.getSingleResult();
        return new MyResponse<ResetPasswordToken>(query.getResultList(),total); // Возвращение DAO<User>
    }
}
