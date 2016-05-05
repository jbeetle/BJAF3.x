package com.beetle.framework.web.controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.web.common.CommonUtil;
import com.beetle.framework.web.controller.ControllerHelper.MethodEx;
import com.beetle.framework.web.controller.draw.DrawInfo;
import com.beetle.framework.web.controller.draw.IDraw;
import com.beetle.framework.web.controller.upload.IUpload;
import com.beetle.framework.web.controller.upload.UploadForm;
import com.beetle.framework.web.view.ModelData;
import com.beetle.framework.web.view.View;

/**
 * 
 * 面板控制器，将框架支持的所有控制器类型都融合在一个统一的控制器中， 以方便编程
 * 
 */
public abstract class FacadeController extends ControllerImp {
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface ViewCtrl {

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface WSCtrl {

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface UploadCtrl {

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface DrawCtrl {

	}

	private static class WSC extends WebServiceController {
		private final Method md;
		private final Object mFather;

		public WSC(Method md, Object father) {
			this.md = md;
			this.mFather = father;
		}

		@Override
		public ModelData defaultAction(WebInput webInput)
				throws ControllerException {
			try {
				return (ModelData) md.invoke(mFather, webInput);
			} catch (Exception e) {
				throw new ControllerException(e);
			}
		}

	}

	private static class FUC implements IUpload {
		private final Method md;
		private final Object mFather;

		public FUC(Method md, Object father) {
			this.md = md;
			this.mFather = father;
		}

		@Override
		public View processUpload(UploadForm uploadForm)
				throws ControllerException {
			try {
				return (View) md.invoke(mFather, uploadForm);
			} catch (Exception e) {
				throw new ControllerException(e);
			}
		}

	}

	private static class DPC implements IDraw {
		private final Method md;
		private final Object mFather;

		public DPC(Method md, Object father) {
			this.md = md;
			this.mFather = father;
		}

		@Override
		public DrawInfo draw(WebInput webInput) throws ControllerException {
			try {
				return (DrawInfo) md.invoke(mFather, webInput);
			} catch (Exception e) {
				throw new ControllerException(e);
			}
		}

	}

	@Override
	public final View perform(WebInput webInput) throws ControllerException {
		String actionName = webInput.getParameter(CommonUtil.ACTION_STR);
		if (actionName == null || actionName.length() == 0) {
			throw new ControllerException("must set '$action' value!");
		}
		MethodEx mex = ControllerHelper.getActionMethod(
				webInput.getControllerName(), actionName, this, WebInput.class);
		try {
			// webInput.getRequest().setAttribute("$action", null);
			if (mex.isViewCtrl()) {
				View view = (View) mex.getMethod().invoke(this, webInput);
				return view;
			} else if (mex.iswSCtrl()) {
				WSC wsc = new WSC(mex.getMethod(), this);
				webInput.getRequest().setAttribute("WS_CTRL_IOBJ", "0");
				return wsc.perform(webInput);
			} else if (mex.isUploadCtrl()) {
				UploadController uc = new UploadController();
				webInput.getRequest().setAttribute("UPLOAD_CTRL_IOBJ",
						new FUC(mex.getMethod(), this));
				return uc.perform(webInput);
			} else if (mex.isDrawCtrl()) {
				DrawController dc = new DrawController();
				webInput.getRequest().setAttribute("DRAW_CTRL_IOBJ",
						new DPC(mex.getMethod(), this));
				return dc.perform(webInput);
			} else {
				throw new AppRuntimeException(actionName
						+ ",method not Annotation ctrl type!");
			}
		} catch (Exception e) {
			throw new ControllerException(e);
		}
	}

	public View sampleViewAction(WebInput webInput) throws ControllerException {
		throw new ControllerException("not implements yet!");
	}

	public ModelData sampleWSAction(WebInput webInput)
			throws ControllerException {
		throw new ControllerException("not implements yet!");
	}

	public View sampleUploadAction(UploadForm uploadForm)
			throws ControllerException {
		throw new ControllerException("not implements yet!");
	}

	public DrawInfo sampleDrawAction(WebInput webInput)
			throws ControllerException {
		throw new ControllerException("not implements yet!");
	}
}
