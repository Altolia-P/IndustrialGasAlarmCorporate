package com.niit.industrialgasalarmcorporate.interfaces.pub;

import com.niit.industrialgasalarmcorporate.application.content.vo.ContentVO;
import com.niit.industrialgasalarmcorporate.application.product.vo.ProductVO;
import com.niit.industrialgasalarmcorporate.assembler.ContentAssembler;
import com.niit.industrialgasalarmcorporate.assembler.ProductAssembler;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import com.niit.industrialgasalarmcorporate.domain.content.Content;
import com.niit.industrialgasalarmcorporate.domain.content.ContentRepository;
import com.niit.industrialgasalarmcorporate.domain.product.Product;
import com.niit.industrialgasalarmcorporate.domain.product.ProductRepository;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class SearchController {

    private final ProductRepository productRepository;
    private final ContentRepository contentRepository;

    @GetMapping("/search")
    public Result<List<Map<String, Object>>> search(@RequestParam @Size(max = 200, message = "搜索关键词不超过200字符") String keyword,
                                                     @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> results = new ArrayList<>();

        List<Product> products = productRepository.searchByKeyword(keyword, limit);
        for (Product product : products) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("type", "product");
            item.put("data", ProductAssembler.toVO(product));
            results.add(item);
        }

        List<Content> contents = contentRepository.searchByKeyword(keyword, limit);
        for (Content content : contents) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("type", "content");
            item.put("data", ContentAssembler.toVO(content));
            results.add(item);
        }

        return Result.ok(results);
    }
}
