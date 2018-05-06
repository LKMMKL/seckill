package org.seckill.dao;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.seckill.entiy.Seckill;


public interface SecKillDao {

	/**
	 * 减库存
	 * @param secKillId 
	 * @param killTime 减库存的时间
	 * @return
	 */
	int reduceNumber(@Param("secKillId") long secKillId,@Param("killTime") Date killTime);
	
	/**
	 * 
	 * @param seckillId
	 * @return
	 */
	Seckill queryById(long seckillId);
	
	/**
	 * 根据偏移量查询秒杀商品列表
	 * @param offet
	 * @param limit
	 * @return
	 */
	//传入多个形参需要用@Param()标识
	List<Seckill> queryAll(@Param("offset")int offset,@Param("limit") int limit);
	
	
	Seckill queryBySeckillId(int seckillId);
	
	/**
	 * 使用存储过程执行秒杀
	 * @param paramMap
	 */
	void killByProcedure(Map<String,Object> paramMap);
}
