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
	 * ��ѯ������ɱ��¼
	 * @return
	 */
	List<Seckill> getSeckillList();
	
	/**
	 * ��ѯ������ɱ��¼
	 * @param seckilled
	 * @return
	 */
	Seckill getById(long seckilled);
	
	/**
	 * ��ɱ����ʱ�������ɱ��ַ���������ϵͳʱ�����ɱʱ��
	 * @param seckilled
	 */
	Exposer exportSeckillUrl(long seckilled);
	
	/**
	 * ִ����ɱ����
	 * @param seckilled
	 * @param userPhone
	 * @param md5
	 */
	SeckillExecution executeSeckill(long seckilled,long userPhone,String md5)
	throws SeckillException,RepeatKillException,SeckillCloseExecption;

	SeckillExecution executeSeckillProcedure(long seckillId,long userPhone,String md5);

}
