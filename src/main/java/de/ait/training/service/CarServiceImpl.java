package de.ait.training.service;

import de.ait.training.model.Car;
import de.ait.training.repository.CarRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository repository;
    private final String uploadDirName;
    private final String hostUrl;

    public CarServiceImpl(
            CarRepository repository,
            @Value("${upload.dir}") String uploadDirName,
            @Value("${host.url}") String hostUrl
    ) {
        this.repository = repository;
        this.uploadDirName = uploadDirName;
        this.hostUrl = hostUrl;
    }

    @Override
    @Transactional
    public void attachImage(Long id, MultipartFile file) {
        System.out.println(">>> attachImage called, id = " + id
                + ", file = " + (file != null ? file.getOriginalFilename() : "null"));

        File uploadDir = new File(uploadDirName);
        uploadDir.mkdirs();

        String uniqueFileName = generateUniqueFileName(file);
        File targetFile = new File(uploadDir, uniqueFileName);

        try (FileOutputStream out = new FileOutputStream(targetFile)) {
            out.write(file.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        // Чтобы получить загруженную картинку обратно, клиент отправляет запрос:
        // GET  ->  http://localhost:8080/b34c0d8d-bfc5-4e65-9498-9cf31b15feb4-bmw.jpg
        Car car = repository.findById(id).orElseThrow(
                // По-хорошему здесь нужно выбрасывать пользовательский эксепшен
                // и обрабатывать его в глобальном обработчике эксепшенов
                () -> new IllegalArgumentException("Car with id " + id + " not found")
        );
        car.setImageUrl(hostUrl + uniqueFileName);
    }

    private String generateUniqueFileName(MultipartFile file) {
        String origFileName = file.getOriginalFilename();
        String randomUuid = UUID.randomUUID().toString();
        return randomUuid + "-" + origFileName;
    }
}
