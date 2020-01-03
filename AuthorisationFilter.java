package com.example.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import com.example.Entity.Admin;
import com.example.Entity.Doctor_Entity;
import com.example.Entity.RegisterEntity;
import com.example.ServicesApi.AdminServices;
import com.example.ServicesApi.Doctor_api;
import com.example.ServicesApi.LoginController;
import com.example.helper.SessionConstants;



@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthorisationFilter implements Filter {

	@Autowired
	Doctor_api  doctorapiservies;
	
	@Autowired
	AdminServices adminservices;
	
	@Autowired
	LoginController userServices;
	
	AntPathMatcher antMatcher=new AntPathMatcher();

	private static final Logger logger = LoggerFactory.getLogger(AuthorisationFilter.class);

	private static final String[] pathArrays = { 
			
			
			"/home",
			"/error",
			"/request/doctor",
			"/user_register",
			"/login_user",
			"/doctor_register",
			"/login_doctor",
			"/forgotPassword_doctor",
			"/forgotPassword_user",
			"/doctor",
			"/list_category_cows",
			"/list_category_calf",
			"/appointment_request_list_to_doctor", 
			"/appointment_list_to_farmer",
			"/doctor_response",
			"/farmer_request",
			"/status_update", 
			"/upload_products",
			"/products_details",
			"/add_to_cart",
			"/cart_list",
			"/order",
			"/otpvalid",
			"/otpvalid_register",
			"/my_order",
			"/dashboard/admin/login",
			"/dashboard/list/doctor/admin",
			"/dashboard/farmer/list/admin",
			"/dashboard/appointment/list/admin",
			"/dashboard/list/farmer/request/admin",
			"/dashboard/product/list/admin",
			"/dashboard/category/list/admin",
			"/appointment_list_to_doctor",
			"/user_change_password",
			"/doctor_change_password",
			"/dashboard/list/doctor/edit/admin",
			"/dashboard/update_category/by/admin",
			"/dashboard/edit/doctor/list/by/admin",
			"/dashboard/delete/doctor/list/by/admin",
			"/dashboard/add/doctor/admin",
			"/dashboard/product/list/admin/test",
			"/dashboard/add/farmer/admin",
			"/dashboard/delete/farmer/list/by/admin",
			"/dashboard/edit/farmer/list/by/admin",
			"/dashboard/list/farmer/edit/admin",
			"/request",
			"/problems"
			
	};

	public AuthorisationFilter() {
		logger.info("Auth Filter initialized");
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		try {
			httpResponse.setHeader("Access-Control-Allow-Origin", "*");
			httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET,PUT");
			httpResponse.setHeader("Access-Control-Max-Age", "3600");
			httpResponse.setHeader("Access-Control-Allow-Headers", "content-type,authorization");
			httpResponse.setHeader("Access-Control-Allow-Credentials", "true");

			if ("OPTIONS".equals(httpRequest.getMethod())) {
				httpResponse.setStatus(HttpServletResponse.SC_OK);
			} else {
				
				if(!appConfigurationPathCheck(httpRequest.getRequestURI())) {
					logger.info("--------------------- NEW REQUEST RECEIVED ----------------------------");
					logger.info("Request path : "+httpRequest.getRequestURI());
				}
				
				if (CheckAuthorisationRequiredOrNot(httpRequest.getRequestURI())) {
					if(!appConfigurationPathCheck(httpRequest.getRequestURI())) {
						logger.info("--------------------- REQUEST PROCESSED ----------------------------");
					}
					chain.doFilter(request, response);
				} else {
					if (doAuthorisation(httpRequest.getHeader("authorization"),httpRequest)) {
						logger.info("--------------------- REQUEST PROCESSED ----------------------------");
						chain.doFilter(request, response);
					} else {
						logger.info("--------------------- REQUEST PROCESSED ----------------------------");
						httpResponse.sendError(401);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error occured in filter:" + e);
			httpResponse.sendError(500);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

	

	private boolean CheckAuthorisationRequiredOrNot(String path) {
		try {
			 return Arrays.stream(pathArrays)
				        .anyMatch(e -> antMatcher.match(e,path));
		} catch (Exception e) {
			logger.error("Error occured when checking the request path will required auth :" + e);
			return false;
		}
	}
	
	private boolean appConfigurationPathCheck(String path) {
		return antMatcher.match("/configuration/*",path);
	}
	
	

	private boolean doAuthorisation(String authToken,HttpServletRequest request) {
		try {
			if (!validateAuthHeader(authToken)) {
				logger.info("Auth token validation failed");
				return false;
			} else {
				authToken = authToken.substring("Basic ".length());
				String[] credentials = new String(Base64.getDecoder().decode(authToken), "UTF-8").split(":");
				boolean flag = false;
				if (adminservices.existsMobile(credentials[0])) {
					Admin adminUser = adminservices.findByMobile(credentials[0]);
					if (adminUser != null && BCrypt.checkpw(credentials[1], adminUser.getAdmin_password())) {
						request.getSession().setAttribute(SessionConstants.ADMIN_USER_DETAILS,adminUser);
						flag = true;
						return true;
					}
				}
				
				if (doctorapiservies.existsMobile(credentials[0])) {
					Doctor_Entity doctor = doctorapiservies.findByMobile(credentials[0]);
					if (doctor != null && BCrypt.checkpw(credentials[1], doctor.getPassword())) {
						request.getSession().setAttribute(SessionConstants.Doctor_DETAILS,doctor);
						flag = true;
						return true;
					}
				}
				
				if (userServices.existsMobile(credentials[0])) {
					RegisterEntity user = userServices.findByMobile(credentials[0]);
					if (user != null && BCrypt.checkpw(credentials[1], user.getPassword())) {
						request.getSession().setAttribute(SessionConstants.USER_DETAILS,user);
						flag = true;
						return true;
					}
				}
				

				if (flag == false) {
					logger.info("Invalid user name or password received");
					return false;
				} else {
					return true;
				}
			}
			}catch (Exception e) {
			logger.error("Error occured when do Authorisation :" + e);
			return false;
		}
	}

	boolean validateAuthHeader(String authHeader) {
		try {
			authHeader = authHeader.substring("Basic ".length());
			String[] credentials = new String(Base64.getDecoder().decode(authHeader), "UTF-8").split(":");
			if (credentials.length == 2 && credentials[0].length() > 0 && credentials[1].length() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			logger.error("Error occured when validate the auth header :" + e);
			return false;
		}
	}

	

}
