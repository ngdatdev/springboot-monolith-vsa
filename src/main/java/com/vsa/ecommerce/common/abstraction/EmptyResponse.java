package com.vsa.ecommerce.common.abstraction;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class EmptyResponse implements Response {
    public static EmptyResponse INSTANCE = new EmptyResponse();
}
