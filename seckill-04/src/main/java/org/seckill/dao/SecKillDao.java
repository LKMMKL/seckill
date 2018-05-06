package org.seckill.dao;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.seckill.entiy.Seckill;


public interface SecKillDao {

	/**
	 * �����
	 * @param secKillId 
	 * @param killTime ������ʱ��
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
	 * ����ƫ������ѯ��ɱ��Ʒ�б�
	 * @param offet
	 * @param limit
	 * @return
	 */
	//�������β���Ҫ��@Param()��ʶ
	List<Seckill> queryAll(@Param("offset")int offset,@Param("limit") int limit);
	
	
	Seckill queryBySeckillId(int seckillId);
	
	/**
	 * ʹ�ô洢����ִ����ɱ
	 * @param paramMap
	 */
	void killByProcedure(Map<String,Object> paramMap);
}
