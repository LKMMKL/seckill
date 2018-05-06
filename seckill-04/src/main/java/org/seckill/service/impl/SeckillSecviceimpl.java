package org.seckill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SecKillDao;
import org.seckill.dao.SuccessKillDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entiy.Seckill;
import org.seckill.entiy.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseExecption;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

//@component @Service @Dao @controller
@Service
public class SeckillSecviceimpl implements SeckillService {

	// 日志
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	// @Autowired 根据类型进行自定装配，因为是扫描接口得到的bean，因此类型一致，名字也一致
	// 名字返回首字母小写
	@Autowired
	private SecKillDao seckillDao;
	@Autowired
	private SuccessKillDao successKillDao;
	@Autowired
	private RedisDao redisDao;
	// MD5盐值字符串，用于混淆md5
	private final String slat = "knancac5a68ASSQ@#E#c354adad";

	private String getMd5(long seckillId) {
		String base = seckillId + "/" + slat;
		String MD5 = DigestUtils.md5DigestAsHex(base.getBytes());
		System.out.println(MD5);
		return MD5;
	}

	@Override
	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 50);
	}

	@Override
	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	@Override
	public Exposer exportSeckillUrl(long seckillId) {
		// 优化点：缓存优化
		// 1.访问redis
		/*
		 * 原本查询秒杀商品时是通过主键直接去数据库查询的，选择将数据缓存在Redis，
		 * 在查询秒杀商品时先去Redis缓存中查询，以此降低数据库的压力
		 */
		Seckill seckill = redisDao.getSeckill(seckillId);
		if (seckill == null) {
			// 2.访问数据库
			seckill = seckillDao.queryById(seckillId);
			if (seckill == null) {
				return new Exposer(false, seckillId);
			} else {
				// 3.放入redis
				redisDao.putSeckill(seckill);
			}
		}
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();

		Date nowTime = new Date();
		if (nowTime.getTime() < startTime.getTime()
				|| nowTime.getTime() > endTime.getTime()) {
			return new Exposer(false, seckillId, nowTime.getTime(),
					startTime.getTime(), endTime.getTime());
		}
		// 不可逆
		String md5 = getMd5(seckillId);// TODO
		return new Exposer(true, md5, seckillId);
	}

	@Override
	@Transactional
	// JDBC Connection [com.mchange.v2.c3p0.impl.NewProxyConnection@3b74ac8]
	// will be managed by Spring
	// 使用事务注解后，jdbc将有spring托管
	/**
	 * 使用注解控制事务方法的优点
	 * 1.开发团队，明确标注事务方法的编程风格
	 * 2.保证事务方法的执行时间尽可能短，不要穿插其他网络操作，（锁）
	 * 3.不是所有的方法都需要事务，比如只有一条修改操作，只读操作不需要事务控制
	 */
	public SeckillExecution executeSeckill(long seckillId, long userPhone,
			String md5) throws SeckillException, RepeatKillException,
			SeckillCloseExecption {
		try {
			if (md5 == null || !md5.equals(getMd5(seckillId))) {
				throw new SeckillException("seckill data rewrite");
			}
			// 执行秒杀逻辑：减库存+记录购买行为
			Date nowTime = new Date();
			// 记录购买行为
			int insertCount = successKillDao.insertSuccessKilled(seckillId,
					userPhone);
			if (insertCount <= 0) {
				// 重复秒杀
				throw new RepeatKillException("seckill repeated");
			} else {
                //减库存，热点商品竞争
				int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
				if (updateCount <= 0) {
					// 没有更新记录，秒杀结束
					throw new SeckillCloseExecption("seckill closed");
				} else {
					// 秒杀成功
					SuccessKilled successKilled = successKillDao
							.queryByIdWithSecKill(seckillId, userPhone);
					return new SeckillExecution(seckillId,
							SeckillStatEnum.SUCCESS, successKilled);
				}
			}
			//这里cath再抛出，是为了将编译期异常转换为运行时异常，spring只会帮我们处理运行时异常，进行回滚
		} catch (SeckillCloseExecption e1) {
			throw e1;
		} catch (RepeatKillException e2) {
			throw e2;
		} catch (Exception e) {
			// logger.error(e.getMessage(), e);
			// 所有编译期异常转换为运行时异常
			// throw new SeckillException("seckill inner error:" +
			// e.getMessage());
			throw e;
		}
	}

	@Override
	public SeckillExecution executeSeckillProcedure(long seckillId,
			long userPhone, String md5) {
		if (md5 == null || !md5.equals(getMd5(seckillId))) {
			throw new SeckillException("seckill data rewrite");
		}
		Date killTime = new Date();
		Map<String,Object> map = new HashMap<>();
		map.put("seckillId", seckillId);
		map.put("phone", userPhone);
		map.put("killTime", killTime);
		map.put("result", null);
		//执行存储过程，result被赋值
		
		try {
			seckillDao.killByProcedure(map);
			//使用工具类从map中获取result的值，没有则返回-2
			int result = MapUtils.getInteger(map, "result",-2);
			if(result == 1){
				SuccessKilled sk = successKillDao.queryByIdWithSecKill(seckillId, userPhone);
			    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,sk);
			}else{
				return new SeckillExecution(seckillId, SeckillStatEnum.stateOf(result));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
		}
		
	}

}
