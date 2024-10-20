package com.project.demo.logic.entity.product;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product, Integer> {


    @EntityGraph(attributePaths = {"category"})
    Optional<Product> findById(Integer id);



}
