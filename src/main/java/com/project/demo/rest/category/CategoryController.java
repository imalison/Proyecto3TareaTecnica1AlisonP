package com.project.demo.rest.category;

import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.category.CategoryRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
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
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    // GET - POST  - PUT - DELETE

    @GetMapping
    public ResponseEntity<?> getCategory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(categoryPage.getTotalPages());
        meta.setTotalElements(categoryPage.getTotalElements());
        meta.setPageNumber(categoryPage.getNumber() + 1);
        meta.setPageSize(categoryPage.getSize());

        return new GlobalResponseHandler().handleResponse("Categories retrieved successfully",
                categoryPage.getContent(), HttpStatus.OK, meta);

    }

    @GetMapping("/{Id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCategoryById(@PathVariable Long Id, HttpServletRequest request) {
        Optional<Category> foundCategory = categoryRepository.findById(Id);
        if(foundCategory.isPresent())  {
            return new GlobalResponseHandler().handleResponse(
                    "Category retrieved successfully",
                    foundCategory.get(),
                    HttpStatus.OK,
                    request);
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "Category with id " + Id + " not found",
                    HttpStatus.NOT_FOUND,
                    request);
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated() && hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> addCategory(@RequestBody Category category, HttpServletRequest request) {
        Category savedCategory = categoryRepository.save(category);
        return new GlobalResponseHandler().handleResponse(
                "Category successfully saved",
                savedCategory,
                HttpStatus.OK,
                request);
    }

    @PutMapping("/{Id}")
    @PreAuthorize("isAuthenticated() && hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateCategory(@PathVariable Long Id, @RequestBody Category category, HttpServletRequest request) {
        Optional<Category> foundCategory = categoryRepository.findById(Id);
        if (foundCategory.isPresent()) {
            Category existingCategory = foundCategory.get();
            existingCategory.setName(category.getName());
            existingCategory.setDescription(category.getDescription());

            categoryRepository.save(existingCategory);

            return new GlobalResponseHandler().handleResponse(
                    "Your category was successfully updated",
                    existingCategory,
                    HttpStatus.OK,
                    request);
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "Sorry! Category with id " + Id + " not found",
                    HttpStatus.NOT_FOUND,
                    request);
        }
    }

    @DeleteMapping("/{Id}")
    @PreAuthorize("isAuthenticated() && hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable Long Id, HttpServletRequest request) {
        Optional<Category> foundCategory = categoryRepository.findById(Id);
        if(foundCategory.isPresent()) {
            categoryRepository.deleteById(Id);
            return new GlobalResponseHandler().handleResponse(
                    "Category successfully deleted",
                    HttpStatus.OK,
                    request);
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "Category with id " + Id + " not found",
                    HttpStatus.NOT_FOUND,
                    request);
        }
    }







}
