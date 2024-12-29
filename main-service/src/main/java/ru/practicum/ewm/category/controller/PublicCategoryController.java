package ru.practicum.ewm.category.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublicCategoryController {
    CategoryService service;

    @GetMapping
    public List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(defaultValue = "10") @Positive int size) {
        return service.getAllCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable("catId") Long catId) {
        return service.getCategoryById(catId);
    }
}
