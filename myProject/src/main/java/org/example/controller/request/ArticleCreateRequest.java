package org.example.controller.request;

import java.util.Set;

public record ArticleCreateRequest(String title, Set<String> tags) {
}
