package ru.practicum.ewm.compilation.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.service.AdminCompilationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCompilationController {
    AdminCompilationService service;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return service.createCompilation(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable("compId") Long compId,
                                            @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        return service.updateCompilation(compId, updateCompilationRequest);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable("compId") Long compId) {
        service.deleteCompilation(compId);
    }
}
