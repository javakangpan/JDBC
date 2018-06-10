package jdbCommon;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DbBase {
	public static void baseUpdate(String sql,Object...params) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DbUtils.getConnection();
			ps = conn.prepareStatement(sql);
			if(null != params) {
				for(int i = 0; i < params.length; i++) {
					ps.setObject(i+1, params[i]);
				}			
			}
			DbUtils.execUpdate(ps);
		} catch (SQLException e) {
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally {
			DbUtils.close(conn, ps);
		}
	}
	
	public static<T> T queryForSingle(String sql,Class<T> cls,Object...args) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		T obj = null;
		
		try {
			conn = DbUtils.getConnection();
			ps = conn.prepareStatement(sql);	
			if(null != args) {
				for(int i = 0; i< args.length; i++) {
					ps.setObject(i+1, args[i]);
				}
			}
			rs = ps.executeQuery();		
			ResultSetMetaData  md = rs.getMetaData();		
			int columnCount = md.getColumnCount();
			if(rs.next()) {			
				obj = cls.newInstance();
				
				for(int i = 1; i < columnCount; i++) {
					Object value = rs.getObject(i);
					String columnName = md.getColumnName(i).toLowerCase();
					if(null == value) {
						continue;
					}
					if(hasFieled(cls, columnName)) {
						Field f = cls.getDeclaredField(columnName);
						f.setAccessible(true);
						
						if(value  instanceof BigDecimal ) {
							BigDecimal val = (BigDecimal) value;
							if("int".equals(f.getType().getName())) {
								f.set(obj, val.intValue());
							}else {
								f.set(obj,val.doubleValue());
							}
						}else if (value instanceof java.sql.Timestamp) {
							java.sql.Timestamp time = (Timestamp) value;
							java.sql.Date date = new java.sql.Date(time.getTime());
							f.set(obj, date);
						}else {
							f.set(obj,value);
						}
					}
				}
			}
	
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}finally {
			DbUtils.close(conn, ps, rs);
		}
		return obj;	
	}
	private static <T> boolean hasFieled(Class<T> cls, String columnName) {
		Field[] f = cls.getDeclaredFields();
		for(int i = 0; i < f.length; i++) {
			if(columnName.equals(f[i].getName())) {
				return true;
			}
		}
		return false;
	}
	
	public static int queryForCount(String sql, Object...args) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DbUtils.getConnection();
			ps = conn.prepareStatement(sql);
			if(null != args) {
				for(int i = 0; i < args.length; i++) {
					ps.setObject(i+1, args[i]);
				}
			}
			return DbUtils.execQueryCount(ps);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			DbUtils.close(conn, ps, rs);
		}
		return -1;
	}

	public static<T> List<T> baseQuery(String sql, Class<T> cls, Object...args) throws SQLException {
		List<T> list = new ArrayList<>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData md = null;
		T o = null;
		Field f = null;
		try {
			conn = DbUtils.getConnection();
			ps = conn.prepareStatement(sql);
			if(null != args) {
				for(int i = 0; i< args.length; i++) {
					ps.setObject(i+1, args[i]);
				}
			}
			rs = ps.executeQuery();
			md = rs.getMetaData();
			while(rs.next()) {
				o = cls.newInstance();
				for(int i = 1; i <= md.getColumnCount(); i++) {
					String columnName = md.getColumnName(i).toLowerCase();
					Object v = rs.getObject(i);
					if(null == v) {
						continue;
					}
					if(hasFieled(cls, columnName)) {
						try {
							f = cls.getDeclaredField(columnName);
						} catch (NoSuchFieldException e) {

							e.printStackTrace();
						} catch (SecurityException e) {

							e.printStackTrace();
						}
						f.setAccessible(true);
						if(v instanceof BigDecimal) {
							BigDecimal val = (BigDecimal) v;
							if("int".equals(f.getType().getName())) {
								f.set(o, val.intValue());
							}else if("double".equals(f.getType().getName())) {
								f.set(0, val.doubleValue());
							}
						}else {
							f.set(o, v);
						}
					}
				}
				list.add(o);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		}finally {
			DbUtils.close(conn, ps, rs);
		}
		return list;
		
	}
	

}
