package com.leyou.common.mapper;

import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface MyMapper<T,K> extends IdListMapper<T,K> , Mapper<T>, InsertListMapper<T> {
}
