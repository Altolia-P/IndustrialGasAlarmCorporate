package com.niit.industrialgasalarmcorporate.assembler;

import com.niit.industrialgasalarmcorporate.application.ai.vo.RecommendedProductVO;
import com.niit.industrialgasalarmcorporate.application.ai.vo.RecommendedSolutionVO;
import com.niit.industrialgasalarmcorporate.domain.content.Content;
import com.niit.industrialgasalarmcorporate.domain.product.Product;

public final class AIAssembler {

    private AIAssembler() {
    }

    public static RecommendedProductVO toProductVO(Product product) {
        RecommendedProductVO vo = new RecommendedProductVO();
        vo.setUuid(product.getProductUuid());
        vo.setName(product.getName());
        vo.setSummary(product.getDescription());
        vo.setImageUrl(product.getCoverImage());
        return vo;
    }

    public static RecommendedSolutionVO toSolutionVO(Content content) {
        RecommendedSolutionVO vo = new RecommendedSolutionVO();
        vo.setUuid(content.getContentUuid());
        vo.setTitle(content.getTitle());
        vo.setSummary(content.getSummary());
        vo.setImageUrl(content.getCoverImage());
        return vo;
    }
}
