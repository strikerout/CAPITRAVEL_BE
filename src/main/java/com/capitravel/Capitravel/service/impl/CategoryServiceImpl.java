package com.capitravel.Capitravel.service.impl;

import com.capitravel.Capitravel.exception.DuplicatedResourceException;
import com.capitravel.Capitravel.exception.ResourceNotFoundException;
import com.capitravel.Capitravel.model.Category;
import com.capitravel.Capitravel.dto.CategoryDTO;
import com.capitravel.Capitravel.repository.CategoryRepository;
import com.capitravel.Capitravel.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category getCategoryById(Long id) {
        Category idCategory = categoryRepository.findById(id).orElse(null);

        if(idCategory!=null){
            return idCategory;
        }

        throw new ResourceNotFoundException("Category for id: " + id + " not found.");
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setImage(categoryDTO.getImage());

        if(categoryExistsByName(category.getName()).isEmpty()){
            return categoryRepository.save(category);
        }
        throw new DuplicatedResourceException("Category with name '" + category.getName() + "' already exists.");

    }

    @Override
    public Category updateCategory(Long id, CategoryDTO categoryDTO) {

        Optional<Category> existingCategory = categoryRepository.findById(id);

        if (existingCategory.isEmpty()) {
            throw new ResourceNotFoundException("The Category for id: " + id + " not found.");
        }

        Optional<Category> sameNameCategory = categoryRepository.findByName(categoryDTO.getName());

        if (sameNameCategory.isPresent() && !sameNameCategory.get().getId().equals(id)) {
            throw new DuplicatedResourceException("Category with name '" + categoryDTO.getName() + "' already exists.");
        }

        Category updatedCategory = existingCategory.get();
        updatedCategory.setName(categoryDTO.getName());
        updatedCategory.setDescription(categoryDTO.getDescription());
        updatedCategory.setImage(categoryDTO.getImage());
        return categoryRepository.save(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public Optional<Category> categoryExistsByName(String name) {
        return categoryRepository.findByName(name);
    }

}
