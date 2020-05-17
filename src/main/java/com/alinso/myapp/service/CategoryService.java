package com.alinso.myapp.service;

import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.Category;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.activity.ActivityDto;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.ActivityRepository;
import com.alinso.myapp.repository.CategoryRepository;
import com.alinso.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryService {


    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    ActivityService activityService;

    public void setUserCategories(List<Long> selectedCategoryIds){

        if(selectedCategoryIds.size()>10)
            throw new UserWarningException("En fazla 10 ilgi alanı seçebilirsin");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        //substract old data count
        Set<Category> oldCategoryList  = user.getCategories();
        for(Category category : oldCategoryList){
            category.setWatcherCount(category.getWatcherCount()-1);
        }
        categoryRepository.saveAll(oldCategoryList);


        Iterable<Long> ids = selectedCategoryIds;
        List<Category> categoryList  = categoryRepository.findAllById(ids);
        Set<Category> set = categoryList.stream().collect(Collectors.toSet());
        user.setCategories(set);
        userRepository.save(user);


        //add new watchers
        Set<Category> newCategoryList  = user.getCategories();
        for(Category category : newCategoryList){
            category.setWatcherCount(category.getWatcherCount()+1);
        }
        categoryRepository.saveAll(newCategoryList);


    }

    public List<Category> allCategories() {
        return categoryRepository.findAllOrderByNameAsc();
    }

    public Set<Category> myCategories(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return  user.getCategories();
    }


    public List<ActivityDto> activitiesByCategoryId(Long id, Integer pageNum) {

        Category category  = categoryRepository.findById(id).get();
        Pageable pageable  = PageRequest.of(pageNum,10);

        List<Activity> activityList  = activityRepository.findByCategoriesOrderByDeadLine(category,pageable);
        return activityService.filterActivities(activityList,false);

    }
}
