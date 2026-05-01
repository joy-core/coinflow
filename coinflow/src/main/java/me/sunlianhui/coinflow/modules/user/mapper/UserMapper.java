package me.sunlianhui.coinflow.modules.user.mapper;

import me.sunlianhui.coinflow.modules.user.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
	UserEntity selectById(Long id);
	UserEntity selectByUsername(String username);
	List<UserEntity> selectList();
	int insert(UserEntity user);
	void updateById(UserEntity user);
	void deleteById(Long id);
}
