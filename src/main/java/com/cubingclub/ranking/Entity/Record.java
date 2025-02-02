package com.cubingclub.ranking.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@DynamicInsert
@Table
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer idx;
    @Column
    private Integer partIdx;
    @Column(length=40)
    private String userName;
    @Column(length=40)
    private String enUserName;
    @Column(length=30)
    private String email;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn
    private CubeEvent cubeEvent;
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal single;
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal average;
    @Column
    private Long singleRanking;
    @Column
    private Long averageRanking;
    @Column
    private Long stateSingleRanking;
    @Column
    private Long stateAverageRanking;

    @Builder
    public Record(Integer idx, Integer partIdx, String userName, String enUserName, String email, CubeEvent cubeEvent, BigDecimal single, BigDecimal average,
                  Long singleRanking, Long averageRanking, Long stateSingleRanking, Long stateAverageRanking){
        this.idx=idx;
        this.partIdx=partIdx;
        this.userName=userName;
        this.enUserName=enUserName;
        this.email=email;
        this.cubeEvent=cubeEvent;
        this.single=single;
        this.average=average;
        this.singleRanking=singleRanking;
        this.averageRanking=averageRanking;
        this.stateSingleRanking=stateSingleRanking;
        this.stateAverageRanking=stateAverageRanking;
    }
}
