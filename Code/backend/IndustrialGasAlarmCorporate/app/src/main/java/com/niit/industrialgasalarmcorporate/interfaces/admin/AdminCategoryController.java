package com.niit.industrialgasalarmcorporate.interfaces.admin;

import com.niit.industrialgasalarmcorporate.application.category.dto.CreateCategoryDTO;
import com.niit.industrialgasalarmcorporate.application.category.dto.UpdateCategoryDTO;
import com.niit.industrialgasalarmcorporate.application.category.service.CategoryService;
import com.niit.industrialgasalarmcorporate.application.category.vo.CategoryVO;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping("/categories")
    public Result<CategoryVO> createCategory(@Valid @RequestBody CreateCategoryDTO dto) {
        return Result.ok("新增成功", categoryService.createCategory(dto));
    }

    @GetMapping("/categories")
    public Result<List<CategoryVO>> getCategories(@RequestParam String type) {
        return Result.ok(categoryService.getCategoriesByType(type));
    }

    @PutMapping("/categories/{uuid}")
    public Result<CategoryVO> updateCategory(@PathVariable String uuid, @Valid @RequestBody UpdateCategoryDTO dto) {
        return Result.ok("修改成功", categoryService.updateCategory(uuid, dto));
    }

    @DeleteMapping("/categories/{uuid}")
    public Result<Void> deleteCategory(@PathVariable String uuid) {
        categoryService.deleteCategory(uuid);
        return Result.ok("删除成功", null);
    }
}
