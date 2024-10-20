package com.project.demo.logic.entity.category;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE %?1%")
    List<Category> findCategoriesWithCharacterInName(String character);

    @Query("SELECT c FROM Category c WHERE c.name = ?1")
    Optional<Category> findByName(String name);

    Optional<Category> findByDescription(String description);



}
