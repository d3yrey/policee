package com.example.policee.mapper;

import com.example.police.dao.entity.UserEntity;
import com.example.police.dto.request.UserRegisterRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity dtoToEntity(UserRegisterRequestDto dto);
}
