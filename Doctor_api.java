package com.example.ServicesApi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.example.Entity.Admin;
import com.example.Entity.Appointment_list;
import com.example.Entity.Doctor_Entity;
import com.example.Entity.Doctor_list;
import com.example.Entity.Farmer_request;
import com.example.Entity.ForgotPasswordEntity;
import com.example.Entity.LoginEntity;
import com.example.Entity.RegisterEntity;
import com.example.Entity.Requestlist;
import com.example.Mailutility.MailHelper;
import com.example.filter.Response;
import com.example.helper.Hashingpassword;
import com.example.helper.UtilityHelper;

@Service
public class Doctor_api {

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	Hashingpassword hashing;

	@Autowired
	Response response;

	@Autowired
	UtilityHelper otphelper;

	@Autowired
	MailHelper mail;

	Logger logger = LoggerFactory.getLogger(Doctor_api.class);

	public Response register(Doctor_Entity entity, HttpSession session) {
		String sql1 = "SELECT count(id) FROM doctor_details WHERE mobile_number = ? ";
		int i = jdbc.queryForObject(sql1, new Object[] { entity.getMobile_number() }, Integer.class);
		String sql2 = "SELECT count(id) FROM doctor_details WHERE mail_id = ? ";
		int j = jdbc.queryForObject(sql2, new Object[] { entity.getMail_id() }, Integer.class);
//		String sql4 = "SELECT count(id) FROM doctor_details WHERE aadharno=?";
//		int k = jdbc.queryForObject(sql4, new Object[] { entity.getAadharno() }, Integer.class);
//		System.out.println(k);
		if (i > 0) {
			logger.error(
					"--------------------DOCTOR mobile_number ALREADY EXITS SO REGISTRATION UNSUCCESSFULL------------------");
			response.setStatus(false);
			response.setMessage("mobile_number  already exits");
			response.setResponse(401);
			return response;
		} else if (j >= 1) {
			logger.error(
					"--------------------DOCTOR mail_id ALREADY EXITS SO REGISTRATION UNSUCCESSFULL------------------");
			response.setStatus(false);
			response.setMessage(" mail_id already exits");
			response.setResponse(401);
			return response;
		} 
//		else if (k >= 1) {
//			logger.error(
//					"--------------------DOCTOR AADHAR NUMBER  ALREADY EXITS SO REGISTRATION UNSUCCESSFULL------------------");
//			response.setStatus(false);
//			response.setMessage(" aadhar number already exits");
//			response.setResponse(401);
//			return response;
//		}
//		else if (!UtilityHelper.validateVerhoeff(entity.getAadharno())) {
//			logger.error(
//					"--------------------DOCTOR AADHAR NUMBER NOT VALID SO REGISTRATION UNSUCCESSFULL------------------");
//			logger.info("user aadhar card validation failed");
//			response.setStatus(false);
//			response.setMessage("Please enter valid aadhar card number");
//			response.setResponse(401);
//			return response;
//
//		} 
		else {
			String status = "yes";
			String password1 = hashing.getMd5(entity.getPassword());
			logger.info("-------------------------------DOCTOR  REGISTRATION PROCESS STARTING -------------------");
			String sql = "INSERT INTO doctor_details(doctor_name, mobile_number,mail_id, password,village_name,city,status,aadharno,specialist) VALUES (?,?,?,?,?,?,?)";
			jdbc.update(sql, entity.getDoctor_name(), entity.getMobile_number(), entity.getMail_id(), password1,
					entity.getVillage_name(), entity.getCity(), status);
			logger.info("-------------------------------DOCTOR  REGISTRATION DONE SUCCESSFULLY-------------------");
			response.setResponse("200");
			response.setMessage("DOCTOR REGISTRATION SUCCESSFULL");
			response.setStatus("True");
			return response;
		}
	}

	public Response doctor_login(LoginEntity logentity) {
		logger.info("-------------------------------DOCTOR  LOGIN PROCESS START SUCCESSFULLY-------------------");
		String password1 = hashing.getMd5(logentity.getPassword());
		Doctor_Entity stu = new Doctor_Entity();
		String sql = "SELECT count(id) FROM doctor_details WHERE mobile_number = ?";
		int i = jdbc.queryForObject(sql, new Object[] { logentity.getMobile_number() }, Integer.class);
		String sql1 = "SELECT * FROM doctor_details WHERE mobile_number=?";
		List<Doctor_Entity> listdetail = this.jdbc.query(sql1, new Object[] { logentity.getMobile_number() },
				new RowMapper<Doctor_Entity>() {
					@Override
					public Doctor_Entity mapRow(ResultSet rs, int rowNum) throws SQLException {
						stu.setPassword(rs.getString("password"));
						return stu;
					}
				});
		String Password2 = stu.getPassword();
		if (i >= 1 && (password1.equals(Password2))) {
			logger.info("----------------------------FARMER LOGIN SUCCESSFULLY----------------------------");
			response.setStatus("True");
			response.setMessage("doctor login successfull");
			response.setResponse("200");
			return response;
		} else {
			logger.error("----------------------------FARMER LOGIN UNSUCCESSFULL-----------------------------");
			response.setStatus("True");
			response.setMessage("doctor Login failed");
			response.setResponse("200");
			return response;
		}
	}

	public Response doctor_forgot(ForgotPasswordEntity entity) {
		logger.info(
				"-------------------------------DOCTOR  FORGOT PASSWORD OTP  START SUCCESSFULLY-------------------");
		String mail_id = entity.getMail_id();
		String sql = "SELECT count(id) FROM doctor_details WHERE mail_id=?";
		int i = jdbc.queryForObject(sql, new Object[] { mail_id }, Integer.class);
		if (i >= 1) {
			String otp = otphelper.getRandomIntegerBetweenRange(4);
			String subject = "OTP CODE FOR FORGETPASSWORD";
			String mailContent = "OTP CODE FOR FORGETPASSWORD FOR DOCTOR  IS " + otp + " " + "SEND SUCCESSFULLY ";
			String template = "template1.html";
			mail.sendHtmlEmail(entity.getMail_id(), subject, mailContent, template);
			String sql1 = "INSERT INTO  password_otp (mail_id ,otp) VALUES (?,?)";
			jdbc.update(sql1, new Object[] { mail_id, otp });
			logger.info("otp have been save in passwordOTP");
		}
		logger.info(
				"--------------------------------SEND OTP TO DOCTOR FOR CHANGING PASSWORD SUCCESSFULLY----------------------------");
		response.setStatus("True");
		response.setMessage("doctor otp send  successful");
		response.setResponse("200");
		return response;
	}

	public Response doctor_changepassword(String mail_id, String enter_password, String re_enter_password) {
		if (enter_password.equals(re_enter_password)) {
			logger.info(
					"-----------------------------------------DOCTOR ENTER INTO CHANGE PASSWORD-------------------------------------------------");
			String password = hashing.getMd5(re_enter_password);
			String sql = "UPDATE doctor_details SET password=? WHERE  mail_id = ? ";
			jdbc.update(sql, new Object[] { password, mail_id });
			logger.info(
					"-------------------------------------------PASSWORD HAVEBEEN CHANGED SUCCESSFULLY FOR DOCTOR----------------------------------------------");
			logger.info("password changed successfully");
			response.setStatus("True");
			response.setMessage("password changed for doctor successfully");
			response.setResponse("200");
			return response;
		}
		response.setStatus("True");
		response.setMessage("please enter correct password");
		response.setResponse("200");
		return response;
	}

	public Response doctorapi(String mobile) {
		logger.info(
				"-------------------------------FARMER  ENTER INTO SEEING DOCTOR LIST  START SUCCESSFULLY-------------------");
		String status = "yes";
		List<Object> stu1 = new ArrayList<Object>();
		String sql = "SELECT doc.id,doc.doctor_name,doc.mobile_number,doc.village_name,doc.specialist FROM doctor_details AS doc INNER JOIN user_details AS user ON doc.village_name=user.village_name AND user.city=doc.city WHERE user.user_mobile_number=? AND doc.status=?";
		List<Doctor_list> listdetail = this.jdbc.query(sql, new Object[] { mobile, status },
				new RowMapper<Doctor_list>() {
					@Override
					public Doctor_list mapRow(ResultSet rs, int rowNum) throws SQLException {

						Doctor_list stu = new Doctor_list();
						stu.setId(rs.getInt("id"));
						stu.setDoctor_name(rs.getString("doctor_name"));
						stu.setMobile_number(rs.getString("mobile_number"));
						stu.setVillage_name(rs.getString("village_name"));
						stu.setSpecialist(rs.getString("specialist"));
						stu1.add(stu);
						return stu;
					}
				});
		response.setStatus("true");
		response.setMessage("doctor_list");
		response.setResponse(stu1);
		logger.info("-------------------------------FARMER SEEING DOCTOR LISTED SUCCESSFULLY-------------------");
		return response;
	}

	public Response farmerdoctorapi(Farmer_request request) {
		logger.info(
				"-------------------------------FARMER REQUEST DOCTOR FOR APPOINTMENT  START SUCCESSFULLY-------------------");
		String status = "no";
		String sql = "INSERT INTO appointment (doctor_id,user_id,appointment_status)VALUES(?,?,?)";
		jdbc.update(sql, new Object[] { request.getDoctor_id(), request.getUser_id(), status });
		logger.info("request added in appoinment table successfully");
		logger.info(
				"-------------------------------FARMER REQUEST DOCTOR FOR APPOINTMENT DONE SUCCESSFULLY-------------------");
		response.setResponse(null);
		response.setMessage("request send successfully");
		response.setStatus("true");
		return response;
	}

	public Map<Integer, RegisterEntity> requestlist(int id) {
		logger.info(
				"------------------------------- DOCTOR SEEING FARMER REQUEST FOR APPOINTMENT  START SUCCESSFULLY-------------------");
		String status = "no";
		Map<Integer, RegisterEntity> mapping = new HashMap<Integer, RegisterEntity>();
		String sql = "SELECT app.id,userentity.flat_no,userentity.name,userentity.mobile_number,userentity.number_of_cows,userentity.village_name FROM user_details AS userentity INNER JOIN doctor_details  AS doc INNER JOIN appointment AS app ON doc.id=(app.doctor_id) AND userentity.id=(app.user_id) WHERE doc.id=? AND app.status=?";

		List<RegisterEntity> listdetail = this.jdbc.query(sql, new Object[] { id, status },
				new RowMapper<RegisterEntity>() {
					@Override
					public RegisterEntity mapRow(ResultSet rs, int rowNum) throws SQLException {

						RegisterEntity stu = new RegisterEntity();
						int id = rs.getInt("id");
						stu.setName(rs.getString("name"));
						stu.setFlat_no(rs.getInt("flat_no"));
						stu.setNumber_of_cows(rs.getInt("number_of_cows"));
						stu.setMobile_number(rs.getString("mobile_number"));
						stu.setVillage_name(rs.getString("village_name"));
						mapping.put(id, stu);
						logger.info("farmer request is listed to doctor");
						return stu;
					}
				});
		logger.info(
				"------------------------------- DOCTOR SEEING FARMER REQUEST FOR APPOINTMENT  SUCCESSFULLY-------------------");
		return mapping;

	}

	public Response farmerlistupdate(int id) {
		logger.info(
				"------------------------------- DOCTOR CHANGING FARMER REQUEST FOR APPOINTMENT  START SUCCESSFULLY-------------------");
		String status = "yes";
		String sql = "UPDATE appointment SET status=? WHERE  id = ?";
		jdbc.update(sql, new Object[] { status, id });
		logger.info("response is updated for appointment");
		logger.info(
				"------------------------------- DOCTOR CHANGED FARMER REQUEST FOR APPOINTMENT SUCCESSFULLY-------------------");
		response.setResponse("200");
		response.setMessage("Request update for farmer request is done successfull");
		response.setStatus("True");
		return response;
	}

	public List<Object> doctor_list(int user_id) {
		logger.info(
				"-------------------------------  FARMER SEEING THE DOCTOR LIST WHO'S  APPOINTMENT IS FIXED START SUCCESSFULLY-------------------");
		List<Object> stu2 = new ArrayList<Object>();
		String status = "yes";
		String sql = "SELECT doc.id,doc.doctor_name,doc.mobile_number,doc.village_name,doc.status FROM doctor_details AS doc RIGHT JOIN appointment AS appointment ON appointment.doctor_id=doc.id  WHERE appointment.user_id=?  AND appointment.status=?";
		List<Doctor_Entity> listdetail = this.jdbc.query(sql, new Object[] { user_id, status },
				new RowMapper<Doctor_Entity>() {
					@Override
					public Doctor_Entity mapRow(ResultSet rs, int rowNum) throws SQLException {

						Doctor_Entity stu = new Doctor_Entity();
						int id = rs.getInt("id");
						stu2.add(id);
						stu.setDoctor_name(rs.getString("doctor_name"));
						stu.setMobile_number(rs.getString("mobile_number"));
						stu.setVillage_name(rs.getString("village_name"));
						stu2.add(stu);
						logger.info("list is displayed to farmer");
						return stu;

					}
				});
		logger.info(
				"-------------------------------  FARMER SEEING THE DOCTOR LIST WHO'S APPOINTMENT IS FIXED DONE SUCCESSFULLY-------------------");
		return stu2;
	}

	public Response doctor_status(int id) {
		logger.info(
				"-------------------------------   DOCTOR CHANGING THERE STATUS START SUCCESSFULLY-------------------");
		String status = "yes";
		String sql = "UPDATE doctor_details SET status=? WHERE  id = ?";
		jdbc.update(sql, new Object[] { status, id });
		logger.info("Status is updated for appointment");
		logger.info(
				"-------------------------------   DOCTOR CHANGING THERE STATUS DONE SUCCESSFULLY-------------------");
		response.setStatus("true");
		response.setMessage("update successfull");
		response.setResponse(null);
		return response;
	}

	public Map<Integer, RegisterEntity> farmer_list(int doctor_id) {
		logger.info(
				"-------------------------------  DOCTOR SEEING THE FARMER LIST WHO'S  APPOINTMENT IS FIXED START SUCCESSFULLY-------------------");
		Map<Integer, RegisterEntity> stu2 = new HashMap<Integer, RegisterEntity>();
		String status = "yes";
		String sql = "SELECT appointment.id,doc.Name,doc.mobile_number,doc.village_name,doc.flat_no,doc.city,doc.number_of_cows FROM user_details AS doc INNER JOIN appointment AS appointment ON appointment.user_id=doc.id  WHERE appointment.doctor_id=?  AND appointment.status=?";
		List<RegisterEntity> listdetail = this.jdbc.query(sql, new Object[] { doctor_id, status },
				new RowMapper<RegisterEntity>() {
					@Override
					public RegisterEntity mapRow(ResultSet rs, int rowNum) throws SQLException {

						RegisterEntity stu = new RegisterEntity();
						int id = rs.getInt("id");
						stu.setName(rs.getString("name"));
						stu.setMobile_number(rs.getString("mobile_number"));
						stu.setVillage_name(rs.getString("village_name"));
						stu.setFlat_no(rs.getInt("flat_no"));
						stu.setCity(rs.getString("city"));
						stu.setNumber_of_cows(rs.getInt("number_of_cows"));
						stu2.put(id, stu);
						return stu;
					}
				});
		logger.info(
				"-------------------------------  DOCTOR SEEING THE FARMER LIST WHO'S APPOINTMENT IS FIXED DONE SUCCESSFULLY-------------------");
		return stu2;
	}

	public boolean existsMobile(String mobile_number) {
		try {
			String sql = "SELECT COUNT(id) from doctor_details WHERE mobile_number = ?";
			return this.jdbc.queryForObject(sql, new Object[] { mobile_number }, Integer.class) > 0 ? true : false;
		} catch (Exception e) {
			logger.error("Error occured when check exist admin user :" + e);
			return false;
		}
	}

	public Doctor_Entity findByMobile(String mobile) {
		try {
			String sql = "SELECT * FROM doctor_details WHERE moble_number = ?";
			return (Doctor_Entity) this.jdbc.query(sql, new Object[] { mobile }, new RowMapper<Doctor_Entity>() {
				@Override
				public Doctor_Entity mapRow(ResultSet rs, int rowNum) throws SQLException {

					Doctor_Entity stu = new Doctor_Entity();
					int id = rs.getInt("id");
					stu.setDoctor_name(rs.getString("doctor_name"));	
					stu.setMobile_number(rs.getString("mobile_number"));
					stu.setVillage_name(rs.getString("village_name"));
					return stu;
				}
			}).get(0);
		} catch (Exception e) {
			logger.error("Error occured when user find by mobile:" + e);
			return null;
		}
	}

	public Response farmerrequestlist(int doctor_id) {
		List details = new ArrayList();
		String sql = "SELECT details.name,details.flat_no,details.village_name,cow.diseases_name , requestlist.request_date , requestlist.request_time,doctor.id ,requestlist.images FROM user_details AS details , cow_problems AS cow JOIN farmer_request_list AS requestlist ON requestlist.reason = cow.id JOIN doctor_request_list AS doctor ON doctor.farmer_request_id= requestlist.id WHERE details.id = requestlist.farmer_id AND doctor.doctor_id=? ";
        jdbc.query(sql, new Object [] {doctor_id},new RowMapper<Requestlist>() {

			@Override
			public Requestlist mapRow(ResultSet rs, int rowNum) throws SQLException {
				Requestlist list = new Requestlist();
				list.setId(rs.getInt("id"));
				list.setFarmer_name(rs.getString("name"));
				list.setFlat_no(rs.getInt("flat_no"));
				list.setVillage_name(rs.getString("village_name"));
				list.setRequest_date(rs.getString("request_date"));
				list.setRequest_time(rs.getString("request_time"));
				list.setReason(rs.getString("diseases_name"));
				list.setImages(rs.getString("images"));
				details.add(list);
				return list;
			}
        	
        });
        response.setMessage("Request list to doctor is successfull");
        response.setStatus(true);
        response.setResponse(details);
        return response;

        		
	}

}
