package org.seckill.exception;
/**
 * 重复秒杀异常（运行期异常），spring只接受处理运行时异常
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
