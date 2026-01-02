package com.vsa.ecommerce.feature.user.list_users;

import com.vsa.ecommerce.common.abstraction.Request;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListUsersRequest implements Request {
    private int page;
    private int size;
}
