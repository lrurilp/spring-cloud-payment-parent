package com.goal.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goal.order.entiy.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Mr.Peng
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
