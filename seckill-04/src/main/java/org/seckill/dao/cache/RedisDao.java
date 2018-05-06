package org.seckill.dao.cache;

import org.seckill.entiy.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class RedisDao {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private JedisPool jedisPool;
	
	public RedisDao(String ip,int port) {
		System.out.println("21-----------------------------------");
		jedisPool = new JedisPool(ip,port);
	}
	
	private RuntimeSchema<Seckill> scheme = RuntimeSchema.createFrom(Seckill.class); 
	public Seckill getSeckill(long seckillId){
		//redis操作逻辑
		try{
			Jedis jedis = jedisPool.getResource();
			try{
				String key = "seckill:"+seckillId;
				//并没有实现序列化
				//get->byte[]->反序列化->Object(seckill)
				//采用自定义序列化
				//ProtostuffIOUtil 比java自带的序列化机制性能更好
				byte[] bytes = jedis.get(key.getBytes());
				if(bytes != null){
					//创建一个空对象
					Seckill seckill = scheme.newMessage();
					ProtostuffIOUtil.mergeFrom(bytes,seckill,scheme);
					//reSerialize
					return seckill;
				}
			}finally{
				jedis.close();
			}
		}catch(Exception e){
			jedisPool.close();
			logger.error(e.getMessage(),e);
		}
		return null;
	}
	
    public String putSeckill(Seckill seckill){
    	try{
			Jedis jedis = jedisPool.getResource();
			try{
				String key = "seckill:"+seckill.getSeckillId();
				byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, scheme,
						LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));//缓存器
				//超时缓存，1小时
				int timeout = 60*60;
				String result = jedis.setex(key.getBytes(),timeout,bytes);
				return result;
			}finally {
				jedis.close();
			}
		}catch (Exception e){
			jedisPool.close();
			logger.error(e.getMessage(),e);
		}
		return null;
	}
}
