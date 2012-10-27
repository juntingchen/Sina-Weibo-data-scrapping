// Author: Chen Junting
// Date: 2012-01-01
// Copyright c Chen Junting 2012

import weibo4j.Users;
import weibo4j.Weibo;
import weibo4j.examples.Log;
import weibo4j.model.Paging;
import weibo4j.model.User;
import weibo4j.model.WeiboException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import weibo4j.model.Paging;
import weibo4j.model.Status;
import weibo4j.Timeline;

import weibo4j.Friendships;


public class WeiboAnalysis {
	public Statement statement = null;
	public Connection conn = null;
	public int API_used_count = 0;
	//public String access_token = "2.00btx2QCJNQayC1f21dcfd000iLdVt"; 
	Weibo weibo = null;
	//= new Weibo();
	//weibo.setToken(access_token);
	
	public void InitialMysql(){
		// This is just to connect to the mySQL database
		
		String driver = "com.mysql.jdbc.Driver";
		String database_name = "sinaweibo";
		String url = "jdbc:mysql://127.0.0.1:3306/" + database_name;
		String user = "root";
		String password = "allen";

		try{
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, password);
			if(!conn.isClosed()){
				System.out.println("Succeeded connection to the Database.");
				System.out.println("Database [" + database_name + "] used.");
			}
			statement = conn.createStatement();

		}catch(ClassNotFoundException e) {   
			System.out.println("Sorry,can`t find the Driver!");   
			e.printStackTrace();   
			} catch(SQLException e) {   
			e.printStackTrace();   
			} catch(Exception e) {   
			e.printStackTrace();   
			}
		
	}
	
	public void closeMysql(){
		try{
			
			conn.close();
			if(conn.isClosed())
				System.out.println("Succeeded closes the Database.");
		}catch(SQLException e) {   
			e.printStackTrace();   
			} catch(Exception e) {   
			e.printStackTrace();   
			}
	}
	public void InitialWeibo(String access_token)
	{
		weibo = new Weibo();
		weibo.setToken(access_token);
	}
	
	public void CreateTableUserProfile()
	{
		// Create the user_profile table
		String sql_create_tb = "create table if not exists "+"user_profile" +
				"(tbIndex INT AUTO_INCREMENT PRIMARY KEY," + //Table index, auto increment
				"id varchar(15) not NULL," +   //用户UID
				"screenName varchar(50)," + //微博昵称
				"province INTEGER," +   //省份编码（参考省份编码表）
				"city INTEGER," +   //城市编码（参考城市编码表）
				"location varchar(100)," +   //地址
				"url varchar(255)," +   //用户博客地址
				"profileImageUrl varchar(255),"+  //自定义图像
				"gender char(1),"+  //性别,m--男，f--女,n--未知
				"followersCount INTEGER,"+   //粉丝数
				"friendsCount INTEGER,"+  //关注数
				"statusesCount INTEGER,"+  //微博数
				"createdAt DATE,"+  //创建时间
				"verified bit,"+  //加V标示，是否微博认证用户
				"biFollowersCount INTEGER," +	//互粉数
				"NewUser bit)";  // Whether it is new to database.
				
				try {
					int rs = statement.executeUpdate(sql_create_tb); 
					
				} catch(SQLException e) {   
					e.printStackTrace();   
				} catch(Exception e) {   
					e.printStackTrace();   
				}
				
	}


	public void CreateTable_ProcStatus()
	{
		String sql_create_tb = "create table if not exists " + "Proc_status" +
				"(tbIndex INT PRIMARY KEY, user_profile_ptr INT)";
		
		try {
			int rt = statement.executeUpdate(sql_create_tb);
			
			String sql_rq = "select * from proc_status where tbIndex  =  1";
			ResultSet rs = statement.executeQuery(sql_rq);
			
			if (!rs.next()){
				sql_rq = "insert into proc_status(tbIndex, user_profile_ptr) values(1, -1)";
				PreparedStatement ps = conn.prepareStatement(sql_rq);
				int result = ps.executeUpdate();
				
				if (result >= 0)
					System.out.println("Succeeded in creating table: Proc_status.");
			}
			
			
		} catch(SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void WriteDetailInfobyUserID(String uid)
	{
		Users um = new Users();
		String sqlst; //sql statement
			// Retrieve the user information from server
			//User user = um.showUserById(uid);
		
			// check the uid and write the user info
			try {
				//Test if the user already exists in the table
				sqlst = "select * from user_profile where id=\'" + uid + "\'";
				ResultSet rs = statement.executeQuery(sqlst);  
				
				if(rs.next() == true) //update
				{
					// The user does exist in the table
					rs.close();
					
				}
				else  //add the user into user_profile
				{
					rs.close();
					
					try {
						//Retrieve user info from server
						User user = um.showUserById(uid);
					
						sqlst = "insert into user_profile(id,screenName,province," +
								"city,location,url,profileImageUrl,gender," +
								"followersCount,friendsCount,statusesCount," +
								"createdAt,verified,biFollowersCount, NewUser) " +
								"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						PreparedStatement ps=conn.prepareStatement(sqlst);
						ps.setString(1, user.getId());
		                ps.setString(2, user.getScreenName());
		                ps.setInt(3, user.getProvince());
		                ps.setInt(4, user.getCity());
		                ps.setString(5, user.getLocation());
		                
		                //System.out.println(user.getLocation() );
		                // ps.setString(6, user.getURL().toString());
		                if(user.getURL() != null)
		                	ps.setString(6, user.getURL().toString());
		                else
		                	ps.setString(6, null);
		                ps.setString(7, user.getProfileImageURL().toString());
		                ps.setString(8, user.getGender());
		                ps.setInt(9, user.getFollowersCount());
						ps.setInt(10, user.getFriendsCount());
						ps.setInt(11, user.getStatusesCount());
						// ps.setDate(12, user.getCreatedAt());
						ps.setDate(12, new java.sql.Date(user.getCreatedAt()
								.getTime()));
						ps.setBoolean(13, user.isVerified());
						ps.setInt(14,user.getbiFollowersCount());
						ps.setInt(15, 1); // This is a newly added user.
						//String s = ""+user.getbiFollowersCount();
						//System.out.println(s);
						// ps.setBoolean(parameterIndex, x);
						// ps.setDate(parameterIndex, x);
						
						int result = ps.executeUpdate();
						//System.out.println(("Insert" + uid));
						if (result <= 0)
							System.out.println(("Insert the userinfo into the database is wrong " + uid));
						
					} catch (WeiboException e) {
						e.printStackTrace();
					}
				}
				


			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	public int WriteSimpleInfo(String uid)
	{
		try {
			String sql = "select * from user_profile where id = \'" + uid + "\'";
			ResultSet rs = statement.executeQuery(sql);
			
			if(rs.next() == true) //update
			{
				rs.close();
				return 0;	// No new user added
			
			}
			else  //insert
			{
				rs.close();
				sql = "insert into user_profile(id, NewUser) values(?,?)";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, uid);
				ps.setInt(2,1);
				
				int result = ps.executeUpdate();
				if (result < 0) {
					System.out.println("Failed to insert user: " + uid);
					return -1;
				}
				else {
					return 1; // A new user added
				}
			}
						
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
			
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int WriteDetailInfobyUser(String uid, User user, int bNew, int bUpdate)
	{
		
		try {
			
			String sql = "select tbIndex, screenName from user_profile where id=\'" + uid + "\'";
			ResultSet rs = statement.executeQuery(sql);  
			
			if(!rs.next())
			{
				// User does not exist in the database. Insert.
				rs.close();
				sql="insert into user_profile(id,screenName,province," +
						"city,location,url,profileImageUrl,gender," +
						"followersCount,friendsCount,statusesCount," +
						"createdAt,verified,biFollowersCount, NewUser) " +
						"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement ps=conn.prepareStatement(sql);
				ps.setString(1, user.getId());
                ps.setString(2, user.getScreenName());
                ps.setInt(3, user.getProvince());
                ps.setInt(4, user.getCity());
                ps.setString(5, user.getLocation());
                
              //  System.out.println(user.getLocation() );
                if(user.getURL() != null)
                	ps.setString(6, user.getURL().toString());
                else
                	ps.setString(6, null);
                
                ps.setString(7, user.getProfileImageURL().toString());
                ps.setString(8, user.getGender());
                ps.setInt(9, user.getFollowersCount());
				ps.setInt(10, user.getFriendsCount());
				ps.setInt(11, user.getStatusesCount());
				// ps.setDate(12, user.getCreatedAt());
				ps.setDate(12, new java.sql.Date(user.getCreatedAt()
						.getTime()));
				ps.setBoolean(13, user.isVerified());
				ps.setInt(14,user.getbiFollowersCount());
				ps.setInt(15, bNew);
				
				int result = ps.executeUpdate();
				if (result <= 0) {
					System.out.println(("Failed to insert user full profile: " + uid));
					return -1;
					
				} else {
					return 1; // A new user Added
				}
				
			} 
			else  
			{
				// User does exist in the database. Update. 
				String screenName = rs.getString("screenName");
				rs.close();
				
				if (bUpdate == 0 && screenName != null)	// Is it to update the user if it does exist?
					return 0;	// An existing user, done nothing.
				
				sql="update user_profile set id=?, screenName=?, province=?," +
						"city=?,location=?,url=?,profileImageUrl=?,gender=?," +
						"followersCount=?,friendsCount=?,statusesCount=?," +
						"createdAt=?,verified=?,biFollowersCount=?, NewUser=? " +
						"where id = \'" + uid + "\'";
				PreparedStatement ps=conn.prepareStatement(sql);
				ps.setString(1, user.getId());
                ps.setString(2, user.getScreenName());
                ps.setInt(3, user.getProvince());
                ps.setInt(4, user.getCity());
                ps.setString(5, user.getLocation());
                
              //  System.out.println(user.getLocation() );
                if(user.getURL() != null)
                	ps.setString(6, user.getURL().toString());
                else
                	ps.setString(6, null);
                
                ps.setString(7, user.getProfileImageURL().toString());
                ps.setString(8, user.getGender());
                ps.setInt(9, user.getFollowersCount());
				ps.setInt(10, user.getFriendsCount());
				ps.setInt(11, user.getStatusesCount());
				// ps.setDate(12, user.getCreatedAt());
				ps.setDate(12, new java.sql.Date(user.getCreatedAt()
						.getTime()));
				ps.setBoolean(13, user.isVerified());
				ps.setInt(14,user.getbiFollowersCount());
				ps.setInt(15, bNew); 	//This is a newly added user.
				//String s = ""+user.getbiFollowersCount();
				//System.out.println(s);
				// ps.setBoolean(parameterIndex, x);
				// ps.setDate(parameterIndex, x);
				
				int result = ps.executeUpdate();
				if (result <= 0) {
					System.out.println(("Failed to update full user profile: " + uid));
					return -1;
					
				} else {
					return 1; // An old user updated
				}
			}
			
		
		}catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		
		
	}
	
	public void WriteUserFollowers(String uid, String TableName) {

		try {
			
			//Insert one user into the uid_followers table
			/*
			String sql = "select * from " + TableName + " where id=\'" + uid + "\'";
			ResultSet rs = statement.executeQuery(sql);
			
			if (rs.next() == true) // update
			{
				rs.close();
				//System.out.println(("update" + uid));

			} else // insert
			{
				rs.close();
				*/
			String	sql = "insert into " + TableName +"(id)"
						+ "values(?)";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, uid);
				
				int result = ps.executeUpdate();
				//System.out.println(("Insert" + uid));
				if (result <= 0)
					System.out
							.println(("Insert the userinfo into the database is wrong" + uid));
				/*
			}*/

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

	public void GetFollowersByID(String uid) {
		GetFollowersByID(uid, -1);
		
	}
	
	public int GetFollowersByID(String uid, int tbIndex) {
		String sql;
		ResultSet rs;
		int Count_Followers;
		int result;
		int each_count = 5000;
		int MAXCOUNT = 5000;
		int i;
		int UserAdded_count = 0;
		int NewUser_count = 0;
		
		String TableName;

		// create the uid_Followers table, name: uid_followers
		try {
			
			// Get the full user information from user_profile table
			if (tbIndex == -1)
				sql = "select * from user_profile where id=\'" + uid + "\'";
			else
				sql = "select * from user_profile where tbIndex=\'" + tbIndex + "\'";
			
			rs = statement.executeQuery(sql);
			if (rs.next()) {
				Count_Followers = rs.getInt("followersCount");
				uid = rs.getString("id");
				rs.close();
			} else {
				rs.close();
				System.out.println(("#Error the user is not exist:" + uid));
				return 0;
			}

			TableName = uid + "_followers";
			rs = conn.getMetaData().getTables(null, null, TableName, null);
			if (rs.next()) {
				//Test if the table exists.
				rs.close();
				sql = "drop table " + TableName;
				result = statement.executeUpdate(sql);
				
			} 
			rs.close();
									
			// create the uid_Followers table
			sql = "create table " + TableName
					+ "(tbIndex INT AUTO_INCREMENT PRIMARY KEY, "
					+ "id varchar(15) not NULL, "
					+ " tStamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"; 
			result = statement.executeUpdate(sql);
			
			// Get the Followers info, and
			// 1) write the user_profile table
			// 2) write the uid_followers table
			//int temp = (int)Math.ceil(((float)Count_Followers /(float)each_count));
			
			// Add simple info
			if (MAXCOUNT > Count_Followers)
				MAXCOUNT = Count_Followers;
			
			while (UserAdded_count < MAXCOUNT) {
				
				try {
					Friendships fm = new Friendships();
					//List<User> users = fm.getFollowersById(uid, each_count, UserAdded_count);
					String[] userIDs = fm.getFollowersIdsById(uid, each_count, UserAdded_count);
					API_used_count ++;
					
					for (String user_id : userIDs) {
						// 1) write the user_profile table
						
						//WriteDetailInfobyUser(u.getId(), u);
						WriteSimpleInfo(user_id);

						// 2) write the uid_followers table
						// WriteUserFollowers(String uid, User user, String
						// TableName) {
						WriteUserFollowers(user_id, TableName);
						
						UserAdded_count ++;

					}
					
					Thread.sleep(1000);
					
				} catch (WeiboException e) {
					e.printStackTrace();
				}
				System.out.println(UserAdded_count + " users (followers) have been added to the follower list.");
			}
			
			// Add full info
			int full_each = 200;
			int full_total = 2000;
			int rt;
			
			if (full_total > Count_Followers)
				full_total = Count_Followers;
			
			int rq_count = (int)Math.floor(((float)Count_Followers /(float)each_count));
			if (rq_count < 1 && full_total > 0)
				rq_count = 1;
			
			for (int rq_i = 0; rq_i < rq_count; rq_i++ ){
				
				try {
					Friendships fm = new Friendships();
					List<User> users = fm.getFollowersById(uid, full_each, rq_i * full_each);
					API_used_count ++;
					
					for (User u : users) {
						rt = WriteDetailInfobyUser(u.getId(), u, 1, 0);
						
						if (rt >= 0)
							NewUser_count += rt;
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					
				} 
			}
				
			System.out.println("** " + NewUser_count + " were added with full information.");

		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		return UserAdded_count;
	}

	public void GetFriendsByID(String uid) {
		GetFriendsByID(uid, -1);
		
	}
	
	public int GetFriendsByID(String uid, int tbIndex)
	{
		// Update 1: (1) Only get friends list; (2) Only through obtaining friends full info list.
		
		String sql;
		ResultSet rs;
		int Count_Friends;
		int result;
		int each_count = 5000;
		int MAXCOUNT = 5000;
		int i;
		String TableName;
		int NewUser_count = 0;
		int UserAdded_count = 0;

		try {

			// Get the ## of friends
			if (tbIndex == -1)
				sql = "select * from user_profile where id=\'" + uid + "\'";
			else
				sql = "select * from user_profile where tbIndex=\'" + tbIndex + "\'";
			
			rs = statement.executeQuery(sql);
			if (rs.next()){
				Count_Friends = rs.getInt("friendsCount");
				uid = rs.getString("id");
				rs.close();
				
			} else {
				rs.close();
				System.out.println(("#Error the user is not exist:" + uid));
				return 0;
			}
			
			TableName = uid + "_friends";
			rs = conn.getMetaData().getTables(null, null, TableName, null);
			if (rs.next()) {
				//Test if the table exists.
				rs.close();
				sql = "drop table " + TableName;
				result = statement.executeUpdate(sql);
				
			} 
			rs.close();
									
			// create the uid_friends table
			sql = "create table " + TableName
					+ "(tbIndex INT AUTO_INCREMENT PRIMARY KEY, "
					+ "id varchar(15) not NULL,"
					+ "tStamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
			
			result = statement.executeUpdate(sql);

			// Get the friends info, and
			// 1) write the user_profile table
			// 2) write the uid_followers table
			
			// Add simple info
			/*
			if (MAXCOUNT > Count_Friends)
				MAXCOUNT = Count_Friends;
			
			while (UserAdded_count < MAXCOUNT) {
				try {
					Friendships fm = new Friendships();
					
					String[] userIDs = fm.getFriendsIds(uid, each_count, UserAdded_count);
					API_used_count ++;
					
					for (String user_id : userIDs) {
						// 1) write the user_profile table
						WriteSimpleInfo(user_id);
						
						// 2) write the uid_followers table
						// WriteUserFollowers(String uid, User user, String
						// TableName) {
						WriteUserFollowers(user_id, TableName);

						UserAdded_count ++;
					}
					
					System.out.println(UserAdded_count + " users (friends) have been added.");
					Thread.sleep(1000);
					
				} catch (WeiboException e) {
					e.printStackTrace();
				}
			}
			*/
			
			// Add full info
			int each_fetch = 200;
			int max_fetch = 2000;
			int rt;
			
			if (Count_Friends < max_fetch)
				max_fetch = Count_Friends;
			
			int rq_count = (int)Math.ceil(((float)max_fetch /(float)each_fetch));
						
			for (int rq_i = 0; rq_i < rq_count; rq_i++ ){
							
				try {
					API_used_count ++;	
					Friendships fm = new Friendships();
					List<User> users = fm.getFriendsByID(uid, each_fetch, rq_i * each_fetch);
					
					// batch updates to the user friends table
					Statement st = null;
					conn.setAutoCommit(false);
					st = conn.createStatement();
					
					for (User u : users) {
						//WriteUserFollowers(u.getId(), TableName);
						// Add batch command
						String sql_rq = "insert into " + TableName 
								+ "(id) values(" + u.getId() + ")";
						st.addBatch(sql_rq);
						
						rt = WriteDetailInfobyUser(u.getId(), u, 1, 0);
						UserAdded_count ++;
						
						if (rt >= 0)
							NewUser_count += rt;
					}
					
					//Execute batch command
					int batch_counts[] = st.executeBatch();
					conn.commit();
					System.out.println("... Adding to friends table: Commited " 
							+ batch_counts.length + " updates.");
								
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			System.out.println("** " + UserAdded_count + " users (friends) have been added.");
			System.out.println("** " + NewUser_count +" new users were added with full information.");

		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		return UserAdded_count;
		
		
	}
	
	
	
	public int WriteUserTimeline(Status s, String TableName) {
		
		String uid = s.getId();

		try {
			/*
			String sql = "select * from " + TableName + " where id=\'" + uid + "\'";
			ResultSet rs = statement.executeQuery(sql);
			if (rs.next() == true) // update
			{
				rs.close();
				return 0;

			} else // insert
			{
				rs.close();
				*/
			
			String	sql = "insert into " + TableName 
						+"(id,createdAt,repostsCount,commentsCount,"
						+ "in_reply_to_status_id, in_reply_to_user_id, mid) "
						+ "values(?,?,?,?,?,?,?)";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, s.getId());
				//ps.setString(2, s.getCreatedAt());
				//ps.setDate(2, new java.sql.Date(s.getCreatedAt().getTime()));
				//ps.setTimestamp(2, new java.sql.Timestamp(s.getCreatedAt().getTime()) );
				Object param = new java.sql.Timestamp(s.getCreatedAt().getTime());
				ps.setObject(2, param);

				//java.sql.Timestamp sqltimestamp = new java.sql.Timestamp(s.getCreatedAt().getTime());
				//System.out.println("##sqltimestamp:" + sqltimestamp);
				
				
				//s.getCreatedAt().get
				ps.setInt(3, s.getRepostsCount());
				ps.setInt(4, s.getCommentsCount());
				ps.setLong(5, s.getInReplyToStatusId());
				ps.setLong(6, s.getInReplyToUserId());
				ps.setString(7, s.getMid());
				
				int result = ps.executeUpdate();
				//System.out.println(("Insert Status" + uid));
				if (result <= 0) {
					System.out
							.println(("Insert the Status into the database is wrong" + uid));
					return -1;
				} 
				else {
					return 1;
				}
				/*
			}*/

		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
			
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

	}
	
	
	
	
	
	
	public int GetUserTimeline(int tbIndex){
		String sql;
		ResultSet rs;
		int NewStatuses_count = 0;
		int StatusesAdded_count = 0;
				
		int MAXStatusesCount = 200;
		Integer count = 200;  //鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0銆�
		Paging page= new Paging();  //杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�銆�
		Integer base_app = 0; // 鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆�涓哄惁锛堟墍鏈夋暟鎹級锛�涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓�銆�
		Integer feature = 0; //杩囨护绫诲瀷ID锛�锛氬叏閮ㄣ�1锛氬師鍒涖�2锛氬浘鐗囥�3锛氳棰戙�4锛氶煶涔愶紝榛樿涓�銆�
		int StatusesCount = 0;
		String uid;
		String TableName;
		String ScreenName;
		
		// create the uid_Followers table, name: uid_followers
		try {
			
			// Get the ## of status and uid
			sql = "select * from user_profile where tbIndex=\'" + tbIndex + "\'";
			rs = statement.executeQuery(sql);
			if (!rs.next()){
				rs.close();
				System.out.println("Failed to find user: tbIndex = " + tbIndex);
				return 0;
				
			} else {
				uid = rs.getString("id");
				StatusesCount = rs.getInt("statusesCount");
				ScreenName = rs.getString("screenName");
				rs.close();
				
			}
			
			TableName = uid + "_Statuses";
			rs = conn.getMetaData().getTables(null, null, TableName, null);
			if (rs.next()) {
				sql = "drop table " + TableName;
				statement.executeUpdate(sql);
				// exist
				
			} 
			rs.close();
			
			// create the uid_Followers table anyway
			sql = "create table " + TableName
						+ "(tbIndex INT AUTO_INCREMENT PRIMARY KEY," +
						"id varchar(50) not NULL," + // status id 1
						"createdAt datetime," + // status鍒涘缓鏃堕棿2
						"repostsCount INTEGER," + // 杞彂鏁�
						"commentsCount INTEGER," + // 璇勮鏁�
						"in_reply_to_status_id long," +
						"in_reply_to_user_id long," +
						"mid varchar(50))"; // 寰崥MID5
				
			statement.executeUpdate(sql);
			

			try {
				if (StatusesCount == 0)
					return 0;
				
				Timeline tm = new Timeline();
				
				if (MAXStatusesCount > StatusesCount)
					MAXStatusesCount = StatusesCount;
				
				int maxLOOP = 1;
				int i_loop = 0;
				int flag = 1;
				while (StatusesAdded_count < MAXStatusesCount && i_loop < maxLOOP)
				{
					API_used_count ++;
					List<Status> status = tm.getUserTimeline(uid, ScreenName,
							count, page, base_app,feature);
					
					// Batch update
					//conn.setAutoCommit(false);
					PreparedStatement ps = null;
					
					conn.setAutoCommit(false);
					sql = "insert into " + TableName 
							+"(id,createdAt,repostsCount,commentsCount,"
							+ "in_reply_to_status_id, in_reply_to_user_id, mid) "
							+ "values(?,?,?,?,?,?,?)";
					
					ps = conn.prepareStatement(sql);
										
					// Batch end
					for(Status st : status){
						//rt = WriteUserTimeline(st, TableName);
						ps.setString(1, st.getId());
						Object param = new java.sql.Timestamp(st.getCreatedAt().getTime());
						ps.setObject(2, param);
						ps.setInt(3, st.getRepostsCount());
						ps.setInt(4, st.getCommentsCount());
						ps.setLong(5, st.getInReplyToStatusId());
						ps.setLong(6, st.getInReplyToUserId());
						ps.setString(7, st.getMid());
						ps.addBatch();
						
						//if (rt >= 0)
						//	NewStatuses_count += rt;
						
						StatusesAdded_count ++;
					}
					int [] updateCounts = ps.executeBatch();
					conn.commit();
					
					System.out.println("... Adding to status table: Committed " 
								+ updateCounts.length + " updates.");
					i_loop ++;
					
					if (flag == 1 && updateCounts.length < 1){
						//Fetching fail, try one more time
						i_loop = 0;
						flag = 0;
						
						System.out.println("!!!! Fetching user statuses failed. Wait for 5 sec before trying one more time.");
						Thread.sleep(5000);
					}
			
					//System.out.println(StatusesAdded_count +" have been added.");
				}
					
				System.out.println(StatusesAdded_count + " statuses were retrieved from the server.");
				
			} catch (WeiboException e) {
				e.printStackTrace();
				return -1;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
			
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
			
		}
		
		return StatusesAdded_count;
	}
	
	public void GetSecondLayerInfo(String uid){
		String sql;
		ResultSet rs;
		String TableName=null;
		String id_temp;
		int i;
		ArrayList<String> Followers_List = new ArrayList<String>();
		ArrayList<String> Friends_List = new ArrayList<String>();
		//String TableName = uid + "_statuses";
		try {
			//Read the First Layer Followers
			TableName = uid + "_followers";
			sql = "select * from " + TableName;
			
			System.out.println( (sql + "GetSecondLayerInfo="+uid) );  
			
			rs = statement.executeQuery(sql);
			while(rs.next()) {
				Followers_List.add(rs.getString("id"));
				//id_temp = rs.getString("id");
				//GetFollowersByID(id_temp);
				//GetFriendsByID(id_temp);
			}
			rs.close();
			for(i=0;i<Followers_List.size();i++)
			{
				id_temp = Followers_List.get(i);
				GetFollowersByID(id_temp);
				GetFriendsByID(id_temp);
			}
			
			
			
			TableName = uid + "_friends";
			sql = "select * from " + TableName;
			System.out.println( (sql + "GetSecondLayerInfo="+uid) );  
			
			rs = statement.executeQuery(sql);
			while(rs.next()) { 
				Friends_List.add(rs.getString("id"));
				//id_temp = rs.getString("id");
				//GetFollowersByID(id_temp);
				//GetFriendsByID(id_temp);
			}
			rs.close();
			
			for(i=0;i<Friends_List.size();i++)
			{
				id_temp = Friends_List.get(i);
				GetFollowersByID(id_temp);
				GetFriendsByID(id_temp);
			}
			
			
			
			
		}catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	
	public void GetAllStatuses(){
		String sql;
		ResultSet rs;
		String id_temp;
		String screen_name;
		Integer statusesCount;
		ArrayList<String> ID_List = new ArrayList<String>();
		ArrayList<String> Screen_Name_List = new ArrayList<String>();
		ArrayList<Integer> StatusesCount_List = new ArrayList<Integer>();
		
		try {
			
			sql = "select * from user_profile";
			System.out.println( (sql + "GetAllStatuses") );  
			
			rs = statement.executeQuery(sql);
			while(rs.next()) {  
				//id_temp = rs.getString("id");
				//screen_name = rs.getString("screenName");
				//statusesCount = rs.getInt("statusesCount");
				ID_List.add(rs.getString("id"));
				Screen_Name_List.add(rs.getString("screenName"));
				StatusesCount_List.add(rs.getInt("statusesCount"));
				
				//GetUserTimeline(String uid, String screen_name, int statusesCount){
				//GetUserTimeline(id_temp,  screen_name, statusesCount);
			}
			rs.close();
			
			
			for(int i=0; i<ID_List.size(); i++)
			{
				id_temp = ID_List.get(i);
				screen_name = Screen_Name_List.get(i);
				statusesCount = StatusesCount_List.get(i);
				//GetUserTimeline(id_temp,  screen_name, statusesCount);
			}
			
			
			
		}catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	public int GetUserFullInfo(int tbIndex)
	{
		// Update 1: No need access to the server. Only user with full information will be further fetched.
		String sql_rq = "select * from user_profile where tbIndex = " + tbIndex;
		ResultSet rs;
		String uid;
		
		try {
			rs = statement.executeQuery(sql_rq);
			
			if (rs.next()){
				uid = rs.getString("id");
				rs.close();
				
			} else {
				System.out.println("Failed to find user, tbIndex = " + tbIndex);
				rs.close();
				return -1;
			}
				
			// Access the Sina server for full user profile.
			Users um = new Users();
			User user_info = um.showUserById(uid);
			API_used_count ++;
			
			WriteDetailInfobyUser(uid, user_info, 1, 1);
			
		} catch (SQLException e) {
			e.printStackTrace();
			return -1; 
			
		} catch (Exception e) {
			e.printStackTrace();
			
			StackTraceElement[] eSts = e.getStackTrace();
			for (StackTraceElement eSt: eSts){
				
				System.out.println(eSt);
				
			}
			
			return -1;
			
		}
		return 0;
	}
	
	public void UpdateUserStatus(int ptr)
	{
		String sql_rq;
		int result;
		
		try {
			sql_rq = "update Proc_status SET user_profile_ptr = " + (ptr + 1);
			PreparedStatement ps = conn.prepareStatement(sql_rq);
			result = ps.executeUpdate();
			
			sql_rq = "update user_profile SET NewUser = 0 where tbIndex = " + ptr;
			ps = conn.prepareStatement(sql_rq);
			result = ps.executeUpdate();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void UpdateUserProfile(int ptr, int newFollowers, int newFriends, int newStatuses)
	{
		String sql;
		
		sql = "update user_profile set "
				+ "NewUser=?, followersAdded=?, friendsAdded=?, statusesAdded=? "
				+ "where tbIndex = " + ptr;
		
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, 0);
			ps.setInt(2, newFollowers);
			ps.setInt(3, newFriends);
			ps.setInt(4, newStatuses);
			
			int result = ps.executeUpdate();
			if (result <= 0) 
				System.out.println("Last Step: Failed to update user profile with additional information: (tbIndex)" + ptr);
			
		}catch (SQLException e) {
			e.printStackTrace();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}
	
	public int CheckNewUser(int tbIndex)
	{
		// Update 1: To find this user: (1) new user,and (2) with full information, i.e., screen name and following > 0 ppl.
		String sql_rq = "select screenName, friendsCount, statusesCount, NewUser from user_profile where tbIndex = " + tbIndex;
		ResultSet rs;
				
		try {
			rs = statement.executeQuery(sql_rq);
			
			if (rs.next()) {
				String screenName = rs.getString("screenName");
				int NewUser = rs.getInt("NewUser");
				int statusesCount = rs.getInt("statusesCount");
				int friendsCount = rs.getInt("friendsCount");
				
				if (screenName != null && NewUser == 1 && statusesCount > 200 && friendsCount > 0)
					return 1;
				else {
					return 0;
				}
				
			} else {
				return -1;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
			
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
			
		}
		
	}
	
	public void crawlUser(int ptr, int count) 
	{
		int succUpdate_count = 0;
		long start_time = System.currentTimeMillis();
		SimpleDateFormat t_now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		//Crawl the detail data of users, starting from User = ptr, with ## = count users.
		// Step1: Get the user information from user_profile
		// Step2: Generate tables uid_followers, uid_friends, and uid_statuses
		// Step3: In table user_profile, set status 'NewUser' = 0, and update the pointer
		//  	user_profile_ptr in table proc_status
		// Step4: Repeat.
		
		int Crawl_count = 0;
		
		while (Crawl_count < count)
		{
			
			long elapsed_time;
						
			while (CheckNewUser(ptr) < 1)
				ptr ++;
			
			System.out.println("\n########\n@ User (tbIndex) " + ptr 
					+ " is being fetched... (" 
					+ t_now.format(Calendar.getInstance().getTime()) + ")");
			
			
			int newFollowers = 0;
			int newFriends = 0;
			int newStatuses = 0;
			
			/* Update 1: Only user with full information will be further fetched.
			int userInfoStatus = GetUserFullInfo(ptr);			// (**API used: 1 - 1 = 0: updated: only the user with full info would be fetched!!
			if (userInfoStatus >= 0) {
				// User does exist on the server.
				newFollowers = GetFollowersByID(null, ptr);		// API used: at most 6
				newFriends = GetFriendsByID(null, ptr);			// API used: at most 6
				newStatuses = GetUserTimeline(ptr);				// API used: 1
			}
			*/
			//newFriends = GetFriendsByID(null, ptr);
			newStatuses = GetUserTimeline(ptr);

			if (newFriends >= 0 && newStatuses > 0) {
				// Successfully updated a user
				succUpdate_count ++;
				
				UpdateUserStatus(ptr); 
				UpdateUserProfile(ptr, newFollowers, newFriends, newStatuses);
			} 
			else {
				System.out.println("Error occurs when updating user (tbIndex) " + ptr);
			}
			
			ptr ++;
			Crawl_count ++;
			
			System.out.println("****** API used for " + API_used_count + " times.");
			
			long tot_elapsed_time = System.currentTimeMillis() - start_time;
			long wait_time = API_used_count * 25 * 1000 - tot_elapsed_time;
			if (wait_time < 0) {
				wait_time = 0;
			}
			else {
				System.out.println("****** Pausing for " + wait_time/1000F + "seconds.");
				
				try {
					Thread.sleep(wait_time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			
		}
		
		System.out.println("**** Finished. Tried: " + Crawl_count + ". Succeeded: " + succUpdate_count);
	}
	
	public void StatusAnalyzer(int st, int ed)
	{
		String sql;
		String uid;
		int statusesCount;
		ResultSet rs = null;
		
		
		try {
			sql = "create table if not exists StatusAnalyzer("
					+ "tbIndex int primary key,"
					+ "id varchar(15) not null,"
					+ "statusesCount int," 
					+ "sumRepostsCount int,"
					+ "sumCommentsCount int,"
					+ "statusesRecorded int"
					+ ")";
			
			statement.execute(sql);
			
			int ptr = st;
			while (ptr <= ed)
			{
				sql = "select id,statusesCount, statusesAdded from user_profile "
						+ "where tbIndex = " + ptr;
				
				rs = statement.executeQuery(sql);
				if (rs.next()) {
					uid = rs.getString("id");
					statusesCount = rs.getInt("statusesCount");
					rs.close();
					
					// Test if the table does exist
					ResultSet rs1;
					String stTable = uid + "_statuses";
					sql = "show tables like \'" + stTable + "\'";
					rs1 = statement.executeQuery(sql);
					if (!rs1.next()) {
						System.out.println("!! No results for user (tbIndex) " + ptr);
						ptr ++;
						continue;
						
					}
					
					// Get statistics of statuses
					ResultSet rs2;
					int sumRepostsCount = 0;
					int sumCommentsCount = 0;
					int statusesRecorded = 0;
					int daysCount = 0;
					java.util.Date latestDate = new Date();
					java.util.Date oldestDate = new Date();
					
					sql = "select count(*) from " + stTable;
					rs1 = statement.executeQuery(sql);
					
					if (rs1.next())
						statusesRecorded = rs1.getInt("count(*)");
					rs1.close();
					
					if (statusesRecorded < 1) {
						System.out.println("!! No results for user (tbIndex) " + ptr);
						ptr ++;
						continue;
					}
						
					sql = "select sum(repostsCount) from " + stTable;
					rs2 = statement.executeQuery(sql);
					
					if (rs2.next()) 
						sumRepostsCount = rs2.getInt("sum(repostsCount)");
					rs2.close();
					
					sql = "select sum(commentsCount) from " + stTable;
					rs2 = statement.executeQuery(sql);
					
					if (rs2.next())
						sumCommentsCount = rs2.getInt("sum(commentsCount)");
					rs2.close();
					
					// ** days count
					sql = "select max(createdAt) from " + stTable;
					rs2 = statement.executeQuery(sql);
					
					if (rs2.next())
						latestDate = rs2.getDate("max(createdAt)");
					rs2.close();
					
					sql = "select min(createdAt) from " + stTable;
					rs2 = statement.executeQuery(sql);
					
					if (rs2.next())
						oldestDate = rs2.getDate("min(createdAt)");
					
					if (latestDate == null || oldestDate == null) {
						System.out.println("!! No results for user (tbIndex) " + ptr);
						ptr ++;
						continue;
					}
					
					long ldaysCount = (latestDate.getTime() - oldestDate.getTime()) / (24 * 3600 * 1000);
					String sdaysCount = "" + ldaysCount;
					daysCount = Integer.parseInt(sdaysCount);
					
					// Write to the summary table
					ResultSet rs3;
					sql = "select tbIndex from StatusAnalyzer where tbIndex = " + ptr;
					rs3 = statement.executeQuery(sql);
					if (rs3.next()) {
						// update
						sql = "update StatusAnalyzer set "
								+ "id = ?, statusesCount = ?, "
								+ "sumRepostsCount = ?, sumCommentsCount = ?,"
								+ "statusesRecorded = ?, daysCount = ?"
								+ " where tbIndex = " + ptr;
						
						PreparedStatement ps = conn.prepareStatement(sql);
						ps.setString(1,uid);
						ps.setInt(2, statusesCount);
						ps.setInt(3, sumRepostsCount);
						ps.setInt(4, sumCommentsCount);
						ps.setInt(5, statusesRecorded);
						ps.setInt(6, daysCount);
						int result = ps.executeUpdate();
						
						if (result > 0)
							System.out.println("** Succeeded in processing user (tbIndex) " + ptr);
					}
					else {
						// insert
						sql = "insert into StatusAnalyzer(tbIndex, id, statusesCount," +
								"sumRepostsCount, sumCommentsCount, statusesRecorded, daysCount) values(?,?,?,?,?,?,?) ";
						
						PreparedStatement ps = conn.prepareStatement(sql);
						ps.setInt(1,ptr);
						ps.setString(2,uid);
						ps.setInt(3, statusesCount);
						ps.setInt(4, sumRepostsCount);
						ps.setInt(5, sumCommentsCount);
						ps.setInt(6, statusesRecorded);
						ps.setInt(7, daysCount);
						int result = ps.executeUpdate();
						
						if (result > 0)
							System.out.println("** Succeeded in processing user (tbIndex) " + ptr);
					}
					
					
				}
				else {
					
				}
					
				ptr ++;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
	
	public static void main(String[] args) {
		
		//Configuration
		String access_token = "2.00kyD9DC6CfyWD833349b662tbPruC";
		String Entrance_user_id = "1883283154";
		
		//Initialization
		WeiboAnalysis weibo_scrapper = new WeiboAnalysis();
		weibo_scrapper.InitialMysql();
		
		weibo_scrapper.CreateTableUserProfile();
		weibo_scrapper.CreateTable_ProcStatus();
		
		weibo_scrapper.InitialWeibo(access_token);
		
		//Crawl data: step 1. start the process from the 'Entrance_user' (the 'root user' mentioned in the paper)
		weibo_scrapper.WriteDetailInfobyUserID(Entrance_user_id);
		//Crawl data: Step 2. This function recursively collect users profiles starting from the 'start_user_tbIndex' user.
		int start_user_tbIndex = 1;
		int users_count = 10000;
		weibo_scrapper.crawlUser(start_user_tbIndex, users_count);
		
		//A simple data processing 
		//weibo_scrapper.StatusAnalyzer(1,13187);
		
		
		
		
		
	}

}
