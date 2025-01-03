package ru.practicum.ewm.category.mapper;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.model.Category;

public class CategoryMapper {
    public static Category mapNewCategoryDtoToCategory(NewCategoryDto newCategoryDto, Category category) {
        if (category != null) {
            if (newCategoryDto.getName() != null) {
                category.setName(newCategoryDto.getName());
                return category;
            } else {
                return category;
            }
        }
        return new Category(null, newCategoryDto.getName());
    }

    public static CategoryDto mapCategoryToCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
