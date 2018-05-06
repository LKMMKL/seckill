package org.seckill.service;

import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entiy.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseExecption;
import org.seckill.exception.SeckillException;

public interface SeckillService {

	/**
	 * 查询所有秒杀记录
	 * @return
	 */
	List<Seckill> getSeckillList();
	
	/**
	 * 查询单个秒杀记录
	 * @param seckilled
	 * @return
	 */
	Seckill getById(long seckilled);
	
	/**
	 * 秒杀开启时，输出秒杀地址，否则输出系统时间和秒杀时间
	 * @param seckilled
	 */
	Exposer exportSeckillUrl(long seckilled);
	
	/**
	 * 执行秒杀操作
	 * @param seckilled
	 * @param userPhone
	 * @param md5
	 */
	SeckillExecution executeSeckill(long seckilled,long userPhone,String md5)
	throws SeckillException,RepeatKillException,SeckillCloseExecption;

	SeckillExecution executeSeckillProcedure(long seckillId,long userPhone,String md5);

}
