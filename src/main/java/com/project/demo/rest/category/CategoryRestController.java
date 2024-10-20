package com.project.demo.rest.category;


import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.category.CategoryRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.order.Order;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/category")
public class CategoryRestController {
    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole( 'SUPER_ADMIN')")
    public Category addCategory(@RequestBody Category category) {
        return categoryRepository.save(category);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
//    public List<Category> getAllCategories() {
//        return categoryRepository.findAll();
//    }

    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Category> categoriesPage = categoryRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(categoriesPage.getTotalPages());
        meta.setTotalElements(categoriesPage.getTotalElements());
        meta.setPageNumber(categoriesPage.getNumber() + 1);
        meta.setPageSize(categoriesPage.getSize());

        return new GlobalResponseHandler().handleResponse("Category retrieved successfully",
                categoriesPage.getContent(), HttpStatus.OK, meta);
    }



    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole( 'SUPER_ADMIN')")
    public Category updateCategory(@PathVariable Integer id, @RequestBody Category category) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    Optional.ofNullable(category.getName()).ifPresent(existingCategory::setName);
                    Optional.ofNullable(category.getDescription()).ifPresent(existingCategory::setDescription);
                    return categoryRepository.save(existingCategory);
                })
                .orElseGet(() -> {
                    category.setId(id);
                    return categoryRepository.save(category);
                });
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public void deleteCategory(@PathVariable Integer id) {
        categoryRepository.deleteById(id);
    }

}
