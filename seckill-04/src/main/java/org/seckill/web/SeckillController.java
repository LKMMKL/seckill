package org.seckill.web;

import java.util.Date;
import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entiy.Seckill;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseExecption;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")//模块 url:/模块/资源/{id}/细分/seckill/list
public class SeckillController {
    
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SeckillService seckillService;
	//model存放数据 jsp+model=modelAndView
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public String list(Model model){
		//获取列表页
		List<Seckill> list = seckillService.getSeckillList();
		model.addAttribute("list",list);
		return "list";
		
	}
	
	@RequestMapping(value="/{seckillId}/detail",method=RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId, Model model){
		if(seckillId == null){
			return "redirect:/list";
		}
		Seckill seckill = seckillService.getById(seckillId);
		if(seckill == null){
			return "forward:/list";
		}
		model.addAttribute("seckill", seckill);
		return "detail";
	}
	
	@RequestMapping(value="/{seckillId}/exposer",
			method=RequestMethod.POST,
			produces={"application/json;charset=UTF-8"})
	//POST 在浏览器直接输入地址无效
	@ResponseBody
	public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
		SeckillResult<Exposer> result;
		try {
			Exposer exposer = seckillService.exportSeckillUrl(seckillId);
			result = new SeckillResult<Exposer>(true, exposer);
		} catch (Exception e) {
            logger.error(e.getMessage(),e);
            result = new SeckillResult<Exposer>(false, e.getMessage());
		}
		return result;
	}
	
	@RequestMapping(value="/{seckillId}/{md5}/execution",
			method=RequestMethod.POST,
			produces={"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,@PathVariable("md5") String md5,
			                                @CookieValue(value="killPhone",required=false) Long userPhone){
		//如果验证信息很多，可以使用springmvc valid
		//还没有登录
		if(userPhone == null){
			return new SeckillResult<SeckillExecution>(true, "未注册");
		}
			try {
				//调用存储过程
				//seckillService.executeSeckill(seckillId, userPhone, md5);
				SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, userPhone, md5);
				
				return new SeckillResult<SeckillExecution>(true, execution);
			} catch (RepeatKillException e1) {
				
				SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.RePEAT_KILL);
		        return new SeckillResult<SeckillExecution>(true, execution);
			} catch (SeckillCloseExecption e2) {
				SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.END);
				return new SeckillResult<SeckillExecution>(true, execution);
			} catch (SeckillException e3) {
				SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
				return new SeckillResult<SeckillExecution>(true, execution);
			}
	}
	@RequestMapping(value="/time/now",method=RequestMethod.GET)
	@ResponseBody
	public SeckillResult<Long> time(){
		Date time = new Date();
		return new SeckillResult<Long>(true,time.getTime());
	}
}
