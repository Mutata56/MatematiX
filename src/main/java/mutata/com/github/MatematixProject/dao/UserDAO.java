package mutata.com.github.MatematixProject.dao;

import mutata.com.github.MatematixProject.entity.User;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@Component
@Transactional(readOnly = true)
public class UserDAO {

    private final EntityManager entityManager;

    @Autowired
    public UserDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public MyResponse<User> findWhereNameLike(Integer currentPage,Integer itemsPerPage,String pattern) {
        pattern = "%" + pattern + "%";
        Session session = entityManager.unwrap(Session.class);
        Query query = entityManager.createQuery("SELECT user FROM User user WHERE user.name LIKE :pattern",User.class);
        query.setParameter("pattern",pattern);
        query.setFirstResult((currentPage) * itemsPerPage);
        query.setMaxResults(itemsPerPage);
        Query countQuery = entityManager.createQuery("SELECT COUNT(user) FROM User user WHERE user.name LIKE :pattern",Long.class);
        countQuery.setParameter("pattern",pattern);
        long total = (Long) countQuery.getSingleResult();
        return new MyResponse<User>(query.getResultList(),total);
    }

    public MyResponse<User> findWhereRoleLike(Integer currentPage,Integer itemsPerPage,String pattern) {
        pattern = pattern.startsWith("админ") || pattern.startsWith("1") || pattern.startsWith("admin") ? "ROLE_ADMIN" : "ROLE_USER";
        Session session = entityManager.unwrap(Session.class);
        Query query = entityManager.createQuery("SELECT user FROM User user WHERE user.role LIKE :pattern",User.class);
        query.setParameter("pattern",pattern);
        query.setFirstResult((currentPage) * itemsPerPage);
        query.setMaxResults(itemsPerPage);
        Query countQuery = entityManager.createQuery("SELECT COUNT(user) FROM User user WHERE user.role LIKE :pattern",Long.class);
        countQuery.setParameter("pattern",pattern);
        long total = (Long) countQuery.getSingleResult();
        return new MyResponse<User>(query.getResultList(),total);
    }

    public MyResponse<User> findWhereBlockedLike(Integer currentPage,Integer itemsPerPage,String pattern) {
        byte patternFindBy = pattern.startsWith("block") || pattern.startsWith("заблок") || pattern.startsWith("1") ? (byte) 1 : (byte) 0;
        Session session = entityManager.unwrap(Session.class);
        Query query = entityManager.createQuery("SELECT user FROM User user WHERE user.blocked = :pattern",User.class);
        query.setParameter("pattern",patternFindBy);
        query.setFirstResult((currentPage) * itemsPerPage);
        query.setMaxResults(itemsPerPage);
        Query countQuery = entityManager.createQuery("SELECT COUNT(user) FROM User user WHERE user.blocked = :pattern",Long.class);
        countQuery.setParameter("pattern",patternFindBy);
        long total = (Long) countQuery.getSingleResult();
        return new MyResponse<User>(query.getResultList(),total);
    }

    public MyResponse<User> findWhereEmailLike(Integer currentPage,Integer itemsPerPage,String pattern) {
        pattern = "%" + pattern + "%";
        Session session = entityManager.unwrap(Session.class);
        Query query = entityManager.createQuery("SELECT user FROM User user WHERE user.email LIKE :pattern",User.class);
        query.setParameter("pattern",pattern);
        query.setFirstResult((currentPage) * itemsPerPage);
        query.setMaxResults(itemsPerPage);
        Query countQuery = entityManager.createQuery("SELECT COUNT(user) FROM User user WHERE user.email LIKE :pattern",Long.class);
        countQuery.setParameter("pattern",pattern);
        long total = (Long) countQuery.getSingleResult();
        return new MyResponse<User>(query.getResultList(),total);
    }

    public MyResponse<User> findWhereEnabledLike(Integer currentPage, Integer itemsPerPage, String pattern) {
        byte patternFindBy = pattern.startsWith("activ") || pattern.startsWith("активиро") || pattern.startsWith("1") ? (byte) 1 : (byte) 0;
        Session session = entityManager.unwrap(Session.class);
        Query query = entityManager.createQuery("SELECT user FROM User user WHERE user.enabled = :pattern",User.class);
        query.setParameter("pattern",patternFindBy);
        query.setFirstResult((currentPage) * itemsPerPage);
        query.setMaxResults(itemsPerPage);
        Query countQuery = entityManager.createQuery("SELECT COUNT(user) FROM User user WHERE user.enabled = :pattern",Long.class);
        countQuery.setParameter("pattern",patternFindBy);
        long total = (Long) countQuery.getSingleResult();
        return new MyResponse<User>(query.getResultList(),total);
    }
}
