package com.nhpdev.backendservicesecond.dto.request;

import com.nhpdev.backendservicesecond.constraint.AppConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class PaginationRequest implements Serializable {
    @Min(value = 1, message = "INVALID_INPUT") @Builder.Default
    int pageNumber = AppConstants.DEFAULT_PAGE;

    @Min(value = 1, message = "INVALID_INPUT") @Max(value = 100, message = "INVALID_INPUT") @Builder.Default
    int pageSize = AppConstants.DEFAULT_SIZE;
}
