package org.seckill.dao;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entiy.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件位置
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKillDaoTest {

	@Resource
	private SuccessKillDao successKillDao;
	@Test
	public void testInsertSuccessKilled() {
		long id = 1001L;
		long phone = 15876498116L;
		int count = successKillDao.insertSuccessKilled(id, phone);
		System.out.println(count);
	}

	@Test
	public void testQueryByIdWithSecKill() {
		long id = 1000L;
		long phone = 15876498116L;
		SuccessKilled s =  successKillDao.queryByIdWithSecKill(id, phone);
	    System.out.println(s);
	    System.out.println(s.getSecKill());
	}

}
