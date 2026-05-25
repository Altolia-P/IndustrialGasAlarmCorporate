package com.niit.industrialgasalarmcorporate.interfaces.pub;

import com.niit.industrialgasalarmcorporate.application.product.service.ProductService;
import com.niit.industrialgasalarmcorporate.application.product.vo.ProductDetailVO;
import com.niit.industrialgasalarmcorporate.application.product.vo.ProductVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    public Result<Page<ProductVO>> getProducts(
            @RequestParam(required = false) String categoryUuid,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(productService.findPublicProducts(categoryUuid, name, page, size));
    }

    @GetMapping("/products/{uuid}")
    public Result<ProductDetailVO> getProduct(@PathVariable String uuid) {
        return Result.ok(productService.getProduct(uuid));
    }
}
