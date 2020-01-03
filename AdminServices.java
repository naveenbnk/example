package com.example.ServicesApi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.example.Entity.AppointmentEntity;
import com.example.Entity.Category_details;
import com.example.Entity.Cow_diseases;
import com.example.Entity.DoctorList_admin;
import com.example.Entity.Doctor_Entity;
import com.example.Entity.Doctor_list;
import com.example.Entity.Entity;
import com.example.Entity.FarmerRequest;
import com.example.Entity.Farmer_list;
import com.example.Entity.Products_Details;
import com.example.Entity.RegisterEntity;
import com.example.filter.Response;
import com.example.helper.Hashingpassword;
import com.example.Entity.Admin;

@Service
public class AdminServices {

	Logger logger = LoggerFactory.getLogger(AdminServices.class);

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	Hashingpassword hashing;

	@Autowired
	Response response;

	@Value("${password.hash.key}")
	String passwordSalt;

	public Response loginservice(String id, String password) {
		logger.info(
				"--------------------------------------------------ENTER INTO ADMIN LOGIN SUCCESSFULLY----------------------------------------------");
		String sql = "SELECT count(id) FROM Admin WHERE admin_id = ? AND admin_password = ?";
		int i = jdbc.queryForObject(sql, new Object[] { id, password }, Integer.class);
		if (i >= 1) {
			response.setResponse("200");
			response.setMessage("admin login successfull");
			response.setStatus("True");
			logger.info(
					"--------------------------------------------------ADMIN LOGIN DONE SUCCESSFULLY ----------------------------------------------");
			return response;

		} else {
			response.setResponse("301");
			response.setMessage("sorry you enter wrong details");
			response.setStatus("False");
			logger.info(
					"--------------------------------------------------  ADMIN LOGIN UNSUCCESSFULLY----------------------------------------------");
			return response;
		}
	}

	public List<DoctorList_admin> doctorlist(Entity entity) {
		int limit = entity.getLimit();
		int a = entity.getPageno() * limit;
		int b = a - limit;
		System.out.println("limit:" + limit);
		System.out.println("pageno:" + b);

		List<DoctorList_admin> stu1 = new ArrayList<DoctorList_admin>();
		String sql = "SELECT * FROM doctor_details LIMIT ?,?";
		List<DoctorList_admin> listdetail = this.jdbc.query(sql, new Object[] { b, limit },
				new RowMapper<DoctorList_admin>() {
					@Override
					public DoctorList_admin mapRow(ResultSet rs, int rowNum) throws SQLException {
						DoctorList_admin stu = new DoctorList_admin();
						stu.setId(rs.getInt("id"));
						stu.setDoctor_name(rs.getString("doctor_name"));
						stu.setMobile_number(rs.getString("mobile_number"));
						stu.setVillage_name(rs.getString("village_name"));
						stu.setMail_id(rs.getString("mail_id"));
						stu.setCity(rs.getString("city"));
						stu.setPassword(rs.getString("password"));
						stu.setStatus(rs.getString("status"));
						stu1.add(stu);
						return stu;
					}
				});
		return stu1;
	}

	public Map<Integer, Farmer_list> farmerslist(Entity entity) {
		int limit = entity.getLimit();
		int a = entity.getPageno() * limit;
		int b = a - limit;

		List<Object> stu1 = new ArrayList<Object>();
		Map<Integer, Farmer_list> map = new HashMap<Integer, Farmer_list>();
		String sql = "SELECT * FROM user_details LIMIT ?,?";
		List<Farmer_list> listdetail = this.jdbc.query(sql, new Object[] { b, limit }, new RowMapper<Farmer_list>() {
			@Override
			public Farmer_list mapRow(ResultSet rs, int rowNum) throws SQLException {
				Farmer_list stu = new Farmer_list();
				stu.setId(rs.getInt("id"));
				stu.setName(rs.getString("name"));
				stu.setMobile_number(rs.getString("user_mobile_number"));
				stu.setVillage_name(rs.getString("village_name"));
				stu.setCity(rs.getString("city"));
				stu.setFlat_no(rs.getInt("flat_no"));
				stu.setMail_id(rs.getString("mail_id"));
				stu.setNo_of_cows(rs.getInt("number_of_cows"));
				map.put(stu.getId(), stu);
				return stu;
			}
		});
		return map;
	}

	public Map<Integer, AppointmentEntity> appoinmnetlists(Entity entity) {
		int limit = entity.getLimit();
		int a = entity.getPageno() * limit;
		int pageno = a - limit;
		Map<Integer, AppointmentEntity> mapping = new HashMap<Integer, AppointmentEntity>();
		String sql = "SELECT app.*,userd.* ,doc.* FROM user_details AS userd JOIN appointment AS app ON app.user_id = userd.id JOIN doctor_details AS doc ON app.doctor_id = doc.id LIMIT ?,?";
		List<Doctor_Entity> listdetail = this.jdbc.query(sql, new Object[] { pageno, limit },
				new RowMapper<Doctor_Entity>() {
					@Override
					public Doctor_Entity mapRow(ResultSet rs, int rowNum) throws SQLException {
						AppointmentEntity stu = new AppointmentEntity();
						Doctor_Entity doc = new Doctor_Entity();
						int id = rs.getInt("id");
						stu.setUser_name(rs.getString("name"));
						stu.setUser_mobile(rs.getString("user_mobile_number"));
						stu.setUser_village(rs.getString("village_name"));
						stu.setUser_city(rs.getString("city"));
						doc.setDoctor_name(rs.getString("doctor_name"));
						doc.setMobile_number(rs.getString("mobile_number"));
						doc.setVillage_name(rs.getString("village_name"));
						doc.setCity(rs.getString("city"));
						stu.setAppointment(rs.getString("appointment_status"));
						stu.setDoctor_list(doc);
						mapping.put(id, stu);
						return doc;
					}
				});
		return mapping;
	}

	public Map<Integer, Products_Details> productlist(Entity entity) {
		int limit = entity.getLimit();
		int a = entity.getPageno() * limit;
		int b = a - limit;
		Map<Integer, Products_Details> mapping = new HashMap<Integer, Products_Details>();
		String sql = "SELECT * FROM products WHERE id IN(SELECT id FROM products WHERE id%2 = 1) LIMIT ?,?";
		List<Products_Details> listdetail = this.jdbc.query(sql, new Object[] { b, limit },
				new RowMapper<Products_Details>() {
					@Override
					public Products_Details mapRow(ResultSet rs, int rowNum) throws SQLException {
						Products_Details stu = new Products_Details();
						int id = rs.getInt("id");
						stu.setProducts_name(rs.getString("products_name"));
						stu.setProducts_prices(rs.getString("products_prices"));
						stu.setProducts_quality(rs.getString("products_quality"));
						stu.setProducts_details(rs.getString("products_details"));
						stu.setCategory_name(rs.getString("category_name"));
						stu.setCategory_type(rs.getString("category_type"));
						stu.setImage_url(rs.getString("image_url"));
						mapping.put(id, stu);
						return stu;
					}
				});
		return mapping;
	}

	public Map<Integer, Category_details> listofcategory(Entity entity) {
		int limit = entity.getLimit();
		int a = entity.getPageno() * limit;
		int b = a - limit;
		Map<Integer, Category_details> mapping = new HashMap<Integer, Category_details>();
		String sql = "SELECT * FROM category LIMIT ?,?";
		List<Category_details> listdetail = this.jdbc.query(sql, new Object[] { b, limit },
				new RowMapper<Category_details>() {
					@Override
					public Category_details mapRow(ResultSet rs, int rowNum) throws SQLException {
						Category_details stu = new Category_details();
						int id = rs.getInt("category_id");
						stu.setCategory_id(rs.getInt("category_id"));
						stu.setCategory_name(rs.getString("category_name"));
						stu.setCategory_type(rs.getString("category_type"));
						mapping.put(id, stu);
						return stu;
					}
				});
		return mapping;
	}

	public boolean existsMobile(String admin_id) {
		try {
			String sql = "SELECT COUNT(id) from admin WHERE admin_id = ?";
			return this.jdbc.queryForObject(sql, new Object[] { admin_id }, Integer.class) > 0 ? true : false;
		} catch (Exception e) {
			logger.error("Error occured when check exist admin user :" + e);
			return false;
		}
	}

	public Admin findByMobile(String mobile) {
		try {
			String sql = "SELECT * FROM admin WHERE admin_id = ?";
			return (Admin) this.jdbc.query(sql, new Object[] { mobile }, new RowMapper<Admin>() {
				@Override
				public Admin mapRow(ResultSet rs, int rowNum) throws SQLException {
					Admin stu = new Admin();
					int id = rs.getInt("id");
					stu.setAdmin_id(rs.getString("admin_id"));
					stu.setAdmin_password(rs.getString("admin_password"));
					return stu;
				}
			}).get(0);
		} catch (Exception e) {
			logger.error("Error occured when user find by mobile:" + e);
			return null;
		}
	}

	public List<DoctorList_admin> edit(int id) {
		List<DoctorList_admin> stu1 = new ArrayList<DoctorList_admin>();
		String sql = "SELECT * FROM doctor_details WHERE id =?";
		jdbc.query(sql, new Object[] { id }, new RowMapper<DoctorList_admin>() {
			@Override
			public DoctorList_admin mapRow(ResultSet rs, int rowNum) throws SQLException {
				DoctorList_admin stu = new DoctorList_admin();
				stu.setId(rs.getInt("id"));
				stu.setDoctor_name(rs.getString("doctor_name"));
				stu.setMobile_number(rs.getString("mobile_number"));
				stu.setVillage_name(rs.getString("village_name"));
				stu.setMail_id(rs.getString("mail_id"));
				stu.setCity(rs.getString("city"));
				stu.setPassword(rs.getString("password"));
				stu.setStatus(rs.getString("status"));
				stu1.add(stu);
				return stu;
			}
		});
		return stu1;

	}

	public Response editlistofdoctor(DoctorList_admin doctor) {
		String sql = "UPDATE doctor_details SET doctor_name=?,mail_id=?,mobile_number=?,village_name=?,city=?,status=? WHERE id=?";
		jdbc.update(sql, new Object[] { doctor.getDoctor_name(), doctor.getMail_id(), doctor.getMobile_number(),
				doctor.getVillage_name(), doctor.getCity(), doctor.getStatus(), doctor.getId() });
		response.setResponse("200");
		response.setMessage("edit successfully");
		response.setStatus("True");
		return response;
	}

	public Response deletedoctor(DoctorList_admin doctor) {
		String sql = "DELETE FROM doctor_details WHERE id =?";
		jdbc.update(sql, new Object[] { doctor.getId() });
		response.setResponse("200");
		response.setMessage("list deleted successfully");
		response.setStatus("True");
		return response;
	}

	public Response addDoctor(Doctor_Entity doctor) {
		String status = "no";
		String sql1 = "SELECT count(id) FROM doctor_details WHERE mobile_number = ? ";
		int i = jdbc.queryForObject(sql1, new Object[] { doctor.getMobile_number() }, Integer.class);
		String sql2 = "SELECT count(id) FROM doctor_details WHERE mail_id = ? ";
		int j = jdbc.queryForObject(sql2, new Object[] { doctor.getMail_id() }, Integer.class);
		if (i > 0) {
			logger.error(
					"--------------------FARMER mobile_number ALREADY EXITS SO REGISTRATION UNSUCCESSFULL------------------");
			response.setStatus("false");
			response.setMessage("mobile_number  already exits");
			response.setResponse("401");
			return response;
		} else if (j >= 1) {
			logger.error(
					"--------------------FARMER mail_id ALREADY EXITS SO REGISTRATION UNSUCCESSFULL------------------");
			response.setStatus("false");
			response.setMessage(" mail_id already exits");
			response.setResponse("401");
			return response;

		} else {
			String password1 = hashing.getMd5(doctor.getPassword());
			String password = doctor.getPassword();
			String password2 = BCrypt.hashpw(password, passwordSalt);
			String sql = "INSERT INTO doctor_details(doctor_name,mail_id,mobile_number, password, village_name,city,status) VALUES (?,?,?,?,?,?,?)";
			jdbc.update(sql, new Object[] { doctor.getDoctor_name(), doctor.getMail_id(), doctor.getMobile_number(),
					password2, doctor.getVillage_name(), doctor.getCity(), status });

			logger.info("admin added doctor successfully");
			response.setStatus("True");
			response.setMessage("Admin Added the doctor successfully");
			response.setResponse("200");
			return response;
		}

	}
	/*
	 * editing farmer list starts here and doctor editing is finished above
	 */

	public List<Farmer_list> editfarmer(int id) {
		List<Farmer_list> stu1 = new ArrayList<Farmer_list>();
		String sql = "SELECT * FROM user_details WHERE id =?";
		jdbc.query(sql, new Object[] { id }, new RowMapper<Farmer_list>() {
			@Override
			public Farmer_list mapRow(ResultSet rs, int rowNum) throws SQLException {
				Farmer_list stu = new Farmer_list();
				stu.setId(rs.getInt("id"));
				stu.setName(rs.getString("name"));
				stu.setMobile_number(rs.getString("user_mobile_number"));
				stu.setVillage_name(rs.getString("village_name"));
				stu.setMail_id(rs.getString("mail_id"));
				stu.setFlat_no(rs.getInt("flat_no"));
				stu.setNo_of_cows(rs.getInt("number_of_cows"));
				stu.setCity(rs.getString("city"));
				stu1.add(stu);
				return stu;
			}
		});
		return stu1;

	}

	public Response editlistoffarmer(Farmer_list farmer) {
		String sql = "UPDATE user_details SET name=?,mail_id=?,user_mobile_number=?,village_name=?,city=?,flat_no=?,number_of_cows=? WHERE id=?";
		jdbc.update(sql,
				new Object[] { farmer.getName(), farmer.getMail_id(), farmer.getMobile_number(),
						farmer.getVillage_name(), farmer.getCity(), farmer.getFlat_no(), farmer.getNo_of_cows(),
						farmer.getId() });
		response.setResponse("200");
		response.setMessage("edit successfully");
		response.setStatus("True");
		return response;
	}

	public Response deletefarmer(Farmer_list farmer) {
		String sql = "DELETE FROM user_details WHERE id =?";
		jdbc.update(sql, new Object[] { farmer.getId() });
		response.setResponse("200");
		response.setMessage("list deleted successfully");
		response.setStatus("true");
		return response;
	}

	public Response addfarmer(RegisterEntity farmer) {

		logger.info("-------------FARMER  REGISTRATION  BY ADMIN STARTS SUCCESSFULLY----------------------");
		System.out.println(farmer.getName() + farmer.getMail_id() + farmer.getMobile_number() + farmer.getPassword()
				+ farmer.getVillage_name() + farmer.getCity() + farmer.getNumber_of_cows() + farmer.getFlat_no());
		String sql1 = "SELECT count(id) FROM user_details WHERE user_mobile_number = ? ";
		int i = jdbc.queryForObject(sql1, new Object[] { farmer.getMobile_number() }, Integer.class);
		String sql2 = "SELECT count(id) FROM user_details WHERE mail_id = ? ";
		int j = jdbc.queryForObject(sql2, new Object[] { farmer.getMail_id() }, Integer.class);
		if (i > 0) {
			logger.error(
					"--------------------FARMER mobile_number ALREADY EXITS SO REGISTRATION UNSUCCESSFULL------------------");
			response.setStatus("false");
			response.setMessage("mobile_number  already exits");
			response.setResponse("401");
			return response;
		} else if (j >= 1) {
			logger.error(
					"--------------------FARMER mail_id ALREADY EXITS SO REGISTRATION UNSUCCESSFULL------------------");
			response.setStatus("false");
			response.setMessage(" mail_id already exits");
			response.setResponse("401");
			return response;

		} else {
			String password = farmer.getPassword();
			String password2 = BCrypt.hashpw(password, passwordSalt);
			String sql = "INSERT INTO user_details(name,mail_id,user_mobile_number, password, village_name,city,number_of_cows,flat_no) VALUES (?,?,?,?,?,?,?,?)";
			jdbc.update(sql, new Object[] { farmer.getName(), farmer.getMail_id(), farmer.getMobile_number(), password2,
					farmer.getVillage_name(), farmer.getCity(), farmer.getNumber_of_cows(), farmer.getFlat_no() });

			response.setStatus("True");
			response.setMessage("Admin Added the farmer successfully");
			logger.info("ADMIN ADDED NEW FARMER SUCCESSFULLY");
			response.setResponse("200");
			logger.info("-------------FARMER  REGISTRATION BY ADMIN  ENDS SUCCESSFULLY----------------------");
			return response;
		}

	}



	public Response appointmentfixing(@Valid Entity entity) {
		int limit = entity.getLimit();
		int a = entity.getPageno() * limit;
		int b = a - limit;
		List details = new ArrayList();
		String sql = "SELECT farmer.*,cow.* FROM farmer_request_list AS farmer , cow_problems AS cow WHERE cow.id = farmer.reason LIMIT ?,?";
		List<FarmerRequest> listdetail = this.jdbc.query(sql, new Object[] { b, limit },
				new RowMapper<FarmerRequest>() {
					@Override
					public FarmerRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
						FarmerRequest stu = new FarmerRequest();
						Cow_diseases cow = new Cow_diseases();
						stu.setFarmer_id(rs.getInt("farmer_id"));
						stu.setReason(rs.getString("reason"));
						stu.setDate(rs.getString("request_date"));
						stu.setRequest_reason(rs.getString("request_reason"));
						stu.setTime(rs.getString("request_time"));
						cow.setId(rs.getInt("id"));
						cow.setDiseases_name(rs.getString("diseases_name"));
						cow.setProblem_description(rs.getString("problem_description"));
						stu.setReason_id(cow);
						details.add(stu);
						return stu;
					}
				});
		response.setStatus(true);
		response.setMessage("Listed successfully");
		response.setResponse(details);
		return response;
	}

	public Response listedrequestdoctor(String specialist) {
		List<Doctor_list> stu1 = new ArrayList<Doctor_list>();
		String sql = "SELECT * FROM doctor_details WHERE specialist=? ";
		List<Doctor_list> listdetail = this.jdbc.query(sql, new Object[] { specialist }, new RowMapper<Doctor_list>() {
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
		logger.info(
				"-------------------------------ADMIN  SEEING DOCTOR FOR REQUSTING LISTED SUCCESSFULLY-------------------");
		return response;
	}

	public Response listofspecialist() {
		List<Doctor_list> stu1 = new ArrayList<Doctor_list>();
		String sql = "SELECT specialist FROM doctor_details ";
		List<Doctor_list> listdetail = this.jdbc.query(sql, new RowMapper<Doctor_list>() {
			@Override
			public Doctor_list mapRow(ResultSet rs, int rowNum) throws SQLException {

				Doctor_list stu = new Doctor_list();
				stu.setSpecialist(rs.getString("specialist"));
				stu1.add(stu);
				return stu;
			}
		});
		response.setStatus("true");
		response.setMessage("doctor_list");
		response.setResponse(stu1);
		logger.info("-------------------------------ADMIN SEEING SPECIALIST LISTED SUCCESSFULLY-------------------");
		return response;
	}

	public Response requestbyadmin(int doctor_id, int farmer_request_id, int farmer_id) {
		String status = "no";
		String sql = "INSERT INTO doctor_request_list (doctor_id,farmer_request_id,appointment)VALUES(?,?,?)";
		jdbc.update(sql, new Object[] { doctor_id, farmer_request_id, status });
		String sql1 = "INSERT INTO appointment (doctor_id,user_id)VALUES(?,?) ";
		jdbc.update(sql1, new Object[] { doctor_id, farmer_id });
		response.setStatus("true");
		response.setMessage("Request sent to doctor by admin");
		response.setResponse(200);
		return response;
	}

//	public Response appointmentfixingresultset(@Valid Entity entity) {
//		int limit = entity.getLimit();
//		int a = entity.getPageno() * limit;
//		int b = a - limit;
//		List details = new ArrayList();
//		String sql ="SELECT farmer.*,cow.* FROM farmer_request_list AS farmer , cow_problems AS cow WHERE cow.id = farmer.reason LIMIT ?,?";
//		FarmerRequest listdetail = this.jdbc.query(sql, new Object[] { b, limit },
//				new ResultSetExtractor<FarmerRequest>() {
//						FarmerRequest stu = new FarmerRequest();
//						Cow_diseases cow = new  Cow_diseases();
//					@Override
//					public FarmerRequest extractData(ResultSet rs) throws SQLException, DataAccessException {
//						while(rs.next()) {
//							stu=null;
//							if(stu==null) {
//								 stu.setFarmer_id(rs.getInt("farmer_id"));
//								    stu.setReason(rs.getString("reason"));
//								    stu.setDate(rs.getString("request_date"));
//								    stu.setRequest_reason(rs.getString("request_reason"));
//								    stu.setTime(rs.getString("request_time"));
//								    details.add(stu);
//							}
//							String id= rs.getString("reason");
//							if(id!= null) {
//								cow .setId(rs.getInt("id"));
//							    cow.setDiseases_name(rs.getString("diseases_name"));
//							    cow.setProblem_description(rs.getString("problem_description"));
//							    stu.setReason_id(cow);
//							    details.add(stu);
//							}
//							
//						}
//						return null;
//					}
//				});		
//		response.setStatus(true);
//		response.setMessage("Listed successfully");
//		response.setResponse(details);
//		return response;
//	}

}
