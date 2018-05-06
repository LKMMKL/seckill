package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entiy.SuccessKilled;

public interface SuccessKillDao {

	/**
	 * 插入购买明细，可过滤重复秒杀
	 * @param secKillId
	 * @param userPhone
	 * @return
	 */
	int insertSuccessKilled(@Param("secKillId") long secKillId,@Param("userPhone") long userPhone);
	
	/**
	 * 根据id查询Successkilled,并携带秒杀产品实体
	 * @param seckillId
	 * @return
	 */
	SuccessKilled queryByIdWithSecKill(@Param("secKillId") long secKillId,@Param("userPhone") long userPhone);
}
