package mutata.com.github.MatematixProject.service;

import mutata.com.github.MatematixProject.dao.MyResponse;
import org.springframework.data.domain.Page;

/**
 * Сервис для пагинации
 * @param <T>
 */
public interface MyService<T> {

    /**
     * Найти всех (без каких-либо исключений) на currentPage, itemsPerPage единиц на страницу
     * @param currentPage - страница, на которой нужно найти сущность
     * @param itemsPerPage - сколько сущностей должно быть на странице
     * @return - страница с itemsPerPage сущностями, начиная с currentPage страницы
     */

    public Page<T> findAllReturnPage(Integer currentPage,Integer itemsPerPage);

    /**
     * Найти сущностей по параметру findBy, поиск по паттерну find  на currentPage, itemsPerPage единиц на страницу
     * @param currentPage - страница, на которой нужно найти сущность
     * @param itemsPerPage - сколько сущностей должно быть на странице
     * @param findBy - по какому параметру искать (например имя, рейтинг и т.д.)
     * @param find - паттерн, по которому нужно искать (например Иванов, 32)
     * @return - страница с itemsPerPage сущностями, начиная с currentPage страницы, с поиском по параметру findBy и паттерну find
     */

    public MyResponse<T> find(Integer currentPage, Integer itemsPerPage, String find, String findBy);

    /**
     * Найти всех сущностей без исключения на currentPage, itemsPerPage единиц на страницу с сортировкой по парамтеру sortBy в направлении sortDirection
     * @param currentPage - страница, на которой нужно найти сущность
     * @param itemsPerPage - сколько сущностей должно быть на странице
     * @param sortBy - по какому параметру сортировать (например имя, рейтинг и т.д.)
     * @param sortDirection - направление сортировки (например возраст., убыв.)
     * @return - страница с itemsPerPage сущностями, начиная с currentPage страницы, с сортировкой по параметру sortBy и с направлением сортровки sortDirection
     */

    public Page<T> findAllSortedBy(Integer currentPage, Integer itemsPerPage, String sortBy,String sortDirection);

    /**
     * Найти сущностей по параметру findBy, поиск по паттерну find  на currentPage, itemsPerPage единиц на страницу с сортировкой по парамтеру sortBy в направлении sortDirection
     * @param currentPage - страница, на которой нужно найти сущность
     * @param itemsPerPage - сколько сущностей должно быть на странице
     * @param findBy - по какому параметру искать (например имя, рейтинг и т.д.)
     * @param find - паттерн, по которому нужно искать (например Иванов, 32)
     * @param sortBy - по какому параметру сортировать (например имя, рейтинг и т.д.)
     * @param sortDirection - направление сортировки (например возраст., убыв.)
     * @return - страница с itemsPerPage сущностями, начиная с currentPage страницы, с поиском по параметру findBy и паттерну find с сортировкой по парамтеру sortBy в направлении sortDirection
     */

    MyResponse findAndSort(Integer currentPage, Integer itemsPerPage, String find, String findBy, String sortBy,String sortDirection);
}
