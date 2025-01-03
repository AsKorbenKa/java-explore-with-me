package ru.practicum.ewm.category.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCategoryController {
    CategoryService service;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        return service.createCategory(newCategoryDto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable("catId") Long catId,
                                   @Valid @RequestBody NewCategoryDto newCategoryDto) {
        return service.updateCategory(catId, newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("catId") Long catId) {
        service.deleteCategory(catId);
    }

}
