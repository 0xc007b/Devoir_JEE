package com.azonnoudo.florent.services;

import com.azonnoudo.florent.models.Annonce;
import com.azonnoudo.florent.models.CategorieAnnonce;
import com.azonnoudo.florent.repositories.AnnonceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AnnonceService {

    @Autowired
    private AnnonceRepository annonceRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public Page<Annonce> getAllAnnonces(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return annonceRepository.findAllByOrderByImportanceDescDateCreationDesc(pageable);
    }

    public Page<Annonce> getAnnoncesByCategorie(CategorieAnnonce categorie, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return annonceRepository.findByCategorieOrderByImportanceDescDateCreationDesc(categorie, pageable);
    }

    public Annonce getAnnonceById(Long id) {
        return annonceRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Annonce non trouvée avec l'ID: " + id));
    }

    @Transactional
    public Annonce saveAnnonce(Annonce annonce, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            annonce.setFichierPath(fileName);
        }

        if (annonce.getDateCreation() == null) {
            annonce.setDateCreation(LocalDateTime.now());
        }
        annonce.setDateModification(LocalDateTime.now());

        return annonceRepository.save(annonce);
    }

    @Transactional
    public void deleteAnnonce(Long id) {
        Annonce annonce = getAnnonceById(id);

        // Supprimer le fichier s'il existe
        if (annonce.getFichierPath() != null) {
            Path filePath = Paths.get(uploadDir).resolve(annonce.getFichierPath());
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de la suppression du fichier", e);
            }
        }

        annonceRepository.deleteById(id);
    }
}
