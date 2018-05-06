package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entiy.SuccessKilled;

public interface SuccessKillDao {

	/**
	 * ���빺����ϸ���ɹ����ظ���ɱ
	 * @param secKillId
	 * @param userPhone
	 * @return
	 */
	int insertSuccessKilled(@Param("secKillId") long secKillId,@Param("userPhone") long userPhone);
	
	/**
	 * ����id��ѯSuccesskilled,��Я����ɱ��Ʒʵ��
	 * @param seckillId
	 * @return
	 */
	SuccessKilled queryByIdWithSecKill(@Param("secKillId") long secKillId,@Param("userPhone") long userPhone);
}
