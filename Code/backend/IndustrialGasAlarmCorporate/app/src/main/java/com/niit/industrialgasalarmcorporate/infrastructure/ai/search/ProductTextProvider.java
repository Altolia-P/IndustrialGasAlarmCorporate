package com.niit.industrialgasalarmcorporate.infrastructure.ai.search;

import com.niit.industrialgasalarmcorporate.domain.product.Product;
import com.niit.industrialgasalarmcorporate.domain.product.ProductRepository;
import dev.langchain4j.data.document.Metadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductTextProvider {

    private final ProductRepository productRepository;

    public record ProductText(String text, Metadata metadata) {
    }

    public List<ProductText> getAllPublished() {
        List<Product> products = productRepository.findAll(1, 500).getContent();
        return products.stream()
                .map(p -> {
                    String text = p.getName() + "。" + truncate(p.getDescription(), 200);
                    Metadata meta = Metadata.from("productUuid", p.getProductUuid());
                    return new ProductText(text, meta);
                })
                .toList();
    }

    private static String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() > maxLen ? text.substring(0, maxLen) : text;
    }
}
