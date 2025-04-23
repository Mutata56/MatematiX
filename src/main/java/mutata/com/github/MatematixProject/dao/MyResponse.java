package mutata.com.github.MatematixProject.dao;

import java.util.List;

/**
 * DAO - data access object - объект, служащий для получения данных из БД. В данном случае MyResponse - генерик, используемый повсоместно как типичный контейнер для данных
 *
 */
public class MyResponse <T> {
    /**
     * Общее кол-во чего-либо
     */
    private long total;
    /**
     * Список чего-либо
     */
    private List<T> content;

    public MyResponse(List<T> content,long total) {
        this.content = content;
        this.total = total;
    }

    public long getTotal() {
        return total;
    }

    public List<T> getContent() {
        return content;
    }

    void setTotal(int total) {
        this.total = total;
    }

    void setContent(List<T> content) {
        this.content = content;
    }
}