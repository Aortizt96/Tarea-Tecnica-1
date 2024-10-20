package com.project.demo.rest.product;


import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.category.CategoryRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.order.Order;
import com.project.demo.logic.entity.product.Product;
import com.project.demo.logic.entity.product.ProductRepository;
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
@RequestMapping("/product")
public class ProductRestController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole( 'SUPER_ADMIN')")
    public Product addProduct(@RequestBody Product product) {
        Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategory(category);
        return productRepository.save(product);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
//    public List<Product> getAllProducts() {
//        return productRepository.findAll();
//    }

    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Product> productsPage = productRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(productsPage.getTotalPages());
        meta.setTotalElements(productsPage.getTotalElements());
        meta.setPageNumber(productsPage.getNumber() + 1);
        meta.setPageSize(productsPage.getSize());

        return new GlobalResponseHandler().handleResponse("Order retrieved successfully",
                productsPage.getContent(), HttpStatus.OK, meta);
    }



    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole( 'SUPER_ADMIN')")
    public Product updateProduct(@PathVariable Integer id, @RequestBody Product product) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    Optional.ofNullable(product.getName()).ifPresent(existingProduct::setName);
                    Optional.ofNullable(product.getDescription()).ifPresent(existingProduct::setDescription);
                    Optional.ofNullable(product.getPrice()).ifPresent(existingProduct::setPrice);
                    Optional.ofNullable(product.getStock()).ifPresent(existingProduct::setStock);
                    Optional.ofNullable(product.getCategory()).ifPresent(existingProduct::setCategory);
                    
                    Category category = categoryRepository.findById(product.getCategory().getId())
                            .orElseThrow(() -> new RuntimeException("Category not found"));
                    existingProduct.setCategory(category);
                    return productRepository.save(existingProduct);
                })
                .orElseGet(() -> {
                    Category category = categoryRepository.findById(product.getCategory().getId())
                            .orElseThrow(() -> new RuntimeException("Category not found"));
                    product.setCategory(category);
                    product.setId(id);
                    return productRepository.save(product);
                });
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public void deleteProduct(@PathVariable Integer id) {
        productRepository.deleteById(id);
    }





}
