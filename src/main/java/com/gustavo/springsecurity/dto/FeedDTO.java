package com.gustavo.springsecurity.dto;

import java.util.List;

public record FeedDTO (List<FeedItemDTO> feedItems, int page, int pageSize, int totalPages, int totalElements) {
}
