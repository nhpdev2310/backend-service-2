package com.nhpdev.backendservicesecond.dto.request;

import com.nhpdev.backendservicesecond.common.constraint.AppConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationRequest {
    @Min(1) @Builder.Default
    int page = AppConstants.DEFAULT_PAGE;

    @Min(1) @Max(100) @Builder.Default
    int size = AppConstants.DEFAULT_SIZE;
}
