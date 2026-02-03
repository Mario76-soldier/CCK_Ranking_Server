package com.cubingclub.ranking.CubeEvent.Service;

import com.cubingclub.ranking.CubeEvent.Entity.ColorType;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.CubeEvent.Repository.CubeEventRepository;
import com.cubingclub.ranking.CubeEvent.DTO.CubeEventDTO;
import com.cubingclub.ranking.Exception.Entity.ErrorCode;
import com.cubingclub.ranking.Exception.Exceptions.CustomizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CubeEventService {
    @Value("${spring.img.path}")
    String path;

    private final CubeEventRepository cubeEventRepository;

    @Transactional(readOnly = true)
    public List<CubeEventDTO> getCubeEvent(){
        return cubeEventRepository.findAll()
                .stream()
                .map(CubeEventDTO::new)
                .toList();
    }

    @Transactional
    public void createCubeEvent(String eventName, String avgCalc, ColorType colorType, MultipartFile file) {
        CubeEvent cubeEvent = CubeEvent.builder()
                .eventName(eventName)
                .avgCalc(avgCalc)
                .colorType(colorType)
                .build();

        try {
            if (file != null && !file.isEmpty()) {
                Path folder = Paths.get(path);
                if (!Files.exists(folder)) Files.createDirectories(folder);

                Path filePath = resolvePath(eventName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            }
            cubeEventRepository.save(cubeEvent);
        }

        catch (IOException e) {
            throw new CustomizedException(ErrorCode.CUBE_EVENT_SAVE_FAILED, e.getMessage());
        }
    }

    @Transactional
    public void updateCubeEvent(String eventName, String avgCalc, ColorType colorType, MultipartFile file) {
        CubeEvent cubeEvent = cubeEventRepository.findById(eventName)
                .orElseThrow(() -> new CustomizedException(ErrorCode.CUBE_EVENT_NOT_FOUND));

        Path filePath = resolvePath(eventName);

        try {
            if (file != null && !file.isEmpty()) {
                Files.createDirectories(filePath.getParent());
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            cubeEvent.update(avgCalc, colorType);

        } catch (IOException e) {
            throw new CustomizedException(ErrorCode.CUBE_EVENT_UPDATE_FAILED, e.getMessage());
        }
    }

    @Transactional
    public void deleteCubeEvent(String eventName) {
        CubeEvent cubeEvent = cubeEventRepository.findById(eventName)
                .orElseThrow(() -> new CustomizedException(ErrorCode.CUBE_EVENT_NOT_FOUND));

        Path filePath = resolvePath(eventName);

        cubeEventRepository.delete(cubeEvent);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new CustomizedException(ErrorCode.CUBE_EVENT_DELETE_FAILED, e.getMessage());
        }
    }

    private Path resolvePath(String eventName) {
        return Paths.get(path).resolve(eventName + ".svg");
    }
}
