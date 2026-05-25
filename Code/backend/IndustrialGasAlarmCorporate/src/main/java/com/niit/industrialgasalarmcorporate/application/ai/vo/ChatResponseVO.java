package com.niit.industrialgasalarmcorporate.application.ai.vo;

import lombok.Data;

import java.util.List;

@Data
public class ChatResponseVO {

    private String sessionId;
    private String reply;
    private List<RecommendedProductVO> recommendedProducts;
    private List<RecommendedSolutionVO> recommendedSolutions;
}
