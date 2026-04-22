package com.example.policee.mapper;


import com.example.policee.dao.entity.UserEntity;
import com.example.policee.dto.request.UserRegisterRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity dtoToEntity(UserRegisterRequestDto dto);
}
