/**
 * 存放主要交互逻辑js代码
 */
var seckill={
		//封装秒杀相关ajax的url
		URL:{
			now:function(){
				return '/seckill-04/time/now';
			},
			exposer:function(seckillId){
				return '/seckill-04/'+seckillId+'/exposer';
			},
			excution:function(seckillId,md5){
				return '/seckill-04/'+seckillId+'/'+md5+'/execution';
			}
		},
		handleSeckill:function(seckillId,node){
			//获取秒杀地址，控制显示逻辑，执行秒杀
			node.hide()
			    .html('<button class="btn btn-primary btn-lg" id="killBtn" >开始秒杀</button>');
		    $.post(seckill.URL.exposer(seckillId),{},function(result){
		    	//在回调函数中，执行交互流程
		    	if(result && result['success']){
		    		var exposer = result['data'];
		    		console.log("exposer:"+exposer);
		    		if(exposer['exposed']){
		    			//开启秒杀
		    			//获取秒杀地址
		    			var md5 = exposer['md5'];
		    			var killUrl = seckill.URL.excution(seckillId, md5);
		    			console.log("md5:"+md5);
		    			console.log("killUrl:"+killUrl);
		    			//只绑定一次点击时间
		    			$('#killBtn').one('click',function(){
		    				//执行秒杀请求
		    				//1.禁用按钮
		    				$(this).addClass('disabled');
		    				//2.发送秒杀请求
		    				$.post(killUrl,{},function(result){
		    					console.log("result:"+result);
		    					if(result && result['success']){
		    						console.log("result:"+result);
		    						var killResult = result['data'];
		    						var state = killResult['state'];
		    						var stateInfo = killResult['stateInfo'];
		    						//3.显示秒杀结构
		    						node.html('<span calss="label label-success">'+stateInfo+'</span>')
		    					}
		    				});
		    			});
		    			node.show();
		    		}else{
		    			//未开始
		    			var nowTime = exposer['now'];
		    			var endTime = exposer['end'];
		    			var startTime = exposer['start'];
		    			//重新计时逻辑
		    			seckill.countDown(seckillId, nowTime, startTime, endTime)
		    		}
		    	}else{
		    		console.log('result:'+result);
		    	}
		    });
		},
		//验证手机号
	    validatePhone: function (phone) {
	        if (phone && phone.length == 11 && !isNaN(phone)) {
	            return true;
	        } else {
	            return false;
	        }
	    },
	    countDown:function(seckillId,nowTime,startTime,endTime){
	    	var seckillBox = $('#seckill-box');
	    	//时间判断
	    	if(nowTime>endTime){
	    		seckillBox.html('秒杀结束！');
	    	}else if(nowTime<startTime){
	    		//秒杀未开始，计时事件绑定
	    		var killTime = new Date(startTime);
	    		seckillBox.countdown(killTime,function(event){
	    			var format = event.strftime('秒杀倒计时: %D %H时 %M分 %S秒');
	    			seckillBox.html(format);
	    			/*时间完成后回调事件*/
	    		}).on('finish.countdown',function(){
	    			//获取秒杀地址，控制显示逻辑，执行秒杀
	    			seckill.handleSeckill(seckillId,seckillBox);
	    		});
	    	}else{
	    		seckill.handleSeckill(seckillId,seckillBox);
	    	}
	    },
	  //详情页秒杀逻辑
	    detail: {
	        //详情页初始化
	        init: function (params) {
	            // 用户手机验证和登录，计时交互操作
	            // 规划交互流程
	            // 在cookie中查找手机号
	            var killPhone = $.cookie('killPhone');
	            //验证手机号 返回false 显示弹出层
	            if (!seckill.validatePhone(killPhone)) {
	                //绑定手机号
	                var killPhoneModal = $('#killPhoneModal');
	                //显示弹出层
	                killPhoneModal.modal({
	                    show: true,// 显示弹出层
	                    backdrop: 'static', //禁止位置关闭
	                    keyboard: false // 关闭键盘事件
	                });
	                $('#killPhoneButton').click(function () {
	                    var inputPhone = $('#killPhoneKey').val();
	                    if (seckill.validatePhone(inputPhone)) {
	                        // 电话号码写入cookie当中,只在/seckill下有效
	                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/'});
	                        //刷新页面
	                        window.location.reload();
	                    } else {
	                        $('#killPhoneMessage').hide().html('<lable class="label label-danger">手机号错误</lable>').show(300);
	                    }
	                });
	            }
	            //已经登录
	            //计时交互
	            var seckillId = params['seckillId'];
			    var startTime = params['startTime'];
			    var endTime = params['endTime'];
	            //获取当前系统时间
	            $.get(seckill.URL.now(),{},function(result){
	            	if(result && result['success']){
	            		var nowTime = result['data'];
	            		console.log("nowTime="+nowTime);
	            		//时间判断
	            		seckill.countDown(seckillId,nowTime,startTime,endTime);
	            	}else{
	            		console.log('result:'+result);
	            	}
	            });

	        }
	    }
}

