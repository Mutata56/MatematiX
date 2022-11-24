package mutata.com.github.MatematixProject.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Table(name = "comments")
@Data
@Entity
@NoArgsConstructor
public class Comment {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "content")
    private String content;

    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "author")
    private String author;

    @ManyToOne
    @JoinColumn(name = "receiver")
    private User receiver;

    @Column(name = "rating")
    private long rating;

}
