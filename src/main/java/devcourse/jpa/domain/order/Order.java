package devcourse.jpa.domain.order;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "memo")
    private String memo;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "order_date_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime orderDateTime;

    // member_fk
    @Column(name = "member_id", insertable = false, updatable = false)
    private Long memberId;

    // 연관관계 매핑
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    public void setMember(Member member) {
        if (Objects.nonNull(this.member)) {
            member.getOrders().remove(this);
        }
        this.member = member;
        member.getOrders().add(this); // member 에도 order 세팅. (for 양방향 매핑)
    }
}
