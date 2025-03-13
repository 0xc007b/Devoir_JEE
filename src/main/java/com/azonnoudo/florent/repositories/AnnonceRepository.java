package com.azonnoudo.florent.repositories;

import com.azonnoudo.florent.models.Annonce;
import com.azonnoudo.florent.models.CategorieAnnonce;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnonceRepository extends JpaRepository<Annonce, Long> {
    Page<Annonce> findByCategorieOrderByImportanceDescDateCreationDesc(CategorieAnnonce categorie, Pageable pageable);
    Page<Annonce> findAllByOrderByImportanceDescDateCreationDesc(Pageable pageable);
}
