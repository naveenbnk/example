package com.example.ServicesApi;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.Entity.Aadharvalidation;

@Component
public class Aadharvalid {

	
	static Aadharvalidation validation =new Aadharvalidation();
	
	public static boolean validateAadharNumber(String aadharNumber) {
		Pattern aadharPattern = Pattern.compile("\\d{12}");
		boolean isValidAadhar = aadharPattern.matcher(aadharNumber).matches();
		if (isValidAadhar) {
			isValidAadhar = validation.validateVerhoeff(aadharNumber);
		}
		return isValidAadhar;
	}

}
