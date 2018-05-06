package org.seckill.exception;
/**
 * √Î…±πÿ±’“Ï≥£
 * @author lkm
 *
 */
public class SeckillCloseExecption extends SeckillException {
	public SeckillCloseExecption(String message){
		super(message);
	}
	
    public SeckillCloseExecption(String message,Throwable cause){
    	super(message,cause);
	}
}
