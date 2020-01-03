package com.example.ServicesApi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.example.Entity.AddtoCart;
import com.example.Entity.CartEntity;
import com.example.Entity.Cart_list;
import com.example.Entity.Category_details;
import com.example.Entity.Products_Details;
import com.example.filter.Response;

@Service
public class CartServicesApi {
	
	
	Logger logger=LoggerFactory.getLogger(CartServicesApi.class);
	
	@Autowired
	JdbcTemplate jdbc;
	
	@Autowired
	Response response;
	
	public Response addtocart(AddtoCart cart) {
		logger.info("-------------------- ENTER INTO ADDING PRODUCTS INTO CART PROCESS START SUCCESSFULLY---------------------------");
		String sql = "INSERT INTO cart(product_id,user_id,products_quantity) VALUES(?,?,?)";
		jdbc.update(sql,new Object[] {cart.getProduct_id(),cart.getUser_id(),cart.getQuantity()});
		logger.info("-------------------- ENTER INTO ADDED PRODUCTS INTO CART PROCESS  SUCCESSFULLY---------------------------");
		response.setStatus(true);
		response.setMessage("Added to cart successful");
		response.setResponse(200);
		return response;
	}

	
	public Response cartlist(int user_id) {
		logger.info("-------------------- ENTER INTO SEEING  PRODUCTS IN CART PROCESS START SUCCESSFULLY---------------------------");
			Map<Integer, List<Object>> stu2 = new HashMap<>();
            List list = new ArrayList();			
		String sql = "SELECT * FROM products AS pro  WHERE pro.id IN (SELECT product_id FROM cart WHERE user_id=?)";
		List<Cart_list> listdetail = this.jdbc.query(sql,new Object[]{user_id},
				new RowMapper<Cart_list>() {
					@Override
					public Cart_list mapRow(ResultSet rs, int rowNum) throws SQLException {
						Cart_list stu = new Cart_list();
						CartEntity stu1 = new CartEntity();
						stu.setProduct_name(rs.getString("products_name"));
						stu.setImage_url(rs.getString("image_url"));
						stu.setProduct_price(rs.getString("products_prices"));
						stu.setProduct_quality(rs.getString("products_quality"));
//						stu.setProducts_quantity(rs.getInt("products_quantity"));
//						list.add(stu1);
						list.add(stu);
						return stu;	
						}
				});
		logger.info("-------------------- SEEING PRODUCTS LIST IN CART SUCCESSFULLY---------------------------");
		response.setStatus(true);
		response.setMessage("cart listed successfully ");
		response.setResponse(list);
		return response;
		
	}
	
	public String addtoorder(int product_id,int user_id,int quantity) {
		logger.info("-------------------- ENTER INTO MAKING ORDER FROM CART PROCESS STARTS SUCCESSFULLY---------------------------");
		String sql = "INSERT INTO order_list(product_id,user_id,product_quantity) VALUES(?,?,?)";
		jdbc.update(sql,new Object[] {product_id,user_id,quantity});
		logger.info(" ADDED PRODUCTS FROM CART TO ORDER DONE");
		String sql1="DELETE FROM cart WHERE product_id=? AND user_id=?";
		jdbc.update(sql1,new Object[] {product_id,user_id});
		logger.info(" REMOVING PRODUCTS FROM CART DONE");
		logger.info("--------------------   ORDER DONE  SUCCESSFULLY---------------------------");
		
		return "Added to order successful";
	}
	
	public Map<Integer, List> myorderlist(int user_id) {
		logger.info("-------------------- ENTER INTO SEEING ORDER LIST PROCESS START SUCCESSFULLY---------------------------");
			Map<Integer, List> stu2 = new HashMap<>();
		String sql = "SELECT * FROM products WHERE id IN (SELECT product_id FROM order_list WHERE user_id=?)";
		List<Products_Details> listdetail = this.jdbc.query(sql,new Object[]{user_id},
				new RowMapper<Products_Details>() {
					@Override
					public Products_Details mapRow(ResultSet rs, int rowNum) throws SQLException {

						Products_Details stu = new Products_Details();
						Category_details stu1 = new Category_details();
						List<Object> list= new ArrayList<Object>();
						stu.setProducts_name(rs.getString("products_name"));
						String image_name =rs.getString("products_images");
						list.add(image_name);
						stu.setProducts_prices(rs.getString("products_prices"));
						list.add(stu);
						stu2.put(stu1.getCategory_id(),list);
						return stu;	
						}
				});
		logger.info("--------------------  SEEING ORDER LIST SUCCESSFULLY---------------------------");
		return stu2;
	}
}

