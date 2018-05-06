package org.seckill.dto;

import org.seckill.entiy.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;

/**
 * ��װ��ɱִ�к���
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
	//��ɱִ�н��״̬
	private int state;
	//״̬��ʾ
	private String stateInfo;
	//��ɱ�ɹ�����
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
	
	//ʧ��
	public SeckillExecution(long seckillId, SeckillStatEnum enums) {
		super();
		this.seckillId = seckillId;
		this.state = enums.getState();
		this.stateInfo = enums.getStateInfo();
	}
	
	
}
