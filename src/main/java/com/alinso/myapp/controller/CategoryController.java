package com.alinso.myapp.controller;

import com.alinso.myapp.entity.Category;
import com.alinso.myapp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;


    @PostMapping("/saveUserCategories")
    public ResponseEntity<?> saveUserCategories(@Valid @RequestBody List<Long> selectedCategoryIds) {

        categoryService.setUserCategories(selectedCategoryIds);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @GetMapping("/allCategories")
    public ResponseEntity<?> allCategories() {

        List<Category> categories = categoryService.allCategories();
        return new ResponseEntity<>(categories, HttpStatus.ACCEPTED);
    }

    @GetMapping("/myCategories")
    public ResponseEntity<?> myCategories() {
        Set<Category> categories = categoryService.myCategories();
        return new ResponseEntity<>(categories, HttpStatus.ACCEPTED);
    }



}
