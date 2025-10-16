package com.cubingclub.ranking.CubeEvent.Service;

import com.cubingclub.ranking.CubeEvent.Entity.ColorType;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.CubeEvent.Repository.CubeEventRepository;
import com.cubingclub.ranking.CubeEvent.DTO.CubeEventDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CubeEventService {
    @Value("${spring.img.path}")
    String path;
    private final CubeEventRepository cubeEventRepository;
    public List<CubeEventDTO> getCubeEvent(){
        List<CubeEvent> list= cubeEventRepository.findAllBy();
        List<CubeEventDTO> listDTO=new ArrayList<>();
        for (CubeEvent cubeEvent:list) {
            listDTO.add(new CubeEventDTO(cubeEvent));
        }
        return listDTO;
    }
    public CubeEvent setCubeEvent(String eventName, String avgCalc, ColorType colorType, MultipartFile file) throws IOException {
        CubeEvent cubeEvent = CubeEvent.builder()
                .EventName(eventName)
                .AvgCalc(avgCalc)
                .colorType(colorType)
                .build();

        // 파일이 있으면 Docker volume에 저장
        if (file != null && !file.isEmpty()) {
            Path folder = Paths.get(path);
            if (!Files.exists(folder)) Files.createDirectories(folder);

            Path filePath = folder.resolve(eventName + ".svg"); // 파일명 = EventName.svg
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        return cubeEventRepository.save(cubeEvent);
    }
}
