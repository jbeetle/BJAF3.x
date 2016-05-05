package com.beetle.WebDemo.presentation;

import com.beetle.WebDemo.common.Const;
import com.beetle.WebDemo.common.Emp;
import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.ControllerImp;
import com.beetle.framework.web.controller.WebInput;
import com.beetle.framework.web.view.ModelData;
import com.beetle.framework.web.view.View;

public class RegisterEmpController extends ControllerImp {
	public RegisterEmpController(){
		this.setAvoidSubmitSeconds(5);//5秒内只能提交一次
	}

	public View perform(WebInput wi) throws ControllerException {
		Emp emp=(Emp)wi.getParameterValuesAsFormBean(Emp.class);
		//test
		System.out.println("---emp info:---");
		System.out.println(emp.getEmpNo());
		System.out.println(emp.getEname());
		System.out.println(emp.getHireDate());
		System.out.println(emp.getJob());
		System.out.println(emp.getSal());
		System.out.println(emp.getEmail());
		System.out.println(emp.getPhone());
		System.out.println("------");
		ModelData vd = new ModelData();
		vd.put(Const.WEB_FORWARD_URL, "BindandValidate.html");
		vd.put(Const.WEB_RETURN_MSG, "录入成功，谢谢！");
		return new View("InfoView", vd); //
	}

}
