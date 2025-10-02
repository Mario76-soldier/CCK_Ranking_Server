package com.cubingclub.ranking.CubeEvent.Service;

import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.CubeEvent.Repository.CubeEventRepository;
import com.cubingclub.ranking.CubeEvent.DTO.CubeEventDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CubeEventService {
    private final CubeEventRepository cubeEventRepository;
    public List<CubeEventDTO> getCubeEvent(){
        List<CubeEvent> list= cubeEventRepository.findAllBy();
        List<CubeEventDTO> listDTO=new ArrayList<>();
        for (CubeEvent cubeEvent:list) {
            listDTO.add(new CubeEventDTO(cubeEvent));
        }
        return listDTO;
    }
    public CubeEvent setCubeEvent(CubeEventDTO cubeEventDTO){
        return cubeEventRepository.save(cubeEventDTO.toEntity());
    }
}
