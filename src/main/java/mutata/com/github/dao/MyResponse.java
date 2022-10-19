package mutata.com.github.dao;

import java.util.List;

public class MyResponse <T> {
    private long total;
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