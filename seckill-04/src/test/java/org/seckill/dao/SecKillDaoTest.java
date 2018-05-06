package org.seckill.dao;


import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entiy.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
/**
 * ����spring��junit���ϣ�junit����ʱ����springIOC����
 * @author lkm
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
//����junit spring�����ļ�λ��
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SecKillDaoTest {

	//ע��Daoʵ��������
	@Resource
	private SecKillDao seckillDao;
	

	@Test
	public void testReduceNumber() {
		Date date = new Date();
		int updateCount = seckillDao.reduceNumber(1000L, date);
		System.out.println(updateCount);
	}

	@Test
	public void testQueryById() {
        long id = 1001;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);
	}

	@Test
	public void testQueryAll() {
		List<Seckill> seckills = seckillDao.queryAll(0, 100);
		for(Seckill seckill:seckills){
			System.out.println(seckill);
		}
	}

}
