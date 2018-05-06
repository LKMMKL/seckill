package org.seckill.dao.cache;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SecKillDao;
import org.seckill.entiy.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {

	private long id = 1008;
	@Autowired
	private RedisDao redisDao;
	@Autowired
	private SecKillDao seckillDao;
	
	@Test
	public void testSeckill() throws Exception {
		Seckill seckill = redisDao.getSeckill(id);
		if(seckill == null){
			System.out.println("31");
			seckill = seckillDao.queryById(id);
			if(seckill != null){
				System.out.println("33");
				String result = redisDao.putSeckill(seckill);
			    System.out.println(result);
			    seckill = redisDao.getSeckill(id);
			    System.out.println(seckill);
			}
		}
	}
	

}
