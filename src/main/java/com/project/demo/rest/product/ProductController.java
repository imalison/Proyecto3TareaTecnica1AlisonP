package com.project.demo.rest.product;


import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.category.CategoryRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.preferenceList.PreferenceList;
import com.project.demo.logic.entity.product.Product;
import com.project.demo.logic.entity.product.ProductRepository;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Product> productPage = productRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(productPage.getTotalPages());
        meta.setTotalElements(productPage.getTotalElements());
        meta.setPageNumber(productPage.getNumber() + 1);
        meta.setPageSize(productPage.getSize());

        return new GlobalResponseHandler().handleResponse("Products retrieved successfully",
                productPage.getContent(), HttpStatus.OK, meta);

    }

    @GetMapping("/{Id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getProductById(@PathVariable Long Id, HttpServletRequest request) {
        Optional<Product> foundProduct = productRepository.findById(Id);
        if(foundProduct.isPresent())  {
            return new GlobalResponseHandler().handleResponse(
                    "Product retrieved successfully",
                    foundProduct.get(),
                    HttpStatus.OK,
                    request);
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "Product with id " + Id + " not found",
                    HttpStatus.NOT_FOUND,
                    request);
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated() && hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> addProdcut(@RequestBody Product product, HttpServletRequest request) {
        Product savedProduct = productRepository.save(product);
        return new GlobalResponseHandler().handleResponse(
                "Product successfully saved",
                savedProduct,
                HttpStatus.OK,
                request);
    }

    @PutMapping("/{Id}")
    @PreAuthorize("isAuthenticated() && hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateProduct(@PathVariable Long Id, @RequestBody Product product, HttpServletRequest request) {
        Optional<Product> foundProduct = productRepository.findById(Id);
        if (foundProduct.isPresent()) {
            Product existingProduct = foundProduct.get();
            existingProduct.setName(product.getName());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setStockQuantity(product.getStockQuantity());

            productRepository.save(existingProduct);

            return new GlobalResponseHandler().handleResponse(
                    "Your product was successfully updated",
                    existingProduct,
                    HttpStatus.OK,
                    request);
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "Sorry! Product with id " + Id + " not found",
                    HttpStatus.NOT_FOUND,
                    request);
        }
    }



    @DeleteMapping("/{Id}")
    @PreAuthorize("isAuthenticated() && hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long Id, HttpServletRequest request) {
        Optional<Product> foundProduct = productRepository.findById(Id);
        if(foundProduct.isPresent()) {
            productRepository.deleteById(Id);
            return new GlobalResponseHandler().handleResponse(
                    "Product successfully deleted",
                    HttpStatus.OK,
                    request);
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "Product with id " + Id + " not found",
                    HttpStatus.NOT_FOUND,
                    request);
        }
    }




}
