package org.seckill.exception;
/**
 * �ظ���ɱ�쳣���������쳣����springֻ���ܴ�������ʱ�쳣
 * @author lkm
 *
 */
public class RepeatKillException extends SeckillException{

	public RepeatKillException(String message){
		super(message);
	}
	
    public RepeatKillException(String message,Throwable cause){
    	super(message,cause);
	}
}
