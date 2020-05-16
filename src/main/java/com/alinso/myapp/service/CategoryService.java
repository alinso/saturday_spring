package com.alinso.myapp.service;

import com.alinso.myapp.entity.Category;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.repository.CategoryRepository;
import com.alinso.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryService {


    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    public void setUserCategories(List<Long> selectedCategoryIds){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Iterable<Long> ids = selectedCategoryIds;
        List<Category> categoryList  = categoryRepository.findAllById(ids);
        Set<Category> set = categoryList.stream().collect(Collectors.toSet());
        user.setCategories(set);
        userRepository.save(user);
    }

    public List<Category> allCategories() {
        return categoryRepository.findAll();
    }

    public Set<Category> myCategories(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return  user.getCategories();
    }


}
