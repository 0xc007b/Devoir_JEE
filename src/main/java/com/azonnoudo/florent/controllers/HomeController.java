package com.azonnoudo.florent.controllers;

import com.azonnoudo.florent.models.CategorieAnnonce;
import com.azonnoudo.florent.services.AnnonceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private AnnonceService annonceService;

    @GetMapping("/")
    public String home(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) CategorieAnnonce categorie,
            Model model) {
        
        // Ajouter les annonces à la page d'accueil
        if (categorie != null) {
            model.addAttribute("annonces", annonceService.getAnnoncesByCategorie(categorie, page, size).getContent());
            model.addAttribute("categorieActuelle", categorie);
        } else {
            model.addAttribute("annonces", annonceService.getAllAnnonces(page, size).getContent());
        }
        
        // Ajouter les informations de pagination
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", annonceService.getAllAnnonces(page, size).getTotalPages());
        model.addAttribute("categories", CategorieAnnonce.values());
        
        return "index";
    }
}
