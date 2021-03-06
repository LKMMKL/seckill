<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!-- 引入JSTL -->
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
<head>
<title>秒杀系统</title>
<%@include file="common/head.jsp"%>
</head>
<body>
	<!-- 页面显示部分 -->
	<div class="container">
		<div class="panel panel-primary">
			<div class="panel-heading text-center">
				<h1>秒杀列表</h1>
			</div>
			<div class="panel-body">
				<table class="table table-hover">
					<thead>
						<tr>
							<th>名称</th>
							<th>库存</th>
							<th>开始时间</th>
							<th>结束时间</th>
							<th>创建时间</th>
							<th>详情页
							<th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="sk" items="${list }">
							<tr>
								<td>${sk.name }</td>
								<td>${sk.number }</td>
								<td><fmt:formatDate value="${sk.startTime }"
										pattern="yyyy-MM-dd HH:mm:ss" /></td>
								<td><fmt:formatDate value="${sk.endTime }"
										pattern="yyyy-MM-dd HH:mm:ss" /></td>
								<td><fmt:formatDate value="${sk.createTime }"
										pattern="yyyy-MM-dd HH:mm:ss" /></td>
								<td><a class="btn btn-info"
									href="/seckill-04/${sk.seckillId }/detail" target="_blank">link</a>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<div class="panel-footer text-center">
					 <small><cite title="Source Title">LKM </cite></small>
			</div>
		</div>
	</div>
</body>
</html>