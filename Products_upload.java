package com.example.ServicesApi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.example.Entity.Category_details;
import com.example.Entity.Entity;
import com.example.Entity.ProductList_details;
import com.example.Entity.Products_Details;
import com.example.Entity.Products_list;
import com.example.filter.Response;
import com.example.helper.Convertingimages;

@Component
public class Products_upload {

	
	Logger logger=LoggerFactory.getLogger(Products_upload.class);
	
	
	@Autowired
	JdbcTemplate jdbc;
	
	@Autowired
	Response response;
	
	
	
	
	public String upload(Products_Details products) {
		logger.info("--------------------ENTER INTO PRODUCT UPLOADING STARTS SUCCESSFULLY---------------------------");
		Convertingimages converts = new Convertingimages();
		String imagename = converts.uploadFile(products.getProducts_images());
		String sql = "INSERT INTO products(products_name, products_images,products_details,products_prices,products_quality,category_name,Category_type) VALUES (?,?,?,?,?,?,?)";
        jdbc.update(sql,products.getProducts_name(),imagename,products.getProducts_details(),products.getProducts_prices(),products.getProducts_quality(),products.getCategory_name(),products.getCategory_type());
		logger.info("--------------------ENTER INTO PRODUCT UPLOADED  SUCCESSFULLY---------------------------");
        return null;

	}
	
	public Response details(Products_list list) {
		
		logger.info("--------------------ENTER INTO PRODUCT DETAILS STARTS SUCCESSFULLY---------------------------");
		
		List list12= new ArrayList();
		String sql = "SELECT pro.* , cate.category_id FROM products AS pro ,category AS cate WHERE pro.category_name=? AND pro.Category_type=? AND cate.category_name=? AND cate.Category_type=?";
		List<ProductList_details> listdetail = this.jdbc.query(sql,new Object[]{list.getCategory_name(),list.getCategory_type(),list.getCategory_name(),list.getCategory_type()},
				new RowMapper<ProductList_details>() {
					@Override
					public ProductList_details mapRow(ResultSet rs, int rowNum) throws SQLException {

						ProductList_details stu = new ProductList_details();
						stu.setProduct_id(rs.getInt("id"));
						stu.setCategory_id(rs.getInt("category_id"));
						stu.setProduct_name(rs.getString("products_name"));
						stu.setProduct_image(rs.getString("products_images"));
						stu.setProduct_details(rs.getString("products_details"));
						stu.setProduct_price(rs.getString("products_prices"));
						stu.setProduct_quality(rs.getString("products_quality"));
						stu.setCategory_type(rs.getString("category_type"));
						stu.setImage_url(rs.getString("image_url"));
						list12.add(stu);
						return stu;
					}
				});
		response.setStatus("true");
		response.setMessage("listed successfully");
		response.setResponse(list12);
		logger.info("--------------------ENTER INTO PRODUCT DETAILS LISTED SUCCESSFULLY---------------------------");
		return response;
		
	}

}
