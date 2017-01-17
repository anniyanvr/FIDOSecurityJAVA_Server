/*
 * Copyright 2015 eBay Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ebayopensource.fidouaf.res.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.ebayopensource.fido.uaf.storage.DuplicateKeyException;
import org.ebayopensource.fido.uaf.storage.RegistrationRecord;
import org.ebayopensource.fido.uaf.storage.StorageInterface;
import org.ebayopensource.fido.uaf.storage.SystemErrorException;

import com.google.gson.Gson;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class StorageImpl implements StorageInterface {

	private static StorageImpl instance = new StorageImpl();
	private Map<String, RegistrationRecord> db = new HashMap<String, RegistrationRecord>();

	private StorageImpl() {
		// Init
		try {
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static StorageImpl getInstance() {
		return instance;
	}

	public void storeServerDataString(String username, String serverDataString) {
		// TODO Auto-generated method stub
	}

	public String getUsername(String serverDataString) {
		// TODO Auto-generated method stub
		return null;
	}

	public void store(RegistrationRecord[] records)
			throws DuplicateKeyException, SystemErrorException {
		if (records != null && records.length > 0) {
			for (int i = 0; i < records.length; i++) {
				if (db.containsKey(records[i].authenticator.toString())) {
					throw new DuplicateKeyException();
				}
				db.put(records[i].authenticator.toString(), records[i]);
			}

		}
	}

	public RegistrationRecord readRegistrationRecord(String key) {
		return db.get(key);
	}

	public void update(RegistrationRecord[] records) {
		// TODO Auto-generated method stub

	}

	public void deleteRegistrationRecord(String key) {
		if (db != null && db.containsKey(key)) {
			System.out
					.println("!!!!!!!!!!!!!!!!!!!....................deleting object associated with key="
							+ key);
			db.remove(key);
		}
	}

	public Map<String, RegistrationRecord> dbDump() {
		return db;
	}

	/** =============================================================== */
	// Database connectivity for MySQL server Database

	/**
	 * Get Database Connection
	 * 
	 * @throws IOException
	 */
	public Connection getConnection() throws IOException {
		String[] splitData = null;
		BufferedReader buffer = null;
		Connection conn = null;

		// 1. Load the Driver.
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		// 2. Define Connection details
		try {
			String dbSpecsFile;
			String dbURL = null, username = null, password = null;
			buffer = new BufferedReader(new FileReader(
					"C:/DB Specs/dbspecs.csv"));

			while ((dbSpecsFile = buffer.readLine()) != null) {
				if (dbSpecsFile != null) {
					splitData = dbSpecsFile.split(",");
					dbURL = splitData[0];
					username = splitData[1];
					password = splitData[2];
				}
			}

			// 3. Establish Connection.
			conn = (Connection) DriverManager.getConnection(dbURL, username,
					password);
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (buffer != null)
					buffer.close();
			} catch (IOException crunchifyException) {
				crunchifyException.printStackTrace();
			}
		}

		return conn;
	}

	/**
	 * Display All records in Database
	 * 
	 * @throws IOException
	 * @throws JSONException
	 */
	public void displayAllRecords() throws IOException, JSONException {
		ResultSet rs = null;
		Statement stmntObj = null;
		try {
			// 4. Creating Statement Obj.
			stmntObj = (Statement) getConnection().createStatement();

			// 5. Define the SQL Query.
			String query = "select * from registrationdb";

			// 6. Execute the Query.
			String name = null, pubkey = null, aaid_keyid = null, deviceId = null, otp = null, vendordetails = null;
			boolean regstats, loginstats;
			rs = stmntObj.executeQuery(query);
			while (rs.next()) {
				name = rs.getString("username");
				regstats = rs.getBoolean("regstats");
				pubkey = rs.getString("publickey");
				otp = rs.getString("otp");
				loginstats = rs.getBoolean("loginstats");
				vendordetails = rs.getString("vendordetails");
				aaid_keyid = rs.getString("aaid_keyid");
				deviceId = rs.getString("deviceId");
				// Display the details of User in Registration table.
				String strObj = "\nUserName: " + name + "Registration Status: "
						+ regstats + "\nPublickKey: " + pubkey + "\nOTP: "
						+ otp + "\nLogin Status: " + loginstats
						+ "\nVendor Details" + vendordetails + "\nAAID#KEYID: "
						+ aaid_keyid + "\nDeviceId: " + deviceId;
				Gson gson = new Gson();
				System.out.println(gson.toJson(strObj));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				getConnection().close();
				rs.close();
				stmntObj.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Insert into user details table values
	 * 
	 * @throws IOException
	 */
	public void storeInDatabase(Map<String, String> dataMap) throws IOException {
		Connection conn = getConnection();
		try {
			// 4. Creating Statement Obj.
			Statement stmntObj = (Statement) conn.createStatement();

			// 5. Define the SQL Query.
			String query = "select * from registrationdb";
			String name;

			// 6. Execute the Query.
			ResultSet rs = stmntObj.executeQuery(query);
			rs.beforeFirst();
			boolean isUserExist = false;
			while (rs.next()) {
				name = rs.getString("username");
				if (dataMap.get("Username").equals(name)) {
					isUserExist = true;
					break;
				}
			}
			if (isUserExist) {
				System.out
						.println("\nUsername: "
								+ dataMap.get("Username")
								+ " is an authenticated user!!! ...updating registration details for user.");
				// 5. Define the SQL Query.
				String sql = "UPDATE registrationdb SET regstats = true, publickey = ?, aaid_keyid = ?, deviceid = ? WHERE username = ?";
				try {
					PreparedStatement statement = (PreparedStatement) conn
							.prepareStatement(sql);
					statement.setString(1, dataMap.get("Publickey"));
					statement.setString(2, dataMap.get("aaid_keyid"));
					statement.setString(3, dataMap.get("DeviceId"));
					statement.setString(4, dataMap.get("Username"));
					int rowsInserted = statement.executeUpdate();
					if (rowsInserted > 0) {
						System.out
								.println("\nUser registration details updated successfully!");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("\nUsername: " + dataMap.get("Username")
						+ " is not an authenticated user!!!");
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to check whether uname and pwd combination are correct
	 * 
	 * @param uname
	 * @param pwd
	 * @return
	 * @throws Exception
	 */
	public boolean checkLogin(String uname, String pwd) throws Exception {
		boolean isUserAvailable = false;
		Connection dbConn = null;
		PreparedStatement stmntObj = null;
		ResultSet rs = null;
		try {
			dbConn = getConnection();
			String query = "SELECT * FROM userlogin WHERE username = ? AND password = ?";
			stmntObj = (PreparedStatement) dbConn.prepareStatement(query);
			stmntObj.setString(1, uname);
			stmntObj.setString(2, pwd);

			rs = stmntObj.executeQuery();
			if (rs.next() == true) {
				System.out.println("User is Authorized. Login sucessfull.");
				isUserAvailable = true;
				// send push notification for authentication
			} else {
				System.out.println("Invalid Username or Password.");
			}
		} catch (SQLException sqle) {
			throw sqle;
		} catch (Exception e) {
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
			stmntObj.close();
			rs.close();
		}
		return isUserAvailable;
	}

	/**
	 * Method to check whether user is pre-registered.
	 * 
	 * @param uname
	 * @param pwd
	 * @return
	 * @throws Exception
	 */
	public boolean checkUserRegDB(String uname) throws Exception {
		Connection dbConn = null;
		Statement stmt = null;
		ResultSet rs = null;
		boolean regStats = false;
		try {
			dbConn = getConnection();
			stmt = (Statement) dbConn.createStatement();
			String query = "SELECT * FROM userlogin WHERE username = '" + uname
					+ "'";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				regStats = rs.getBoolean("regstats");
				if (regStats == true) {
					System.out
							.println("User is already Registered!!!.. User can move onto Authentication");
				} else {
					System.out
							.println("User has not yet registered!!! Please register the user");
				}
			}
		} catch (SQLException sqle) {
			throw sqle;
		} catch (Exception e) {
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
			stmt.close();
			rs.close();
		}
		return regStats;
	}

	/**
	 * Insert into userLogin table
	 * 
	 * @throws IOException
	 */
	public String storeInLogin(Map<String, String> dataMap) throws IOException {
		Connection conn = getConnection();
		Statement stmntObj1 = null;
		ResultSet rs = null;
		String message = null;
		try {
			stmntObj1 = (Statement) conn.createStatement();

			String query = "select * from userlogin";
			String uname = null;

			rs = stmntObj1.executeQuery(query);
			boolean recordExists = rs.first();
			if (recordExists == false) {
				String sql = "INSERT INTO userlogin (username, password) VALUES (?, ?)";
				try {
					PreparedStatement statement = (PreparedStatement) conn
							.prepareStatement(sql);
					statement.setString(1, dataMap.get("Username"));
					statement.setString(2, dataMap.get("Password"));
					int rowsInserted = statement.executeUpdate();
					if (rowsInserted > 0) {
						System.out
								.println("\nA new user record inserted successfully in Login DB!");
					}
					message = "User Signed Up sucessfully.";
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				rs.beforeFirst();
				while (rs.next()) {
					uname = rs.getString("username");
					// If username exists, throw error.
					if (dataMap.get("Username").equals(uname)) {
						System.out
								.println("\nUsername: "
										+ dataMap.get("Username")
										+ " already exists. Please use a different username.");
						message = "Username: "
								+ dataMap.get("Username")
								+ " already exists. Please use a different username.";
					}
					// if new username, add in login database.
					else {
						String sql = "INSERT INTO userlogin (username, password) VALUES (?, ?)";
						try {
							PreparedStatement statement = (PreparedStatement) conn
									.prepareStatement(sql);
							statement.setString(1, dataMap.get("Username"));
							statement.setString(2, dataMap.get("Password"));
							int rowsInserted = statement.executeUpdate();
							if (rowsInserted > 0) {
								System.out
										.println("\nA new user record inserted successfully in Login DB!");
							}
							message = "User Signed Up sucessfully.";
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				stmntObj1.close();
				conn.close();
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return message;
	}

	/**
	 * Get Registration Record details from device details DB using the
	 * aaid_keyid combo during authentication flow.
	 */
	public RegistrationRecord readRegistrationRecordfromDB(String key)
			throws IOException {
		Connection conn = getConnection();
		RegistrationRecord result = new RegistrationRecord();
		ResultSet rs = null;
		Statement stmnt = null;
		try {
			stmnt = (Statement) conn.createStatement();

			// 5. Define the SQL Query.
			// String query =
			// "select * from registrationdb where aaid_keyid = '"+ key + "'";
			String query = "select * from key_info where aaid_keyid = '" + key
					+ "'";

			// 6. Execute the Query.
			rs = stmnt.executeQuery(query);
			rs.beforeFirst();
			while (rs.next()) {
				result.username = rs.getString("rpaccountid");
				result.PublicKey = rs.getString("publickey");
				result.deviceId = rs.getString("deviceid");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				try {
					getConnection().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				rs.close();
				stmnt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	/**
	 * Store in Registration Database
	 * 
	 * @param dataMap
	 */
	public void storeInRegDB(Map<String, String> dataMap) {
		Connection conn = null;
		// String otpForPreRegisteredUser = null;
		Date date = null;
		try {
			conn = getConnection();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		try {

			// Convert string type to date format.
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			try {
				date = (Date) formatter.parse(dataMap.get("Creation Date"));
			} catch (ParseException e) {
				e.printStackTrace();
			}

			// 4. Creating Statement Obj.
			Statement stmntObj = (Statement) conn.createStatement();

			// 5. Define the SQL Query.
			String query = "select * from registrationdb";
			String name;

			// 6. Execute the Query.
			ResultSet rs = stmntObj.executeQuery(query);
			boolean recordExists = rs.first();
			if (recordExists == false) {
				String sql = "INSERT INTO registrationdb (username, regstats, publickey, otp, loginstats, vendordetails, aaid_keyid, deviceId, otp_creationdate, devicetoken) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				try {
					PreparedStatement statement = (PreparedStatement) conn
							.prepareStatement(sql);
					statement.setString(1, dataMap.get("PhoneNumber"));
					statement.setBoolean(2, false);
					statement.setString(3, null);
					statement.setString(4, dataMap.get("OTP"));
					statement.setBoolean(5, false);
					statement.setString(6, null);
					statement.setString(7, null);
					statement.setString(8, null);
					statement.setDate(9, new java.sql.Date(date.getTime()));
					statement.setString(10, null);
					int rowsInserted = statement.executeUpdate();
					if (rowsInserted > 0) {
						System.out
								.println("\nA new user record inserted successfully in Registration DB!");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				rs.beforeFirst();
				boolean isUserExist = false;
				while (rs.next()) {
					name = rs.getString("username");
					if (dataMap.get("PhoneNumber").equals(name)) {
						isUserExist = true;
						// otpForPreRegisteredUser = rs.getString("otp");

						// update OTP for existing user in DB
						PreparedStatement ps;
						try {
							ps = (PreparedStatement) conn
									.prepareStatement("UPDATE registrationdb SET otp = ? WHERE username = ?");
							ps.setString(1, dataMap.get("OTP"));
							ps.setString(2, dataMap.get("PhoneNumber"));
							ps.executeUpdate();
							ps.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						break;
					}
				}
				if (isUserExist) {
					System.out.println("\nUsername: "
							+ dataMap.get("PhoneNumber")
							+ " is already Registered!!!. Updated the new OTP");
				} else {
					// 5. Define the SQL Query.
					String sql = "INSERT INTO registrationdb (username, regstats, publickey, otp, loginstats, vendordetails, aaid_keyid, deviceId, otp_creationdate, devicetoken) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
					try {
						PreparedStatement statement = (PreparedStatement) conn
								.prepareStatement(sql);
						statement.setString(1, dataMap.get("PhoneNumber"));
						statement.setBoolean(2, false);
						statement.setString(3, null);
						statement.setString(4, dataMap.get("OTP"));
						statement.setBoolean(5, false);
						statement.setString(6, null);
						statement.setString(7, null);
						statement.setString(8, null);
						statement.setDate(9, new java.sql.Date(date.getTime()));
						statement.setString(10, null);
						int rowsInserted = statement.executeUpdate();
						if (rowsInserted > 0) {
							System.out
									.println("\nA new user record inserted successfully in Registration DB!");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// return otpForPreRegisteredUser;
	}

	/**
	 * Method to get OTP details for a given username.
	 * 
	 * @param uname
	 * @return otp
	 */
	public Map<String, String> getOtpDetailsForUsername(String uname) {
		Connection conn = null;
		Map<String, String> otpDetails = new HashMap<String, String>();
		try {
			conn = getConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String otp = null;
		Date date = null;
		ResultSet rs = null;
		Statement stmnt = null;
		try {
			stmnt = (Statement) conn.createStatement();

			// 5. Define the SQL Query.
			String query = "select * from registrationdb where username = '"
					+ uname + "'";

			// 6. Execute the Query.
			rs = stmnt.executeQuery(query);
			rs.beforeFirst();
			while (rs.next()) {
				otp = rs.getString("otp");
				date = rs.getTimestamp("otp_creationdate");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				try {
					getConnection().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				rs.close();
				stmnt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		otpDetails.put("OTP", otp);
		otpDetails.put("Creation Date", date.toString());
		return otpDetails;
	}

	/**
	 * Update Login status to true for a user.
	 * 
	 * @param uname
	 */
	public void updateRegDBLogin(String uname) {
		Connection conn = null;
		try {
			conn = getConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		PreparedStatement ps;
		try {
			ps = (PreparedStatement) conn
					.prepareStatement("UPDATE registrationdb SET loginstats = true WHERE username = ?");
			ps.setString(1, uname);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Store Vendor data in Vendor Database.
	 * 
	 * @param dataToStore
	 */
	public void storeInVendorDB(Map<String, String> dataToStore) {
		Connection conn = null;
		Date date = null;
		boolean userWithSameVendor = false, isUserExists = false;
		try {
			conn = getConnection();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		try {
			// Convert string type to date format.
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			try {
				date = (Date) formatter.parse(dataToStore.get("Date"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			// 4. Creating Statement Obj.
			Statement stmntObj = (Statement) conn.createStatement();

			// 5. Define the SQL Query.
			String query = "select * from vendordb";
			String vendor, acntId;

			// 6. Execute the Query.
			ResultSet rs = stmntObj.executeQuery(query);
			boolean recordExists = rs.first();
			if (recordExists == false) {
				String sql = "INSERT INTO vendordb (accountid, displayname, rpdisplayname, email, otp, vendor_regstats, otp_creationdate) VALUES (?, ?, ?, ?, ?, ?, ?)";
				try {
					PreparedStatement statement = (PreparedStatement) conn
							.prepareStatement(sql);
					statement.setString(1, dataToStore.get("accountid"));
					statement.setString(2, dataToStore.get("Username"));
					statement.setString(3, dataToStore.get("Vendor"));
					// statement.setString(4, dataToStore.get("Phone"));
					statement.setString(4, dataToStore.get("email"));
					statement.setString(5, dataToStore.get("OTP"));
					statement.setBoolean(6, false);
					statement.setDate(7, new java.sql.Date(date.getTime()));

					int rowsInserted = statement.executeUpdate();
					if (rowsInserted > 0) {
						System.out
								.println("\nA new record inserted successfully in Vendor DB!");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				rs.beforeFirst();
				while (rs.next()) {
					vendor = rs.getString("rpdisplayname");
					acntId = rs.getString("accountid");

					if (dataToStore.get("accountid").equals(acntId)) {
						isUserExists = true;
						if (dataToStore.get("Vendor").equals(vendor)) {
							userWithSameVendor = true;
							break;
						}
					}
				}

				if (isUserExists && userWithSameVendor) {
					System.out
							.println("\nUser Account already present with same vendor in Database!!!");
				}
				if (isUserExists && !userWithSameVendor) {
					System.out
							.println("\nUser Account is registered previously, but with a different Relying Party. Adding this new record in vendor table!!!");
					String sql = "INSERT INTO vendordb (accountid, displayname, rpdisplayname, email, otp, vendor_regstats, otp_creationdate) VALUES (?, ?, ?, ?, ?, ?, ?)";
					try {
						PreparedStatement statement = (PreparedStatement) conn
								.prepareStatement(sql);
						statement.setString(1, dataToStore.get("accountid"));
						statement.setString(2, dataToStore.get("Username"));
						statement.setString(3, dataToStore.get("Vendor"));
						// statement.setString(4, dataToStore.get("Phone"));
						statement.setString(4, dataToStore.get("email"));
						statement.setString(5, dataToStore.get("OTP"));
						statement.setBoolean(6, false);
						statement.setDate(7, new java.sql.Date(date.getTime()));

						int rowsInserted = statement.executeUpdate();
						if (rowsInserted > 0) {
							System.out
									.println("\nA new record inserted successfully in Vendor DB!");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (!isUserExists) {
					System.out
							.println("\nNew User Account - Vendor Registration. Adding User Account in vendor table!!!.");
					String sql = "INSERT INTO vendordb (accountid, displayname, rpdisplayname, email, otp, vendor_regstats, otp_creationdate) VALUES (?, ?, ?, ?, ?, ?, ?)";
					try {
						PreparedStatement statement = (PreparedStatement) conn
								.prepareStatement(sql);
						statement.setString(1, dataToStore.get("accountid"));
						statement.setString(2, dataToStore.get("Username"));
						statement.setString(3, dataToStore.get("Vendor"));
						// statement.setString(4, dataToStore.get("Phone"));
						statement.setString(4, dataToStore.get("email"));
						statement.setString(5, dataToStore.get("OTP"));
						statement.setBoolean(6, false);
						statement.setDate(7, new java.sql.Date(date.getTime()));

						int rowsInserted = statement.executeUpdate();
						if (rowsInserted > 0) {
							System.out
									.println("\nA new record inserted successfully in Vendor DB!");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Get otp for given vendor name and username.
	 * 
	 * @param username
	 * @param vendorName
	 * @return otp
	 */
	public Map<String, String> getOtpForVendor(String phoneNumber,
			String vendorName) {
		Connection conn = null;
		Map<String, String> otpDetails = new HashMap<String, String>();
		try {
			conn = getConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String otp = null;
		Date date = null;
		ResultSet rs = null;
		Statement stmnt = null;
		try {
			stmnt = (Statement) conn.createStatement();

			// 5. Define the SQL Query.
			String query = "select * from vendordb where phonenumber = '"
					+ phoneNumber + "' and rpdisplayname = '" + vendorName
					+ "'";

			// 6. Execute the Query.
			rs = stmnt.executeQuery(query);
			rs.beforeFirst();
			while (rs.next()) {
				otp = rs.getString("otp");
				date = rs.getTimestamp("otp_creationdate");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				try {
					getConnection().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				rs.close();
				stmnt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		otpDetails.put("OTP", otp);
		otpDetails.put("Creation Date", date.toString());
		return otpDetails;
	}

	/**
	 * Updates vendor registration status to true.
	 * 
	 * @param username
	 * @param vendorName
	 */
	public void updateVendorRegStatus(String phoneNumber, String vendorName) {
		Connection conn = null;
		try {
			conn = getConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		PreparedStatement ps;
		try {
			ps = (PreparedStatement) conn
					.prepareStatement("UPDATE vendordb SET vendor_regstats = true WHERE phonenumber = ? and rpdisplayname = ?");
			ps.setString(1, phoneNumber);
			ps.setString(2, vendorName);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get phone Number for given username and vendor name from vendor table.
	 * 
	 * @param username
	 * @param vendorName
	 * @return phone number
	 */
	public String getPhoneForVendorAndUser(String username, String vendorName) {
		Connection conn = null;
		try {
			conn = getConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String phoneNumber = null;
		ResultSet rs = null;
		Statement stmnt = null;
		try {
			stmnt = (Statement) conn.createStatement();

			// 5. Define the SQL Query.
			String query = "select * from vendordb where displayname = '"
					+ username + "' and rpdisplayname = '" + vendorName + "'";

			// 6. Execute the Query.
			rs = stmnt.executeQuery(query);
			rs.beforeFirst();
			while (rs.next()) {
				phoneNumber = rs.getString("phonenumber");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				try {
					getConnection().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				rs.close();
				stmnt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return phoneNumber;
	}

	/**
	 * Update vendor details in Registration table.
	 */
	public void updateVendorInRegDB(String phone, String vendor) {
		Connection conn = null;
		try {
			conn = getConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		PreparedStatement ps;
		try {
			ps = (PreparedStatement) conn
					.prepareStatement("UPDATE registrationdb SET vendordetails = ? WHERE username = ? ");
			ps.setString(1, vendor);
			ps.setString(2, phone);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check is registration status is true for given vendorname and phone
	 * number.
	 * 
	 * @param vendorName
	 * @param phonenumber
	 * @return vendor registration status.
	 */
	public boolean checkVendorRegistration(String vendorName, String phonenumber)
			throws IOException {
		Connection dbConn = null;
		Statement stmt = null;
		ResultSet rs = null;
		boolean vendorRegStats = false;
		try {
			dbConn = getConnection();
			stmt = (Statement) dbConn.createStatement();
			String query = "SELECT * FROM vendordb WHERE rpdisplayname = '"
					+ vendorName + "' and phonenumber = '" + phonenumber + "'";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				vendorRegStats = rs.getBoolean("vendor_regstats");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				dbConn.close();
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return vendorRegStats;
	}

	/**
	 * Gets Vendor(RP) Details for given RP account Id.
	 * 
	 * @param accountId
	 *            is the RP account Id.
	 * @return
	 */
	public Map<String, String> getVendorDetails(/*
												 * String username, String
												 * vendorName
												 */String accountId) {
		Map<String, String> responseData = new HashMap<String, String>();
		Connection dbConn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			try {
				dbConn = getConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
			stmt = (Statement) dbConn.createStatement();
			// String query = "SELECT * FROM vendordb WHERE phonenumber = '" +
			// username + "' and rpdisplayname = '" + vendorName + "'";
			String query = "SELECT * FROM vendordb WHERE accountid = '"
					+ accountId + "'";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				responseData.put("User", rs.getString("displayname"));
				responseData.put("Vendor", rs.getString("rpdisplayname"));
				// responseData.put("Phone", rs.getString("phonenumber"));
				responseData.put("Email", rs.getString("email"));
				responseData.put("AccountId", rs.getString("accountid"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				dbConn.close();
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return responseData;
	}

	/**
	 * Method to store device token in Registration table to grant push
	 * notification permission to FIDO server after user confirms login via OTP.
	 * (Login Response API).
	 * 
	 * @param uname
	 * @param devicetoken
	 */
	public void storeTokenInRegDB(String uname, String devicetoken) {
		Connection conn = null;
		try {
			conn = getConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		PreparedStatement ps;
		try {
			ps = (PreparedStatement) conn
					.prepareStatement("UPDATE registrationdb SET devicetoken = ? WHERE username = ? ");
			ps.setString(1, devicetoken);
			ps.setString(2, uname);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to get device token for a given username from Reg DB.
	 * 
	 * @param username
	 * @return
	 */
	public Map<String, String> getDevicetokenForAccountId(
	/* String phoneNumber */String accountId) {
		Connection conn = null;
		String deviceId = null;
		Map<String, String> dataRetrieved = new HashMap<String, String>();
		try {
			conn = getConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ResultSet rs = null;
		Statement stmnt = null;
		try {
			stmnt = (Statement) conn.createStatement();
			String query = "select * from registrationdb where rpaccountid = '"
					+ accountId + "'";

			// 6. Execute the Query.
			rs = stmnt.executeQuery(query);

			boolean recordExists1 = rs.first();
			if (recordExists1 == false) {
				System.out
						.println("This RP Account Id is not present in registration DB");
			} else {
				rs.beforeFirst();
				while (rs.next()) {
					deviceId = rs.getString("deviceid");
					// dataRetrieved.put("deviceid",rs.getString("deviceid"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Statement stmnt2 = null;
		try {
			stmnt2 = (Statement) conn.createStatement();
			String query2 = "select * from devicedetails where deviceid = '"
					+ deviceId + "'";

			// 6. Execute the Query.
			rs = stmnt2.executeQuery(query2);
			boolean recordExists2 = rs.first();
			if (recordExists2 == false) {
				System.out
						.println("This DeviceId is not registered in DeviceDetails Table");
			} else {
				rs.beforeFirst();
				while (rs.next()) {
					dataRetrieved.put("devicetoken",
							rs.getString("devicetoken"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				try {
					getConnection().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				rs.close();
				stmnt.close();
				stmnt2.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return dataRetrieved;
	}

	/**
	 * Get the Registration Status for a device.
	 * 
	 * @param rpAccountId
	 * @return
	 */
	public boolean getIsRegisteredUser(
	/* String phoneNumber */String rpAccountId) {
		Connection conn = null;
		try {
			conn = getConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		boolean isRegistered = false;
		ResultSet rs = null;
		Statement stmnt = null;
		try {
			stmnt = (Statement) conn.createStatement();

			// 5. Define the SQL Query.
			// String query = "select * from registrationdb where username = '"
			// + uname + "'";
			// String query =
			// "select * from devicedetails where phonenumber = '" + phoneNumber
			// + "'";

			String query = "select * from registrationdb where rpaccountid = '"
					+ rpAccountId + "'";

			// 6. Execute the Query.
			rs = stmnt.executeQuery(query);
			rs.beforeFirst();
			while (rs.next()) {
				isRegistered = rs.getBoolean("regstats");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				try {
					getConnection().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				rs.close();
				stmnt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return isRegistered;
	}

	/**
	 * Removes All transaction , vendor and device details from DB for give
	 * Device Id.
	 * 
	 * @param deviceId
	 * @return
	 */
	public boolean removeVendorAndUserDetailsFromDB(
	/* String phoneNumber *//* String deviceId */) {
		Connection conn = null;

		try {
			conn = getConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		boolean isDeleted = false;
		int vendorDeleted = 0, deviceDeleted = 0, transactionDeleted = 0;

		try {
			// Delete from Transaction DB
			// String query3 =
			// "delete from transactiondb where accountid in (select rpaccountid from devicedetails where phonenumber = ?)";
			String query3 = "delete from transactiondb"; // where accountid in
															// (select
															// rpaccountid from
															// devicedetails
															// where deviceid =
															// ?)";
			PreparedStatement preparedStmt3 = (PreparedStatement) conn
					.prepareStatement(query3);
			// preparedStmt3.setString(1, phoneNumber);
			// preparedStmt3.setString(1, deviceId);
			transactionDeleted = preparedStmt3.executeUpdate();

			// Delete from device details DB
			// String query1 =
			// "delete from devicedetails where phonenumber = ?";
			String query1 = "delete from devicedetails"; // where deviceid = ?";
			PreparedStatement preparedStmt1 = (PreparedStatement) conn
					.prepareStatement(query1);
			// preparedStmt1.setString(1, phoneNumber);
			// preparedStmt1.setString(1, deviceId);
			deviceDeleted = preparedStmt1.executeUpdate();

			// Delete from Vendor DB
			// String query2 = "delete from vendordb where phonenumber = ?";
			String query2 = "delete from vendordb"; // where accountid in
													// (select rpaccountid from
													// devicedetails where
													// deviceid = ?)";
			PreparedStatement preparedStmt2 = (PreparedStatement) conn
					.prepareStatement(query2);
			// preparedStmt2.setString(1, phoneNumber);
			// preparedStmt2.setString(1, deviceId);
			vendorDeleted = preparedStmt2.executeUpdate();

			// Delete from registration DB
			/*
			 * String query1 = "delete from registrationdb where username = ?";
			 * PreparedStatement preparedStmt1 = (PreparedStatement) conn
			 * .prepareStatement(query1); preparedStmt1.setString(1, username);
			 * userDeleted = preparedStmt1.executeUpdate();
			 */

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// If both user and relying party details are cleared, mark isDeleted =
		// true.
		// if((vendorDeleted > 0 && deviceDeleted > 0)|| (deviceDeleted > 0 &&
		// vendorDeleted == 0))
		if ((deviceDeleted > 0 && vendorDeleted == 0 && transactionDeleted == 0)
				|| (deviceDeleted > 0 && vendorDeleted > 0 && transactionDeleted > 0)
				|| (deviceDeleted > 0 && vendorDeleted > 0 && transactionDeleted == 0)
				|| (deviceDeleted > 0 && vendorDeleted == 0 && transactionDeleted > 0)) {
			isDeleted = true;
		}

		return isDeleted;
	}

	/**************************************** Phase 2 - No login, OTP services ***************************************/

	/**
	 * Stores the device Id and device token in device details DB when the user
	 * first accepts push notifications on mobile on first app install.
	 * 
	 * @param dataToStore
	 *            contains the data to be stored in DB
	 * @return true/false, based on data saved or not.
	 */
	public boolean storePushNotifyEnabledDevice(Map<String, String> dataToStore) {
		Connection conn = null;
		boolean isDuplicateDeviceId = false;
		boolean deviceDataUpdate = false;
		try {
			conn = getConnection();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		try {
			// 4. Creating Statement Obj.
			Statement stmntObj = (Statement) conn.createStatement();

			// 5. Define the SQL Query.
			String query = "select * from devicedetails";
			String deviceid;

			// 6. Execute the Query.
			ResultSet rs = stmntObj.executeQuery(query);
			boolean recordExists = rs.first();
			if (recordExists == false) {
				String sql = "INSERT INTO devicedetails (deviceid, devicetoken) VALUES (?, ?)";
				try {
					// 2. Prepare statement to insert.
					PreparedStatement statement = (PreparedStatement) conn
							.prepareStatement(sql);
					statement.setString(1, dataToStore.get("deviceid"));
					statement.setString(2, dataToStore.get("devicetoken"));

					int rowsInserted = statement.executeUpdate();
					if (rowsInserted > 0) {
						System.out
								.println("\nNew Device Data successfully added in Device Table!");
						deviceDataUpdate = true;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				rs.beforeFirst();
				while (rs.next()) {
					deviceid = rs.getString("deviceid");

					if (dataToStore.get("deviceid").equals(deviceid)) {
						isDuplicateDeviceId = true;
						break;
					}
				}

				if (isDuplicateDeviceId) {
					System.out
							.println("\nDuplicate Device Id; Already registered in Database!!!");
				} else {
					System.out
							.println("\nNew Device Id registration. Adding details in device table!!!.");
					String sql = "INSERT INTO devicedetails (deviceid, devicetoken) VALUES (?, ?)";
					try {
						// 2. Prepare statement to insert.
						PreparedStatement statement = (PreparedStatement) conn
								.prepareStatement(sql);
						statement.setString(1, dataToStore.get("deviceid"));
						statement.setString(2, dataToStore.get("devicetoken"));

						int rowsInserted = statement.executeUpdate();
						if (rowsInserted > 0) {
							System.out
									.println("\nNew Device Data successfully added in Device Table!");
							deviceDataUpdate = true;
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return deviceDataUpdate;
	}

	/**
	 * Get RP details given the RP account Id.
	 * 
	 * @param accountId
	 *            is The RP Account ID.
	 * @return
	 */
	public Map<String, String> getRPDetailsForAccountId(String accountId) {
		Map<String, String> responseData = new HashMap<String, String>();
		Connection dbConn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			try {
				dbConn = getConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
			stmt = (Statement) dbConn.createStatement();
			String query = "SELECT * FROM vendordb WHERE accountid = '"
					+ accountId + "'";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				responseData
						.put("RPDisplayName", rs.getString("rpdisplayname"));
				responseData.put("DisplayName", rs.getString("displayname"));
				responseData.put("Email", rs.getString("email"));
				responseData.put("OTP", rs.getString("otp"));
				responseData.put("AccountId", rs.getString("accountid"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				dbConn.close();
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return responseData;
	}

	/**
	 * Updates and Links RP to the Device Details Table.
	 * 
	 * @param dataToUpdate
	 *            contains fields to be added in device details table.
	 */
	public void insertRPDetailsInRegDB(Map<String, String> dataToUpdate) {
		Connection conn = null;
		boolean isDeviceRegistered = false;

		try {
			conn = getConnection();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		try {
			// 4. Creating Statement Obj.
			Statement stmntObj = (Statement) conn.createStatement();

			// 5. Define the SQL Query.
			String query = "select * from devicedetails where deviceid = '"
					+ dataToUpdate.get("DeviceId") + "'";
			String deviceId = null;

			// 6. Execute the Query.
			ResultSet rs = stmntObj.executeQuery(query);
			boolean recordExists = rs.first();
			if (recordExists == false) {
				System.out.println("This Device Id is not registered: "
						+ dataToUpdate.get("DeviceId"));
			} else {
				rs.beforeFirst();
				while (rs.next()) {
					deviceId = rs.getString("deviceid");

					if (dataToUpdate.get("DeviceId").equals(deviceId)) {
						isDeviceRegistered = true;
						break;
					}
				}

				if (isDeviceRegistered) {
					System.out
							.println("\nDeviceId is registered in Database, adding device - relying party mapping details in registrationdb");
					String sql = "INSERT INTO registrationdb (deviceid, rpaccountname, regstats, rpaccountid, auth_in_progress, authstats) VALUES (?, ?, ?, ?, ?, ?)";
					try {
						// 2. Prepare statement to insert.
						PreparedStatement statement = (PreparedStatement) conn
								.prepareStatement(sql);
						statement.setString(1, dataToUpdate.get("DeviceId"));
						statement.setString(2,
								dataToUpdate.get("RPDisplayName"));
						statement.setBoolean(3, false);
						statement.setString(4, dataToUpdate.get("AccountId"));
						statement.setBoolean(5, false);
						statement.setBoolean(6, false);

						int rowsInserted = statement.executeUpdate();
						if (rowsInserted > 0) {
							System.out
									.println("\nNew Device - RP mapped Data successfully added in Registration Table!");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets user's display name given the RP display name from DB.
	 * 
	 * @param vendorname
	 *            is the relying party display name.
	 * @return
	 */
	public String getUserDisplaynameFromVendorName(String vendorname,
			String accountId) {
		Connection conn = null;
		String userDisplayName = null;
		try {
			conn = getConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ResultSet rs = null;
		Statement stmnt = null;
		try {
			stmnt = (Statement) conn.createStatement();

			// 5. Define the SQL Query.
			String query = "select * from vendordb where rpdisplayname = '"
					+ vendorname + "' and accountid = '" + accountId + "'";

			// 6. Execute the Query.
			rs = stmnt.executeQuery(query);
			rs.beforeFirst();
			while (rs.next()) {
				userDisplayName = rs.getString("displayname");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				try {
					getConnection().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				rs.close();
				stmnt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return userDisplayName;
	}

	/**
	 * Method to update the registration status (regstats) field in device
	 * details table.
	 * 
	 * @param dataToStore
	 *            contains params to be stored in DB.
	 */
	public void updateRegStatsInRegDB(String accountId, String deviceId) {
		Connection conn = null;
		String deviceid = null, accountid = null;
		boolean isRegisteredCombo = false;
		try {
			conn = getConnection();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		try {
			// 4. Creating Statement Obj.
			Statement stmntObj = (Statement) conn.createStatement();

			// 5. Define the SQL Query.
			String query = "select * from registrationdb where rpaccountid = '"
					+ accountId + "' and deviceid = '" + deviceId + "'";

			// 6. Execute the Query.
			ResultSet rs = stmntObj.executeQuery(query);
			boolean recordExists = rs.first();
			if (recordExists == false) {
				System.out
						.println("This combination of DeviceId + AccountId is not present in DB");
			} else {
				rs.beforeFirst();
				while (rs.next()) {
					deviceid = rs.getString("deviceid");
					accountid = rs.getString("rpaccountid");

					if (deviceId.equals(deviceid)) {
						System.out.println("Device Id Matches!");
						if (accountId.equals(accountid)) {
							System.out.println("RP Account Id Matches");
							isRegisteredCombo = true;
							break;
						}
					}
				}

				if (isRegisteredCombo) {
					System.out
							.println("\nUpdating Reg Stats for this combo to true");
					PreparedStatement ps;
					try {
						ps = (PreparedStatement) conn
								.prepareStatement("UPDATE registrationdb SET regstats = true WHERE deviceid = ? and rpaccountid = ?");
						ps.setString(1, deviceId);
						ps.setString(2, accountId);
						ps.executeUpdate();
						ps.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Displays all records in Device Details table.
	 */
	public void showDeviceDBRecords() {
		ResultSet rs = null;
		Statement stmntObj = null;
		try {
			// 4. Creating Statement Obj.
			try {
				stmntObj = (Statement) getConnection().createStatement();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// 5. Define the SQL Query.
			String query = "select * from devicedetails";

			// 6. Execute the Query.
			String deviceId = null, deviceToken = null;

			rs = stmntObj.executeQuery(query);
			while (rs.next()) {
				deviceId = rs.getString("deviceid");
				deviceToken = rs.getString("devicetoken");

				// Display the details of device from Device details table.
				String strObj = "\nDeviceId: " + deviceId + "\nDevice Token: "
						+ deviceToken;
				System.out.println(strObj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				try {
					getConnection().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				rs.close();
				stmntObj.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Updates the Vendor Registration status in vendor table.
	 * 
	 * @param rpdisplayName
	 * @param accountId
	 */
	public void updateVendorRegStatusInVendorDB(String rpdisplayName,
			String accountId) {
		Connection conn = null;
		try {
			conn = getConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		PreparedStatement ps;
		try {
			ps = (PreparedStatement) conn
					.prepareStatement("UPDATE vendordb SET vendor_regstats = true WHERE rpdisplayname = ? and accountid = ?");
			ps.setString(1, rpdisplayName);
			ps.setString(2, accountId);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Stores transaction details in Transaction table.
	 * 
	 * @param dataToStore
	 */
	public void storeInTransactionDB(Map<String, String> dataToStore) {
		Connection conn = null;
		try {
			conn = getConnection();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		try {
			// 4. Creating Statement Obj.
			Statement stmntObj = (Statement) conn.createStatement();

			// 5. Define the SQL Query.
			String query = "select * from transactiondb";

			// 6. Execute the Query.
			ResultSet rs = stmntObj.executeQuery(query);
			boolean recordExists = rs.first();
			if (recordExists == false) {
				String sql = "INSERT INTO transactiondb (accountid, contents, appid) VALUES (?, ?, ?)";
				try {
					// 2. Prepare statement to insert.
					PreparedStatement statement = (PreparedStatement) conn
							.prepareStatement(sql);
					statement.setString(1, dataToStore.get("accountid"));
					statement.setString(2, dataToStore.get("content"));
					statement.setString(3, dataToStore.get("appid"));

					int rowsInserted = statement.executeUpdate();
					if (rowsInserted > 0) {
						System.out
								.println("\nNew Transaction Data successfully added in Transaction Table!");
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				String sql = "INSERT INTO transactiondb (accountid, contents, appid) VALUES (?, ?, ?)";
				try {
					// 2. Prepare statement to insert.
					PreparedStatement statement = (PreparedStatement) conn
							.prepareStatement(sql);
					statement.setString(1, dataToStore.get("accountid"));
					statement.setString(2, dataToStore.get("content"));
					statement.setString(3, dataToStore.get("appid"));

					int rowsInserted = statement.executeUpdate();
					if (rowsInserted > 0) {
						System.out
								.println("\nNew Transaction Data successfully added in Transaction Table!");
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets user's display name and device token using accountid from vendor and
	 * device details tables respectively.
	 * 
	 * @param accountId
	 * @return
	 */
	public Map<String, String> getDeviceTokenFromAccountId(String accountId) {
		Connection conn = null;
		Map<String, String> dataRetrieved = new HashMap<String, String>();

		try {
			conn = getConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ResultSet rs = null;
		Statement stmnt = null;
		// Get device token
		try {
			stmnt = (Statement) conn.createStatement();

			// 5. Define the SQL Query.
			String query = "select * from devicedetails where rpaccountid = '"
					+ accountId + "'";

			// 6. Execute the Query.
			rs = stmnt.executeQuery(query);
			rs.beforeFirst();
			while (rs.next()) {
				dataRetrieved.put("Token", rs.getString("devicetoken"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				try {
					getConnection().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				rs.close();
				stmnt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return dataRetrieved;
	}

	/**
	 * Get Trancation table details using Account Id.
	 * 
	 * @param rpAccountId
	 * @return
	 */
	public Map<String, String> getTransactionUsingAccountId(String rpAccountId) {
		Connection conn = null;
		Map<String, String> dataRetrieved = new HashMap<String, String>();

		try {
			conn = getConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ResultSet rs = null;
		Statement stmnt = null;
		// Get user display name.
		try {
			stmnt = (Statement) conn.createStatement();

			// 5. Define the SQL Query.
			String query = "select * from transactiondb where accountid = '"
					+ rpAccountId + "'";

			// 6. Execute the Query.
			rs = stmnt.executeQuery(query);
			boolean recordExists1 = rs.first();
			if (recordExists1 == false) {
				System.out
						.println("No transaction present for given Account Id in DB");
			} else {
				rs.beforeFirst();
				while (rs.next()) {
					dataRetrieved.put("content", rs.getString("contents"));
					dataRetrieved.put("appid", rs.getString("appid"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				try {
					getConnection().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				rs.close();
				stmnt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return dataRetrieved;
	}

	/*****************************************************************************************************************/

	/**
	 * Delete all records in all tables when user requests for FIDO
	 * DeRegistration
	 * 
	 * @param key
	 *            is the aaid_keyid combo.
	 */
	public void deleteAllDBRecords(String key) {
		Connection conn = null;

		try {
			conn = getConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			// Delete from Transaction DB
			String query3 = "delete from transactiondb where accountid in (select rpaccountid from devicedetails where aaid_keyid = ?)";
			PreparedStatement preparedStmt3 = (PreparedStatement) conn
					.prepareStatement(query3);
			preparedStmt3.setString(1, key);
			preparedStmt3.executeUpdate();

			// Delete from Vendor DB
			String query2 = "delete from vendordb where accountid in (select rpaccountid from devicedetails where aaid_keyid = ?)";
			PreparedStatement preparedStmt2 = (PreparedStatement) conn
					.prepareStatement(query2);
			preparedStmt2.setString(1, key);
			preparedStmt2.executeUpdate();

			// Delete from Device Details DB
			String query1 = "delete from devicedetails where aaid_keyid = ?";
			PreparedStatement preparedStmt1 = (PreparedStatement) conn
					.prepareStatement(query1);
			preparedStmt1.setString(1, key);
			preparedStmt1.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Method to update authstats field in DeviceDetails DB to true to mark the
	 * success of auth response API.
	 * 
	 * @param aaid_Keyid
	 */
	public void updateAuthStatusInDB(String aaid_Keyid) {
		Connection conn = null;
		String deviceId = null, rpAccountId = null;
		try {
			conn = getConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ResultSet rs = null;
		Statement stmnt = null;
		try {
			stmnt = (Statement) conn.createStatement();
			String query = "select * from key_info where aaid_keyid = '"
					+ aaid_Keyid + "'";

			// 6. Execute the Query.
			rs = stmnt.executeQuery(query);

			boolean recordExists1 = rs.first();
			if (recordExists1 == false) {
				System.out
						.println("This AAID_KEYID is not present in Key_Info table");
			} else {
				rs.beforeFirst();
				while (rs.next()) {
					deviceId = rs.getString("deviceid");
					rpAccountId = rs.getString("rpaccountid");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		PreparedStatement ps;
		try {
			ps = (PreparedStatement) conn
					.prepareStatement("UPDATE registrationdb SET authstats = true WHERE deviceid = ? and rpaccountid = ?");
			ps.setString(1, deviceId);
			ps.setString(2, rpAccountId);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				try {
					getConnection().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				rs.close();
				stmnt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method that marks the start of authentication by making auth_in_progress
	 * field as true in deviceDetais table.
	 * 
	 * @param accountId
	 */
	public void markAuthenticationInProgressInDB(String accountId) {
		Connection conn = null;
		try {
			conn = getConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		PreparedStatement ps;
		try {
			ps = (PreparedStatement) conn
					.prepareStatement("UPDATE registrationdb SET auth_in_progress = true WHERE rpaccountid = ?");
			ps.setString(1, accountId);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method that resets the auth related flag in device details table after 2
	 * mins into authentication from RP website.
	 * 
	 * @param accountId
	 */
	public void resetAuthFlagsInRegDB(String accountId) {
		Connection conn = null;
		try {
			conn = getConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		PreparedStatement ps;
		try {
			ps = (PreparedStatement) conn
					.prepareStatement("UPDATE registrationdb SET auth_in_progress = false, authstats = false WHERE rpaccountid = ?");
			ps.setString(1, accountId);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method that gets the Authentication related flags from devicedetails
	 * table for given accountid.
	 * 
	 * @param accountId
	 * @return
	 */
	public Map<String, Boolean> getAuthFlagsforAccountId(String accountId) {
		Map<String, Boolean> responseData = new HashMap<String, Boolean>();
		Connection dbConn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			try {
				dbConn = getConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
			stmt = (Statement) dbConn.createStatement();
			String query = "SELECT * FROM registrationdb WHERE rpaccountid = '"
					+ accountId + "'";
			rs = stmt.executeQuery(query);
			boolean recordExists1 = rs.first();
			if (recordExists1 == false) {
				System.out
						.println("This RP Account Id is not present in registration DB");
			} else {
				rs.beforeFirst();
				while (rs.next()) {
					responseData.put("auth_in_progress",
							rs.getBoolean("auth_in_progress"));
					responseData.put("authstats", rs.getBoolean("authstats"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				dbConn.close();
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return responseData;
	}

	/**
	 * Method that stores each public key and aaid_keyid pair for each assertion
	 * processed for each accountid.
	 * 
	 * @param dataToStore
	 */
	public void storeInKeyInfoDB(Map<String, String> dataToStore) {
		Connection conn = null;
		String deviceid = null, accountid = null, aaid_keyid = null, pubKey = null;
		boolean isDuplicateAAID_KEYID = false, isDuplicatePubKey = false;
		try {
			conn = getConnection();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		try {
			// 4. Creating Statement Obj.
			Statement stmntObj = (Statement) conn.createStatement();

			// 5. Define the SQL Query.
			String query = "select * from key_info";

			// 6. Execute the Query.
			ResultSet rs = stmntObj.executeQuery(query);
			boolean recordExists = rs.first();
			if (recordExists == false) {
				String sql = "INSERT INTO key_info (deviceid, rpaccountid, publickey, aaid_keyid) VALUES (?, ?, ?, ?)";
				try {
					// 2. Prepare statement to insert.
					PreparedStatement statement = (PreparedStatement) conn
							.prepareStatement(sql);
					statement.setString(1, dataToStore.get("DeviceId"));
					statement.setString(2, dataToStore.get("AccountId"));
					statement.setString(3, dataToStore.get("Publickey"));
					statement.setString(4, dataToStore.get("aaid_keyid"));

					int rowsInserted = statement.executeUpdate();
					if (rowsInserted > 0) {
						System.out
								.println("\nNew Key Data successfully added in Key_Info Table!");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				rs.beforeFirst();
				while (rs.next()) {
					deviceid = rs.getString("deviceid");
					accountid = rs.getString("rpaccountid");
					aaid_keyid = rs.getString("publickey");
					pubKey = rs.getString("aaid_keyid");

					if (dataToStore.get("DeviceId").equals(deviceid)) {
						System.out.println("Device Id Exist!");
						if (dataToStore.get("AccountId").equals(accountid)) {
							System.out
									.println("Device Id Exist with the passed AccountId!");
							if (dataToStore.get("Publickey").equals(pubKey)) {
								System.out
										.println("Same Public key exists with passed DeviceId and AccountId!");
								isDuplicatePubKey = true;
								break;
							}
							if (dataToStore.get("aaid_keyid")
									.equals(aaid_keyid)) {
								System.out
										.println("Same AAID_KEYID combo exists with passed DeviceId and AccountId");
								isDuplicateAAID_KEYID = true;
								break;
							}
						}
					}
				}

				if (isDuplicatePubKey) {
					System.out.println("\nDuplicate Record: PUBLIC KEY!!!");
				} else if (isDuplicateAAID_KEYID) {
					System.out.println("\nDuplicate Record: AAID_KEYID!!!");
				} else {
					String sql = "INSERT INTO key_info (deviceid, rpaccountid, publickey, aaid_keyid) VALUES (?, ?, ?, ?)";
					try {
						// 2. Prepare statement to insert.
						PreparedStatement statement = (PreparedStatement) conn
								.prepareStatement(sql);
						statement.setString(1, dataToStore.get("DeviceId"));
						statement.setString(2, dataToStore.get("AccountId"));
						statement.setString(3, dataToStore.get("Publickey"));
						statement.setString(4, dataToStore.get("aaid_keyid"));

						int rowsInserted = statement.executeUpdate();
						if (rowsInserted > 0) {
							System.out
									.println("\nNew Key Data successfully added in Key_Info Table!");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Displays all data in Key_Info table.
	 */
	public void showKeyInfoDBRecords() {
		ResultSet rs = null;
		Statement stmntObj = null;
		try {
			// 4. Creating Statement Obj.
			try {
				stmntObj = (Statement) getConnection().createStatement();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// 5. Define the SQL Query.
			String query = "select * from key_info";

			// 6. Execute the Query.
			String deviceId = null, rpAccountId = null, pubKey = null, aaid_keyid = null;

			rs = stmntObj.executeQuery(query);
			while (rs.next()) {
				deviceId = rs.getString("deviceid");
				rpAccountId = rs.getString("rpaccountid");
				pubKey = rs.getString("publickey");
				aaid_keyid = rs.getString("aaid_keyid");

				// Display the details of device from Device details table.
				String strObj = "\nDeviceId: " + deviceId + "\nRP Account ID: "
						+ rpAccountId + "\nPublic Key: " + pubKey
						+ "\nAAID_KEYID: " + aaid_keyid;
				System.out.println(strObj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				try {
					getConnection().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				rs.close();
				stmntObj.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to get AAIDs for given deviceId and accountId
	 * 
	 * @param deviceId
	 * @param accountId
	 * @return string array of AAIDs.
	 */
	public String[] getAAIDforDeviceIdAccountId(String deviceId,
			String accountId) {
		String[] aaids = null;
		String aaid = null;

		Connection dbConn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			try {
				dbConn = getConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
			stmt = (Statement) dbConn.createStatement();
			String query = "select * from key_info where deviceid = '"
					+ deviceId + "' and rpaccountid = '" + accountId + "'";
			rs = stmt.executeQuery(query);
			rs.last();
			aaids = new String[rs.getRow()];
			rs.beforeFirst();
			for (int i = 0; i < aaids.length; i++) {
				while (rs.next()) {
					aaid = rs.getString("aaid_keyid");
					String[] parts = aaid.split("#");
					String part1 = parts[0]; // 004
					String part2 = parts[1];
					String part3 = parts[2];
					aaid = part1 + "#" + part2;

					aaids[i] = aaid;
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				dbConn.close();
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return aaids;
	}

	/**
	 * Method to display all records in registrationdb table.
	 */
	public void showRegistrationDBRecords() {
		ResultSet rs = null;
		Statement stmntObj = null;
		try {
			// 4. Creating Statement Obj.
			try {
				stmntObj = (Statement) getConnection().createStatement();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// 5. Define the SQL Query.
			String query = "select * from registrationdb";

			// 6. Execute the Query.
			String deviceId = null, rpAccountName = null, rpAccountId = null;
			boolean regStats = false, auth_in_progress = false, authStats = false;

			rs = stmntObj.executeQuery(query);
			while (rs.next()) {
				deviceId = rs.getString("deviceid");
				rpAccountName = rs.getString("rpaccountname");
				regStats = rs.getBoolean("regstats");
				rpAccountId = rs.getString("rpaccountid");
				auth_in_progress = rs.getBoolean("auth_in_progress");
				authStats = rs.getBoolean("authstats");

				// Display the details of registration from registrationdb
				// table.
				String strObj = "\nDeviceId: " + deviceId
						+ "\nRP Account Name: " + rpAccountName
						+ "\nRegistration Status: " + regStats
						+ "\nRP AccountId: " + rpAccountId
						+ "\nAuthentication_In_Progress?: " + auth_in_progress
						+ "\nAuthentication Status: " + authStats;
				System.out.println(strObj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				try {
					getConnection().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				rs.close();
				stmntObj.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method that gets deviceid for given accountId which is passed as username
	 * at the time of RegRequest GET API.
	 * 
	 * @param username
	 * @return
	 */
	public String getDeviceIdForAccountId(String accountId) {
		Connection conn = null;
		String deviceId = null;

		try {
			conn = getConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ResultSet rs = null;
		Statement stmnt = null;
		// Get device token
		try {
			stmnt = (Statement) conn.createStatement();

			// 5. Define the SQL Query.
			String query = "select * from registrationdb where rpaccountid = '"
					+ accountId + "'";

			// 6. Execute the Query.
			rs = stmnt.executeQuery(query);
			rs.beforeFirst();
			while (rs.next()) {
				deviceId = rs.getString("deviceid");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				try {
					getConnection().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				rs.close();
				stmnt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return deviceId;
	}

	/**
	 * Method that sets user context and authenticators enforced during
	 * authentication (Flexible Authentication)
	 * 
	 * @param responseData
	 */
	public int updateUserContextAndAuthenticators(
			Map<String, String> responseData) {
		Connection conn = null;
		int dataInserted = 0;
		try {
			conn = getConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		PreparedStatement ps;
		try {
			ps = (PreparedStatement) conn
					.prepareStatement("UPDATE registrationdb SET context = ?, authenticators_enforced = ? WHERE rpaccountid = ?");
			ps.setString(1, responseData.get("context"));
			ps.setString(2, responseData.get("authenticators"));
			ps.setString(3, responseData.get("accountId"));
			dataInserted = ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dataInserted;
	}

	/**
	 * Get enforced AAIDs stored in registrationdb table based on user context.
	 * 
	 * @param accountId
	 * @return
	 */
	public String[] getAAIDsEnforcedForAccountId(String accountId) {
		ArrayList<String> aaids = new ArrayList<String>();
		String aaid = null;
		String aaidList[] = null;

		Connection dbConn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			try {
				dbConn = getConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
			stmt = (Statement) dbConn.createStatement();
			String query = "select * from registrationdb where rpaccountid = '"
					+ accountId + "'";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				aaid = rs.getString("authenticators_enforced");
			}
			// Replace "[" and "]" with space in the string
			aaid = aaid.replace("[", "");
			aaid = aaid.replace("]", "");
			aaid = aaid.replace(" ", "");
			if (aaid.contains(",")) {
				String[] parts = aaid.split(",");
				for (int j = 0; j < parts.length; j++) {
					aaids.add(parts[j]);
				}
			} else {
				aaids.add(aaid);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				dbConn.close();
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		// Convert ArrayList to String[] to match the method signatures.
		aaidList = aaids.toArray(new String[aaids.size()]);
		return aaidList;
	}

	/**
	 * Method that gets all stored public keys for given accountId.
	 * 
	 * @param accountId
	 * @return
	 */
	public String[] getPublicKeyForAccountId(String accountId) {
		ArrayList<String> pubs = new ArrayList<String>();
		String pubKeys[] = null;

		Connection dbConn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			try {
				dbConn = getConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
			stmt = (Statement) dbConn.createStatement();
			String query = "select * from key_info where rpaccountid = '"
					+ accountId + "'";
			rs = stmt.executeQuery(query);
			for (int i = 0; i < pubs.size(); i++) {
				while (rs.next()) {
					pubs.add(rs.getString("publickey"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				dbConn.close();
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		// Convert ArrayList to String[] to match the method signatures.
		pubKeys = pubs.toArray(new String[pubs.size()]);
		return pubKeys;
	}

	/**
	 * Method that gets stored Public Key for given AAID and AccountId.
	 * 
	 * @param rpAccountId
	 * @param aaid
	 * @return
	 */
	public String getPubKeyforAccountIdAndAAID(String rpAccountId, String aaid) {
		String publicKey = null;
		Connection dbConn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			try {
				dbConn = getConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
			stmt = (Statement) dbConn.createStatement();
			String query = "select * from key_info where rpaccountid = '"
					+ rpAccountId + "' and aaid_keyid like '" + aaid + "%'";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				publicKey = rs.getString("publickey");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				dbConn.close();
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return publicKey;
	}

	/**
	 * Method that gets context stored for given accountID from registrationdb table.
	 * @param rpAccountId
	 * @return
	 */
	public String getContextForAccountID(String rpAccountId) {
		String context = null;
		Connection dbConn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			try {
				dbConn = getConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
			stmt = (Statement) dbConn.createStatement();
			String query = "select * from registrationdb where rpaccountid = '"
					+ rpAccountId + "'";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				context = rs.getString("context");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7. Close Connection
			try {
				dbConn.close();
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return context;
	}
}
