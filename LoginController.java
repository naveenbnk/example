package com.example.ServicesApi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.example.Entity.Change_password;
import com.example.Entity.Cow_diseases;
import com.example.Entity.FarmerRequest;
import com.example.Entity.Farmer_list;
import com.example.Entity.ForgotPasswordEntity;
import com.example.Entity.LoginEntity;
import com.example.Entity.RegisterEntity;
import com.example.Mailutility.MailHelper;
import com.example.filter.Response;
import com.example.helper.Convertingimages;
import com.example.helper.FileUtils;
import com.example.helper.Hashingpassword;
import com.example.helper.OTPServices;
import com.example.helper.OtpTasks;
import com.example.helper.SMSHelper;
import com.example.helper.UtilityHelper;

@Component
public class LoginController {

	@Autowired
	JdbcTemplate jdbc;
	
	@Autowired
	Aadharvalid validaadhar;
	
	
	@Autowired
	Register_user user;

	@Autowired
	Hashingpassword hashing;

	@Autowired
	UtilityHelper otphelper;
	
	@Autowired
	Response response;

	@Autowired
	MailHelper mail;
	
	@Autowired
	SMSHelper sms;
	
	@Autowired
	OTPServices otpServices;
	
	@Autowired
	OtpTasks otptask;
	
	Register_user userServices = new Register_user();
	
	public Object[] institutes = {};
	
	@Value("${password.hash.key}")
	String passwordSalt;

	Logger logger = LoggerFactory.getLogger(LoginController.class);

	public Response register(RegisterEntity entity) {
		logger.info("-------------FARMER  REGISTRATION STARTS SUCCESSFULLY----------------------");
		String sql1 = "SELECT count(id) FROM user_details WHERE user_mobile_number = ? ";
		int i = jdbc.queryForObject(sql1, new Object[] { entity.getMobile_number() }, Integer.class);
		String sql2 = "SELECT count(id) FROM user_details WHERE mail_id = ? ";
		int j = jdbc.queryForObject(sql2, new Object[] { entity.getMail_id() }, Integer.class);
		String sql4="SELECT count(id) FROM user_details WHERE aadharno=?";
		int k = jdbc.queryForObject(sql4, new Object[] { entity.getAadharno() }, Integer.class);
		if (i > 0) {
			logger.error("--------------------FARMER mobile_number ALREADY EXITS SO REGISTRATION UNSUCCESSFULL------------------");
			response .setStatus(false);
			response.setMessage("mobile_number  already exits");
			response.setResponse(new HashMap<String , String>());
			return response;
		}else if(j>=1) {
			logger.error("--------------------FARMER mail_id ALREADY EXITS SO REGISTRATION UNSUCCESSFULL------------------");
			response .setStatus(false);
			response.setMessage(" mail_id already exits");
			response.setResponse(new HashMap<String , String>());
			return response;
		}else if(k>=1){
			logger.error("--------------------FARMER AADHAR NUMBER  ALREADY EXITS SO REGISTRATION UNSUCCESSFULL------------------");
			response .setStatus(false);
			response.setMessage(" aadhar number already exits");
			response.setResponse(new HashMap<String , String>());
			return response;
		}else if(!UtilityHelper.validateVerhoeff(entity.getAadharno())){
			logger.info("user aadhar card validation failed");
			response .setStatus(false);
			response.setMessage("Please enter valid aadhar card number");
			response.setResponse(new HashMap<String , String>());
			return response;	
			
		}else {
			String password = entity.getPassword();
			String password2=  BCrypt.hashpw(password, passwordSalt);
			FileUtils image= new FileUtils();
			String imageurl = image.ImageUploadpp(entity.getVillage_name(),entity.getName(),entity.getProfile_img());
			System.out.println(imageurl);
			long id = user.useridreturns(entity.getMobile_number(),  entity.getMail_id(), password2, entity.getName(), entity.getFlat_no(), entity.getNumber_of_cows(), entity.getCity(), entity.getVillage_name(),entity.getAadharno(),entity.getDob(),imageurl);
			if(id==0) {
				logger.info("User registration failed due to some technical difficulties");
				response.setResponse("500");
				response.setMessage("Something went wrong please try to contact the administrator");
				response.setStatus(true);
				return response;
			}else {
				otptask.accountActivationOTP(otpServices,id,entity.getMobile_number(),entity.getName());
				logger.info("New user registration success");
				response.setResponse(200);
				response.setMessage("Registration success");
				response.setStatus(true);
			    return response;
			}
		}
		}
//			System.out.println(entity.getName()+entity.getFlat_no()+entity.getVillage_name()+ entity.getCity()+
//					entity.getNumber_of_cows()+entity.getMobile_number()+ entity.getMail_id()+entity.getPassword());			
//			String sql = "INSERT INTO user_details(name,flat_no,village_name,city,number_of_cows, user_mobile_number,mail_id, password) VALUES (?,?,?,?,?,?,?,?)";
//			jdbc.update(sql,new Object[] {entity.getName(),entity.getFlat_no(),entity.getVillage_name(), entity.getCity(),
//					entity.getNumber_of_cows(),entity.getMobile_number(), entity.getMail_id(),password2});
//			
//			
//			String otp = otphelper.getRandomIntegerBetweenRange(4);
//			String message="<#> Hi "+entity.getName()+" Please enter this OTP to Activate Your Veterinary Account : "+otp+" ";
//			sms.sendOTPSMS(entity.getMobile_number(),otp,message);
//			
//			
//			response.setStatus(true);
//			response.setMessage("Registration done");
//			response.setResponse(200);
//			return response;
		

	
	public Response user_login(LoginEntity logentity) {
		logger.info("------------------------------FARMER  LOGIN STARTS SUCCESSFULLY---------------------------------");
		String sql = "SELECT count(id) FROM user_details WHERE user_mobile_number = ?";
		int i = jdbc.queryForObject(sql, new Object[] { logentity.getMobile_number() }, Integer.class);
		String sql1 = "SELECT * FROM user_details WHERE user_mobile_number=?";
		Farmer_list stu = new Farmer_list();
		List<Farmer_list> listdetail = this.jdbc.query(sql1, new Object[] { logentity.getMobile_number() },
				new RowMapper<Farmer_list>() {
					@Override
					public Farmer_list mapRow(ResultSet rs, int rowNum) throws SQLException {
						stu.setId(rs.getInt("id"));
						stu.setPassword(rs.getString("password"));
						stu.setName(rs.getString("name"));
						stu.setMobile_number(rs.getString("user_mobile_number"));
						stu.setVillage_name(rs.getString("village_name"));
						stu.setFlat_no(rs.getInt("flat_no"));
						stu.setCity(rs.getString("city"));
						stu.setMail_id(rs.getString("mail_id"));
						stu.setNo_of_cows(rs.getInt("number_of_cows"));
						stu.setProfile_img(rs.getString("profile_img"));
						return stu;
					}
				});	
//		System.out.println(stu.getPassword());
//		System.out.println(logentity.getPassword());
		String password2=  BCrypt.hashpw(logentity.getPassword(), passwordSalt);
//		System.out.println(password2);
//		System.out.println(i);
		if (i >= 1 && BCrypt.checkpw(logentity.getPassword(), stu.getPassword())) {
			logger.info("----------------------------FARMER LOGIN SUCCESSFULLY----------------------------");
			
			response .setStatus(true);
			response.setMessage("user login successfull");
			response.setResponse(stu);
			logger.info(response.getMessage());
			return response;
		} else {
			logger.error("----------------------------FARMER LOGIN UNSUCCESSFULL-----------------------------");
			response .setStatus(false);
			response.setMessage("user login failed");
			response.setResponse(new HashMap<String , String>());
			logger.info(response.getMessage());
			return response;
		}
	}

	public Response user_forgot(ForgotPasswordEntity entity) {
		logger.info(
				"-------------------------------FARMER  FORGOT PASSWORD OTP  START SUCCESSFULLY-------------------");
		String mail_id = entity.getMail_id();
		String sql = "SELECT count(id) FROM user_details WHERE mail_id=?";
		int i = jdbc.queryForObject(sql, new Object[] { mail_id }, Integer.class);
		if (i >= 1) {
			String otp = otphelper.getRandomIntegerBetweenRange(4);
			String subject = "OTP CODE FOR FORGETPASSWORD";
			String mailContent =  otp;
			String template = "template2.html";
			mail.sendHtmlEmail(entity.getMail_id(), subject, mailContent, template);
			String sql1 = "INSERT INTO  password_otp (mail_id ,otp) VALUES (?,?)";
			jdbc.update(sql1, new Object[] { mail_id, otp });
			logger.info("otp have been save in forgotpasswordOTP");
			response .setStatus("true");
			response.setMessage("user otp send  successful");
			response.setResponse("200");
			return response;
		}else {
		logger.info(
				"-------------------------------- SEND OTP TO FARMER FOR CHANGING PASSWORD SUCCESSFULLY ----------------------------");
		response .setStatus("false");
		response.setMessage("user otp send  unsuccessful  please enter your correct mail_id to send otp");
		response.setResponse("401");
		return response;
		}
	}

	
	
	
	public Response user_changepassword(Change_password password) {
			String sql1 = "SELECT count(id) FROM user_details WHERE mail_id=?";
			int i = jdbc.queryForObject(sql1, new Object[] {password.getMail_id() }, Integer.class);
//			&&  password.getPassword().equals(password.getRe_password())
			if (i >= 1 ){
				logger.info("-----------------------------------------USER ENTER INTO CHANGE PASSWORD-------------------------------------------------");
				String password1=  BCrypt.hashpw((password.getPassword()), passwordSalt);
				String sql = "UPDATE user_details SET password=? WHERE  mail_id = ? ";
				jdbc.update(sql,new Object[] {password1,password.getMail_id()});
				logger.info("-------------------------------------------PASSWORD HAVEBEEN CHANGED SUCCESSFULLY USER----------------------------------------------");
				logger.info("password changed successfully");
				response .setStatus("true");
				System.out.println(password1);
				System.out.println(password.getPassword());
				response.setMessage("password changed for user successfully");
				response.setResponse("200");
				return response;
			}else {
				response.setStatus("false");
				response.setMessage("please enter correct mail_id or password");
				response.setResponse("401");
				return response;
			}
		
	}
	
	
	
	

	public boolean existsMobile(String mobile_number) {
		try {
			String sql = "SELECT COUNT(id) from user_details WHERE user_mobile_number = ?";
			return this.jdbc.queryForObject(sql, new Object[] { mobile_number }, Integer.class) > 0 ? true : false;
		} catch (Exception e) {
			logger.error("Error occured when check exist admin user :" + e);
			return false;
		}
	}

	public RegisterEntity findByMobile(String mobile) {
		try {
			String sql = "SELECT * FROM user_details WHERE user_mobile_number = ?";
			return (RegisterEntity) this.jdbc.query(sql,new Object[] {mobile}, 
				new RowMapper<RegisterEntity>() {
					@Override
					public RegisterEntity mapRow(ResultSet rs, int rowNum) throws SQLException {

						RegisterEntity stu = new RegisterEntity();
						int id = rs.getInt("id");
						stu.setName(rs.getString("name"));
						stu.setMobile_number(rs.getString("user_mobile_number"));
						stu.setVillage_name(rs.getString("village_name"));
						stu.setFlat_no(rs.getInt("flat_no"));
						stu.setCity(rs.getString("city"));
						stu.setNumber_of_cows(rs.getInt("number_of_cows"));
						return stu;
			}
		}).get(0);
		  }catch(Exception e) {
			logger.error("Error occured when user find by mobile:"+e);
			return null;
		 }
	  }
	
	
	public Response requestcontrol (FarmerRequest request) {
		FileUtils image= new FileUtils();
		String imageurl = image.ImageUpload(request.getFarmer_id(),request.getRequest_reason(),request.getImage());
		System.out.println(imageurl);
//		Convertingimages converts = new Convertingimages();
//		String imagename = converts.uploadFile(request.getImage());
		String sql = "INSERT INTO farmer_request_list(farmer_id,reason,request_reason,request_date,request_time,images) VALUES (?,?,?,?,?,?)";
		jdbc.update(sql,new Object[] {request.getFarmer_id(),request.getReason(),request.getRequest_reason(),request.getDate(),request.getTime(),imageurl});
		response.setStatus(true);
		response.setMessage("request added successfully");
		response.setResponse(200);
		return response;
		
	}
	
	public Response listdiseases() {
		List<Cow_diseases> details= new ArrayList<Cow_diseases>();
           String sql = "SELECT * FROM cow_problems ";
           List<Cow_diseases> listdetail = this.jdbc.query(sql,new RowMapper<Cow_diseases>() {
   					@Override
   					public Cow_diseases mapRow(ResultSet rs, int rowNum) throws SQLException {
   						Cow_diseases stu= new Cow_diseases();
   						stu.setId(rs.getInt("id"));
   						stu.setDiseases_name(rs.getString("diseases_name"));
   						stu.setProblem_description(rs.getString("problem_description"));
   						details.add(stu);
   					   return stu;
   					}
   				});	
           response.setStatus(true);
           response.setMessage("listed diseases successfully ");
           response.setResponse(details);
           return response;
	}
	
	
	
	

}











