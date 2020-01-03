package com.example.ServicesApi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.example.Entity.OtpEntity;
import com.example.Entity.OtpValidationEntity;
import com.example.Entity.RegisterOtpvalidation;
import com.example.filter.Response;

@Service
public class Otpservices {

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	Response response;

	String currentTimeStamp = null;

	public Response otp(OtpValidationEntity otpvalid) {
		OtpEntity stu = new OtpEntity();
		String sql = "SELECT count(id) FROM password_otp WHERE mail_id=? AND otp=?";
		int i = jdbc.queryForObject(sql, new Object[] { otpvalid.getMail_id(), otpvalid.getOtp() }, Integer.class);
		if (i >= 1) {
			String sql1 = "SELECT created_at FROM password_otp WHERE mail_id=? AND otp=?";
			List<OtpEntity> listdetail = this.jdbc.query(sql1,
					new Object[] { otpvalid.getMail_id(), otpvalid.getOtp() }, new RowMapper<OtpEntity>() {
						@Override
						public OtpEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
							currentTimeStamp = rs.getString("created_at");
							stu.setResult(stu.calculatetime(currentTimeStamp));
							return stu;
						}
					});
		}
		if ((stu.getResult()) <= 3) {
//			String message = "OtP activated successfully";
//			String sql3 = "SELECT count(*) FROM user_details WHERE mail_id=?";
//			int i1 = jdbc.queryForObject(sql, new Object[] { otpvalid.getMail_id() }, Integer.class);
//			if (i1 >= 1) {
//				String farmer = "yes";
//				String doctor = "no";
//				String sql2 = "INSERT INTO activated_otp(mail_id,farmer,doctor,message,otp) VALUES(?,?,?,?,?)";
//				jdbc.update(sql2, new Object[] { otpvalid.getMail_id(),farmer, doctor,message,otpvalid.getOtp()});
//			}else {
//				String farmer = "no";
//				String doctor = "yes";
//				String sql4 = "INSERT INTO activated_otp(mail_id,farmer,doctor,message,otp) VALUES(?,?,?,?,?)";
//				jdbc.update(sql4, new Object[] { otpvalid.getMail_id(),farmer,doctor,message, otpvalid.getOtp() });
//			}
			response.setStatus("true");
			response.setMessage("otp have been verified successfully");
			response.setResponse("401");
			return response;

		} else {
			response.setStatus("false");
			response.setMessage("please enter valid otp to verify");
			response.setResponse("401");
			return response;
		}
	}

	public Response registerotp(RegisterOtpvalidation otpvalid) {
		OtpEntity stu = new OtpEntity();
		String sql = "SELECT count(otp_id) FROM otp WHERE otp_user_id=? AND otp_otp=?";
		int i = jdbc.queryForObject(sql, new Object[] { otpvalid.getFarmer_id(), otpvalid.getOtp() }, Integer.class);
		if (i >= 1) {
			String sql1 = "SELECT otp_created_at FROM otp WHERE otp_user_id=? AND otp_otp=?";
			List<OtpEntity> listdetail = this.jdbc.query(sql1,
					new Object[] { otpvalid.getFarmer_id(), otpvalid.getOtp() }, new RowMapper<OtpEntity>() {
						@Override
						public OtpEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
							currentTimeStamp = rs.getString("otp_created_at");
							stu.setResult(stu.calculatetime(currentTimeStamp));
							System.out.println(stu.getResult());
							return stu;
						}
					});

		}else {
			response.setStatus("false");
			response.setMessage("please enter valid mobile number to verify");
			response.setResponse("401");
			return response;
		}
		if ((stu.getResult()) <= 3) {
			System.out.println(stu.getResult());
			response.setStatus("true");
			response.setMessage("otp have been verified successfully");
			response.setResponse("200");
			return response;
		} else {
			response.setStatus("false");
			response.setMessage("please enter valid otp to verify");
			response.setResponse("401");
			return response;
		}

	}

}
