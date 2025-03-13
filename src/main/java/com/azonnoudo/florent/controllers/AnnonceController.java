package com.azonnoudo.florent.controllers;

import com.azonnoudo.florent.models.Annonce;
import com.azonnoudo.florent.models.CategorieAnnonce;
import com.azonnoudo.florent.services.AnnonceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/annonces")
public class AnnonceController {

    @Autowired
    private AnnonceService annonceService;

    @GetMapping
    public String listAnnonces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) CategorieAnnonce categorie,
            Model model) {

        Page<Annonce> annoncesPage;
        if (categorie != null) {
            annoncesPage = annonceService.getAnnoncesByCategorie(categorie, page, size);
            model.addAttribute("categorieActuelle", categorie);
        } else {
            annoncesPage = annonceService.getAllAnnonces(page, size);
        }

        model.addAttribute("annonces", annoncesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", annoncesPage.getTotalPages());
        model.addAttribute("categories", CategorieAnnonce.values());

        return "annonces/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("annonce", new Annonce());
        model.addAttribute("categories", CategorieAnnonce.values());
        return "annonces/form";
    }

    @PostMapping
    public String saveAnnonce(
            @Valid @ModelAttribute Annonce annonce,
            BindingResult result,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("categories", CategorieAnnonce.values());
            return "annonces/form";
        }

        try {
            annonceService.saveAnnonce(annonce, file);
            redirectAttributes.addFlashAttribute("success", "Annonce enregistrée avec succès");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement du fichier: " + e.getMessage());
        }

        return "redirect:/annonces";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Annonce annonce = annonceService.getAnnonceById(id);
        model.addAttribute("annonce", annonce);
        model.addAttribute("categories", CategorieAnnonce.values());
        return "annonces/form";
    }

    @GetMapping("/{id}")
    public String viewAnnonce(@PathVariable Long id, Model model) {
        Annonce annonce = annonceService.getAnnonceById(id);
        model.addAttribute("annonce", annonce);
        return "annonces/view";
    }

    @GetMapping("/{id}/delete")
    public String deleteAnnonce(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            annonceService.deleteAnnonce(id);
            redirectAttributes.addFlashAttribute("success", "Annonce supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/annonces";
    }

    @GetMapping("/files/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("file-uploads").resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
