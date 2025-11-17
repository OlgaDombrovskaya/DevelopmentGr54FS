package de.ait.training.service;

import org.springframework.web.multipart.MultipartFile;

public interface CarService {

    void attachImage(Long id, MultipartFile file);
}
