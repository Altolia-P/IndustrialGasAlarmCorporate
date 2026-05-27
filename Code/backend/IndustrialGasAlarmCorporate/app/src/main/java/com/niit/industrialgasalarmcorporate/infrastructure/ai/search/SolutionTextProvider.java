package com.niit.industrialgasalarmcorporate.infrastructure.ai.search;

import com.niit.industrialgasalarmcorporate.domain.content.Content;
import com.niit.industrialgasalarmcorporate.domain.content.ContentRepository;
import com.niit.industrialgasalarmcorporate.domain.content.ContentType;
import dev.langchain4j.data.document.Metadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SolutionTextProvider {

    private final ContentRepository contentRepository;

    public record SolutionText(String text, Metadata metadata) {
    }

    public List<SolutionText> getAllPublished() {
        List<Content> solutions = contentRepository.findByType(ContentType.SOLUTION, 1, 500).getContent();
        return solutions.stream()
                .map(s -> {
                    String text = s.getTitle() + "。" + truncate(s.getSummary(), 200);
                    Metadata meta = Metadata.from("contentUuid", s.getContentUuid());
                    return new SolutionText(text, meta);
                })
                .toList();
    }

    private static String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() > maxLen ? text.substring(0, maxLen) : text;
    }
}
