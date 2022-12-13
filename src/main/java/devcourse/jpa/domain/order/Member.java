package devcourse.jpa.domain.order;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "nick_name", nullable = false, length = 30, unique = true)
    private String nickName;

    @Column(name = "age", nullable = false)
    private int age;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "description")
    private String description;

/*    create table member (
        id bigint not null,
        address varchar(255) not null,
        age integer not null,
        description varchar(255),
        name varchar(30) not null,
        nick_name varchar(30) not null,
        primary key (id)
    )*/

    @OneToMany(mappedBy = "member") // 연관관계 주인은 order.member
    private List<Order> orders = new ArrayList<>();

    public void addOrder(Order order) {
        order.setMember(this);
    }
}
