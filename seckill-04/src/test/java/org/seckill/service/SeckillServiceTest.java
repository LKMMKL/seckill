package org.seckill.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entiy.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseExecption;
import org.seckill.exception.SeckillException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
	                   "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SeckillService seckillService;
	
	
	@Test
	public void testGetSeckillList() {
		System.out.println("111");
		List<Seckill> list = seckillService.getSeckillList();
		System.out.println(logger);
		logger.info("mmm");
		//System.out.println(list);
		//logger.info("list={}",list);
		logger.debug("list={}",list);
		logger.trace("logback的--trace日志--输出了");
		logger.debug("logback的--debug日志--输出了");
		logger.info("logback的--info日志--输出了");
		logger.warn("logback的--warn日志--输出了");
		logger.error("logback的--error日志--输出了");
	}

	@Test
	public void testGetById() {
        long seckilled = 1000L;
        Seckill seckill = seckillService.getById(seckilled);
        logger.info("seckill={}",seckill);
	}

	@Test
	public void testExportSeckillUrl() {
		long seckilled = 1000L;	
		Exposer exposer = seckillService.exportSeckillUrl(seckilled);
		logger.info("exposer={}",exposer);
        /**
         * exposer=Exposer [exposed=true,
         *  md5=83b19f08d0d7a54b1624e1d66b808886,
         *   seckillId=1000, now=0, start=0, end=0
         */

	}

	@Test
	public void testExecuteSeckill() {
		long seckilled = 1000L;	
		long phone = 2264647611L;
		
		try {
			String md5 = "83b19f08d0d7a54b1624e1d66b808886";
			SeckillExecution execution = seckillService.executeSeckill(seckilled, phone, md5);
			logger.info("execution={}",execution);
		} catch (RepeatKillException e) {
			logger.error(e.getMessage(),e);
		} catch (SeckillCloseExecption e) {
			logger.error(e.getMessage());
		} catch (SeckillException e) {
			logger.error(e.getMessage());
		}
	}
	
	@Test
	@Transactional
	public void testLogic() {
		long seckilled = 1001L;	
		Exposer exposer = seckillService.exportSeckillUrl(seckilled);
		if(exposer.isExposed()){
			logger.info("exposer={}",exposer);
			long phone = 2264687611L;
			String md5 = exposer.getMd5();
			try {
				SeckillExecution execution = seckillService.executeSeckill(seckilled, phone, md5);
				logger.info("execution={}",execution);
			} catch (RepeatKillException e) {
				logger.error(e.getMessage(),e);
			} catch (SeckillCloseExecption e) {
				logger.error(e.getMessage());
			} catch (SeckillException e) {
				logger.error(e.getMessage());
			}
		}else{
			//秒杀未开始
			logger.info("exposer={}",exposer);
		}
	}

}
