
package mutata.com.github.MatematixProject.service;

import mutata.com.github.MatematixProject.dao.MyResponse;
import org.springframework.data.domain.Page;

/**
 * Универсальный сервис для поиска и пагинации сущностей.
 * <p>Определяет основные операции по получению страниц
 * данных с возможностью фильтрации и сортировки.</p>
 *
 * @param <T> тип сущности, которой управляет сервис
 * @author Khaliullin Cyrill
 * @version 1.0.0
 */
public interface MyService<T> {

    /**
     * Получает страницу всех сущностей без фильтрации.
     *
     * @param currentPage   номер запрашиваемой страницы (0-based)
     * @param itemsPerPage  количество элементов на одной странице
     * @return объект {@link Page} с сущностями типа T и метаданными пагинации
     */
    Page<T> findAllReturnPage(Integer currentPage, Integer itemsPerPage);

    /**
     * Выполняет поиск сущностей по указанному полю и паттерну.
     *
     * @param currentPage   номер запрашиваемой страницы (0-based)
     * @param itemsPerPage  количество элементов на одной странице
     * @param find          строка-паттерн для поиска (например, часть имени)
     * @param findBy        имя поля сущности, в котором выполнять поиск
     * @return контейнер {@link MyResponse} с найденными сущностями и общим числом результатов
     */
    MyResponse<T> find(Integer currentPage, Integer itemsPerPage, String find, String findBy);

    /**
     * Получает страницу всех сущностей с сортировкой.
     *
     * @param currentPage   номер запрашиваемой страницы (0-based)
     * @param itemsPerPage  количество элементов на одной странице
     * @param sortBy        имя поля сущности для сортировки
     * @param sortDirection направление сортировки ("asc" или "desc")
     * @return объект {@link Page} с сущностями типа T и метаданными пагинации
     */
    Page<T> findAllSortedBy(Integer currentPage, Integer itemsPerPage, String sortBy, String sortDirection);

    /**
     * Выполняет комбинированный поиск и сортировку сущностей.
     *
     * @param currentPage   номер запрашиваемой страницы (0-based)
     * @param itemsPerPage  количество элементов на одной странице
     * @param find          строка-паттерн для поиска
     * @param findBy        имя поля сущности, в котором выполнять поиск
     * @param sortBy        имя поля сущности для сортировки
     * @param sortDirection направление сортировки ("asc" или "desc")
     * @return контейнер {@link MyResponse} с найденными, отфильтрованными и отсортированными сущностями
     */
    MyResponse findAndSort(Integer currentPage, Integer itemsPerPage,
                              String find, String findBy,
                              String sortBy, String sortDirection);

    Long getCount();
}