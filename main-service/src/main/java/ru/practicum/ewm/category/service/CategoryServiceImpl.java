package ru.practicum.ewm.category.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.DuplicatedDataException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository repository;
    EventRepository eventRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository repository, EventRepository eventRepository) {
        this.repository = repository;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        log.info("Добавляем новую категорию в базу данных.");

        // Проверяем поле name на валидность
        if (newCategoryDto.getName().isBlank()) {
            throw new ValidationException(String.format("Поле: name. Ошибка: не может быть пустым. Значение: %s",
                    newCategoryDto.getName()));
        }

        // Проверяем есть ли такое название категории в базе данных
        Optional<Category> result = repository.findByName(newCategoryDto.getName());
        if (result.isPresent()) {
            throw new DuplicatedDataException(String.format("Ошибка при создании категории. Категория с названием %s " +
                    "уже существует.", newCategoryDto.getName()));
        }

        Category savedCategory = repository.save(CategoryMapper.mapNewCategoryDtoToCategory(newCategoryDto, null));
        return CategoryMapper.mapCategoryToCategoryDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, NewCategoryDto newCategoryDto) {
        log.info("Обновляем данные категории.");

        // Проверяем есть ли категория с таким id
        Category categoryToChange = repository.findById(catId).orElseThrow(() -> new NotFoundException(
                String.format("Категория с id=%d не найдена.", catId)));

        // Проверяем существует ли категория с таким названием
        Optional<Category> result = repository.findByName(newCategoryDto.getName());
        if (!categoryToChange.getName().equals(newCategoryDto.getName()) && result.isPresent()) {
            throw new DuplicatedDataException(String.format("Не удалось обновить название категории. " +
                    "Категория с названием %s уже существует.", newCategoryDto.getName()));
        }

        Category updatedCategory = repository.save(CategoryMapper.mapNewCategoryDtoToCategory(newCategoryDto,
                categoryToChange));
        return CategoryMapper.mapCategoryToCategoryDto(updatedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(int from, int size) {
        log.info("Получаем список категорий.");
        Pageable pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "id"));

        List<Category> result = repository.findAll(pageable).stream().toList();
        return result.stream()
                .map(CategoryMapper::mapCategoryToCategoryDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long catId) {
        log.info("Получаем категорию по ее id.");

        Category result = repository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Категория с id=%d не найдена.", catId)));

        return CategoryMapper.mapCategoryToCategoryDto(result);
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        log.info("Удаляем категорию по ее id.");

        // Проверяем существует ли такая категория
        if (!repository.existsById(catId)) {
            throw new NotFoundException(String.format("Ошибка при удалении категории. Категория с id=%d не найдена.",
                    catId));
        }

        // Проверяем используется ли категория в событии
        if (!eventRepository.findAllByCategoryId(catId).isEmpty()) {
            throw new ConditionsNotMetException(String.format("Ошибка при удалении категории id=%d. Нельзя удалить " +
                    "категорию, которая используется в событии.", catId));
        }

        repository.deleteById(catId);
    }
}
