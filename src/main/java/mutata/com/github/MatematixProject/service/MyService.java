package mutata.com.github.MatematixProject.service;

import mutata.com.github.MatematixProject.dao.MyResponse;
import org.springframework.data.domain.Page;

public interface MyService<T> {

    public Page<T> findAllReturnPage(Integer currentPage,Integer itemsPerPage);

    public MyResponse<T> find(Integer currentPage, Integer itemsPerPage, String find, String findBy);

    public Page<T> findAllSortedBy(Integer currentPage, Integer itemsPerPage, String sortBy,String sortDirection);

    MyResponse findAndSort(Integer currentPage, Integer itemsPerPage, String find, String findBy, String sortBy,String sortDirection);
}
