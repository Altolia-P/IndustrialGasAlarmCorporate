package com.niit.industrialgasalarmcorporate.infrastructure.ai.search;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@ConditionalOnProperty(name = "deepseek.embedding.enabled", havingValue = "true")
@Slf4j
public class EmbeddingSearchService {

    private final EmbeddingModel embeddingModel;
    private final ProductTextProvider productTextProvider;
    private final SolutionTextProvider solutionTextProvider;
    private final InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
    private volatile boolean indexReady = false;

    public EmbeddingSearchService(EmbeddingModel embeddingModel,
                                  ProductTextProvider productTextProvider,
                                  SolutionTextProvider solutionTextProvider) {
        this.embeddingModel = embeddingModel;
        this.productTextProvider = productTextProvider;
        this.solutionTextProvider = solutionTextProvider;
    }

    @PostConstruct
    void buildIndex() {
        try {
            log.info("开始构建产品和方案向量索引...");
            List<ProductTextProvider.ProductText> products = productTextProvider.getAllPublished();
            for (ProductTextProvider.ProductText pt : products) {
                Embedding emb = embeddingModel.embed(pt.text()).content();
                embeddingStore.add(emb, TextSegment.from(pt.text(), pt.metadata()));
            }
            List<SolutionTextProvider.SolutionText> solutions = solutionTextProvider.getAllPublished();
            for (SolutionTextProvider.SolutionText st : solutions) {
                Embedding emb = embeddingModel.embed(st.text()).content();
                embeddingStore.add(emb, TextSegment.from(st.text(), st.metadata()));
            }
            indexReady = true;
            log.info("向量索引构建完成: {} 个产品, {} 个方案", products.size(), solutions.size());
        } catch (Exception e) {
            log.warn("向量索引构建失败，将回退到关键字搜索: {}", e.getMessage());
            indexReady = false;
        }
    }

    public List<String> searchProductIds(String query, int maxResults) {
        if (!indexReady) {
            return Collections.emptyList();
        }
        try {
            Embedding queryEmbedding = embeddingModel.embed(query).content();
            EmbeddingSearchResult<TextSegment> result = embeddingStore.search(
                    EmbeddingSearchRequest.builder()
                            .queryEmbedding(queryEmbedding)
                            .maxResults(maxResults)
                            .minScore(0.5)
                            .build()
            );
            return result.matches().stream()
                    .map(m -> m.embedded().metadata().getString("productUuid"))
                    .distinct()
                    .limit(maxResults)
                    .toList();
        } catch (Exception e) {
            log.warn("向量搜索产品失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<String> searchSolutionIds(String query, int maxResults) {
        if (!indexReady) {
            return Collections.emptyList();
        }
        try {
            Embedding queryEmbedding = embeddingModel.embed(query).content();
            EmbeddingSearchResult<TextSegment> result = embeddingStore.search(
                    EmbeddingSearchRequest.builder()
                            .queryEmbedding(queryEmbedding)
                            .maxResults(maxResults)
                            .minScore(0.5)
                            .build()
            );
            return result.matches().stream()
                    .filter(m -> m.embedded().metadata().getString("contentUuid") != null)
                    .map(m -> m.embedded().metadata().getString("contentUuid"))
                    .distinct()
                    .limit(maxResults)
                    .toList();
        } catch (Exception e) {
            log.warn("向量搜索方案失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
