//package com.example.ServicesApi;
//
//import javax.validation.Valid;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.example.Entity.RegisterEntity;
//import com.kambaa.asynctasks.OtpTasks;
//import com.kambaa.asynctasks.UserActivityTasks;
//import com.kambaa.entity.Users;
//import com.kambaa.model.Response;
//import com.kambaa.model.UserForgotPasswordModel;
//import com.kambaa.service.OTPServices;
//import com.kambaa.service.UserService;
//
//@RestController
//public class ForgotPasswordController {
//
//	@Autowired
//	RegisterEntity userServices;
//	
//	@Autowired
//	OtpTasks otpTaskService;
//	
//	@Autowired
//	OTPServices otpServices;
//		
//	@Value("${otp.retry.count}")
//	int maxOTPTry;
//	
//	private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordController.class);
//	
//	@RequestMapping(value = "/forgotpassword", method = RequestMethod.POST)
//	public com.example.filter.Response getForgotPasswordValidation(com.example.filter.Response response,@Valid @RequestBody UserForgotPasswordModel userBean) {
//		logger.info("-----------------------USER FORGOT PASSWORD CONTROLLER START----------------------------");
//		try {
//			if(!userServices.existsMobile(userBean.getMobile())) {
//				logger.info("Invalid mobile number");
////				response.setResponsecode("400");
//				response.setMessage("invalid details");
//				response.setResponse(null);
//			}else {
//				RegisterEntity user=userServices.findByMobile(userBean.getMobile());
//				if(!user.isEnable() || user.isLocked()) {
//					logger.info("User account is not enabled or locked");
//					response.setResponsecode("400");
//					response.setMessage("Invalid details or your account has been locked kindly contact to administrator");
//					response.setResponse(null);
//				}else {
//					int otpTries=otpServices.countForgotPasswordTries(userBean.getMobile(),"FORGOTPASSWORD");
//					if(!( otpTries< maxOTPTry)) {
//						userActivityTasks.userForgotPasswordMaxTriesExceed(user.getId(),user.getMobile(),otpTries);
//						logger.info("user already exceed the maximum number of forgot password attempt");
//						response.setResponsecode("400");
//						response.setMessage("You Already reached the maximum forgot password tries please contact the administrator");
//						response.setResponse(null);
//					}else {
//						otpTaskService.forgotPasswordOTP(otpServices,userServices,user.getId(),userBean.getMobile(),user.getName());
//						userActivityTasks.userForgotPasswordActivitySave(user.getId(),user.getMobile(),otpTries);
//						logger.info("User temporary password has been sent success");
//						response.setResponsecode("200");
//						response.setMessage("Temporary password has been sent to your mobile number");
//						response.setResponse(null);
//					}					
//				}
//			}
//		} catch (Exception e) {
//			logger.error("Error occured when user forgot password:"+e);
//			response.setResponsecode("500");
//			response.setMessage("Something went wrong please try to contact the administrator");
//			response.setResponse(null);
//		}
//		logger.info("-----------------------USER FORGOT PASSWORD CONTROLLER END----------------------------");
//		return response;
//	}		
//}
