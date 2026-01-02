package com.vsa.ecommerce.feature.user.list_users;

import com.vsa.ecommerce.common.abstraction.Response;
import com.vsa.ecommerce.feature.user.dto.UserDto;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class UserListResponse implements Response {
    private List<UserDto> users;
}
