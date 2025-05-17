package com.cubingclub.ranking.Round.DTO;

import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.Round.Entity.RoundGroup;
import com.cubingclub.ranking.Round.Repository.RoundRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoundGroupDTO {
    private Integer idx;
    private Integer roundIdx;
    private String groupName;
    public RoundGroupDTO(RoundGroup entity){
        this.idx= entity.getIdx();
        this.roundIdx=entity.getRound().getIdx();
        this.groupName=entity.getGroupName();
    }

    public RoundGroupDTO(Integer roundIdx, String groupName) {
        this.roundIdx=roundIdx;
        this.groupName=groupName;
    }

    public RoundGroup toEntity(RoundRepository roundRepository){
        return RoundGroup.builder()
                .idx(idx)
                .round(roundRepository.findByIdx(roundIdx))
                .groupName(groupName)
                .build();
    }
}
