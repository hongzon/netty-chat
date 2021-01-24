package com.nettychat.server.common.utils.mybatis;


import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;


public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
