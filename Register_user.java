package com.example.ServicesApi;

import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class Register_user {
	
	private static final Logger log = LoggerFactory.getLogger(Register_user.class);

	
	@Autowired
	NamedParameterJdbcTemplate namedjdbc;
	
	@Value("${password.hash.key}")
	String passwordSalt;
	
	
	public long useridreturns(String mobile,String email,String password,String name,int flat_no,int number_of_cows,String city , String village_name,String aadharno,String dob,String img) {
		try {
			String sql = "INSERT INTO user_details(name,flat_no,village_name,city,number_of_cows, user_mobile_number,mail_id, password,aadharno,dob,profile_img) VALUES (:name,:flat_no,:village_name,:city,:number_of_cows,:mobile,:email,:password,:aadharno,:dob,:img)";
			SqlParameterSource parameters = new MapSqlParameterSource().addValue("mobile", mobile)
			.addValue("mobile",mobile)
			.addValue("email", email)
			.addValue("password",password)
			.addValue("name", name)
			.addValue("city", city)
			.addValue("village_name",village_name)
			.addValue("number_of_cows",number_of_cows)
			.addValue("flat_no",flat_no)
			.addValue("aadharno",aadharno)
			.addValue("dob",dob)
			.addValue("img",img);
			KeyHolder keyHolder = new GeneratedKeyHolder();
			this.namedjdbc.update(sql, parameters, keyHolder);
			return keyHolder.getKey().longValue();
		}catch (Exception e) {
			log.error("Error occured when insert the user :"+e);
			return 0;
		}
	}


}
