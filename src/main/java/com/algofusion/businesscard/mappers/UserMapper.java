package com.algofusion.businesscard.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.algofusion.businesscard.entities.User;
import com.algofusion.businesscard.requests.RegisterUserRequest;
import com.algofusion.businesscard.responses.RegisterUserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "refreshTokenExpiresAt", ignore = true)
    User toUser(RegisterUserRequest registerUserRequest);

    RegisterUserResponse toUserResponse(User user);
}
