package com.anee.projects.lovable_clone.dto.subscription;


public record UsageTodayResponse(
        Integer tokensUsed,
        Integer tokensLimit,
        Integer previewRunning,
        Integer previewsLimit
) {
}
