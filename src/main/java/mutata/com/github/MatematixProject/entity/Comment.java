package mutata.com.github.MatematixProject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private Date date;

    @Transient
    private String stringDate;

    @Column(name = "author")
    private String author;

    @ManyToOne
    @JoinColumn(name = "receiver")
    @JsonIgnore
    private User receiver;

    @Column(name = "rating")
    private long rating;




}
