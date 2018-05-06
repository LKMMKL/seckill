package org.seckill.dto;

import org.seckill.entiy.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;

/**
 * 封装秒杀执行后结果
 * @author lkm
 *
 */
public class SeckillExecution {

	@Override
	public String toString() {
		return "SeckillExecution [seckillId=" + seckillId + ", state=" + state
				+ ", stateInfo=" + stateInfo + ", successKilled="
				+ successKilled + "]";
	}

	private long seckillId;
	//秒杀执行结果状态
	private int state;
	//状态表示
	private String stateInfo;
	//秒杀成功对象
	private SuccessKilled successKilled;
	public long getSeckillId() {
		return seckillId;
	}
	public void setSeckillId(long seckillId) {
		this.seckillId = seckillId;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getStateInfo() {
		return stateInfo;
	}
	public void setStateInfo(String stateInfo) {
		this.stateInfo = stateInfo;
	}
	public SuccessKilled getSuccessKilled() {
		return successKilled;
	}
	public void setSuccessKilled(SuccessKilled successKilled) {
		this.successKilled = successKilled;
	}
	public SeckillExecution(long seckillId, SeckillStatEnum enums,
			SuccessKilled successKilled) {
		super();
		this.seckillId = seckillId;
		this.state = enums.getState();
		this.stateInfo = enums.getStateInfo();
		this.successKilled = successKilled;
	}
	
	//失败
	public SeckillExecution(long seckillId, SeckillStatEnum enums) {
		super();
		this.seckillId = seckillId;
		this.state = enums.getState();
		this.stateInfo = enums.getStateInfo();
	}
	
	
}
