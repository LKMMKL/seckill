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

	// ��־
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	// @Autowired �������ͽ����Զ�װ�䣬��Ϊ��ɨ��ӿڵõ���bean���������һ�£�����Ҳһ��
	// ���ַ�������ĸСд
	@Autowired
	private SecKillDao seckillDao;
	@Autowired
	private SuccessKillDao successKillDao;
	@Autowired
	private RedisDao redisDao;
	// MD5��ֵ�ַ��������ڻ���md5
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
		// �Ż��㣺�����Ż�
		// 1.����redis
		/*
		 * ԭ����ѯ��ɱ��Ʒʱ��ͨ������ֱ��ȥ���ݿ��ѯ�ģ�ѡ�����ݻ�����Redis��
		 * �ڲ�ѯ��ɱ��Ʒʱ��ȥRedis�����в�ѯ���Դ˽������ݿ��ѹ��
		 */
		Seckill seckill = redisDao.getSeckill(seckillId);
		if (seckill == null) {
			// 2.�������ݿ�
			seckill = seckillDao.queryById(seckillId);
			if (seckill == null) {
				return new Exposer(false, seckillId);
			} else {
				// 3.����redis
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
		// ������
		String md5 = getMd5(seckillId);// TODO
		return new Exposer(true, md5, seckillId);
	}

	@Override
	@Transactional
	// JDBC Connection [com.mchange.v2.c3p0.impl.NewProxyConnection@3b74ac8]
	// will be managed by Spring
	// ʹ������ע���jdbc����spring�й�
	/**
	 * ʹ��ע��������񷽷����ŵ�
	 * 1.�����Ŷӣ���ȷ��ע���񷽷��ı�̷��
	 * 2.��֤���񷽷���ִ��ʱ�価���̣ܶ���Ҫ�����������������������
	 * 3.�������еķ�������Ҫ���񣬱���ֻ��һ���޸Ĳ�����ֻ����������Ҫ�������
	 */
	public SeckillExecution executeSeckill(long seckillId, long userPhone,
			String md5) throws SeckillException, RepeatKillException,
			SeckillCloseExecption {
		try {
			if (md5 == null || !md5.equals(getMd5(seckillId))) {
				throw new SeckillException("seckill data rewrite");
			}
			// ִ����ɱ�߼��������+��¼������Ϊ
			Date nowTime = new Date();
			// ��¼������Ϊ
			int insertCount = successKillDao.insertSuccessKilled(seckillId,
					userPhone);
			if (insertCount <= 0) {
				// �ظ���ɱ
				throw new RepeatKillException("seckill repeated");
			} else {
                //����棬�ȵ���Ʒ����
				int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
				if (updateCount <= 0) {
					// û�и��¼�¼����ɱ����
					throw new SeckillCloseExecption("seckill closed");
				} else {
					// ��ɱ�ɹ�
					SuccessKilled successKilled = successKillDao
							.queryByIdWithSecKill(seckillId, userPhone);
					return new SeckillExecution(seckillId,
							SeckillStatEnum.SUCCESS, successKilled);
				}
			}
			//����cath���׳�����Ϊ�˽��������쳣ת��Ϊ����ʱ�쳣��springֻ������Ǵ�������ʱ�쳣�����лع�
		} catch (SeckillCloseExecption e1) {
			throw e1;
		} catch (RepeatKillException e2) {
			throw e2;
		} catch (Exception e) {
			// logger.error(e.getMessage(), e);
			// ���б������쳣ת��Ϊ����ʱ�쳣
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
		//ִ�д洢���̣�result����ֵ
		
		try {
			seckillDao.killByProcedure(map);
			//ʹ�ù������map�л�ȡresult��ֵ��û���򷵻�-2
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
