package com.example.ServicesApi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.example.Entity.Category_details;
import com.example.Entity.Entity;
import com.example.filter.Response;

@Service
public class CategoryController {

	
	Logger logger = LoggerFactory.getLogger(CategoryController.class);
	
	@Autowired
	JdbcTemplate jdbc;
	
	
	@Autowired
	Response response;
	
	
	public String update(String category_name,String type) {
		logger.info("ENTER INTO CATEGORY UPLOAD PROCESS  STARTS SUCCESSFULLY");
		String sql = "INSERT INTO category(category_name,Category_type) VALUES (?,?) ";
		jdbc.update(sql, new Object[] {category_name,type});
		logger.info(" CATEGORY UPLOADED SUCCESSFULLY");
		return "Category update successfully";
		
	}


	public Response details() {
		logger.info("ENTER INTO CATEGORY LIST PROCESS  STARTS SUCCESSFULLY");
		List stu1 = new ArrayList();
		String type ="cow";
		String sql = "SELECT * FROM category WHERE category_type=? ";
		List<Category_details> listdetail = this.jdbc.query(sql,new Object[]{type},
				new RowMapper<Category_details>() {
					@Override
					public Category_details mapRow(ResultSet rs, int rowNum) throws SQLException {

						Category_details stu = new Category_details();
						stu.setCategory_id(rs.getInt("category_id"));
						stu.setCategory_name(rs.getString("category_name"));
						stu.setCategory_type(rs.getString("category_type"));
						stu1.add(stu);
						return stu;
					}
				});
		response.setStatus("true");
		response.setMessage("Category listed successfully");
		response.setResponse(stu1);
		logger.info("CATEGORY LISTED Products  SUCCESSFULLY");
		return response;
	}
	
	
	public Response details1() {
		logger.info("ENTER INTO CATEGORY LIST PROCESS  STARTS SUCCESSFULLY");
		List stu1 = new ArrayList();
		String type ="calf";
		String sql = "SELECT * FROM category WHERE category_type=? ";
		List<Category_details> listdetail = this.jdbc.query(sql,new Object[]{type},
				new RowMapper<Category_details>() {
					@Override
					public Category_details mapRow(ResultSet rs, int rowNum) throws SQLException {

						Category_details stu = new Category_details();
						stu.setCategory_id(rs.getInt("category_id"));
						stu.setCategory_name(rs.getString("category_name"));
						stu.setCategory_type(rs.getString("category_type"));
						stu1.add(stu);
						return stu;
					}
				});
		response.setStatus("true");
		response.setMessage("Category listed successfully");
		response.setResponse(stu1);
		logger.info("CATEGORY LISTED Products  SUCCESSFULLY");
		return response;
	}
}
