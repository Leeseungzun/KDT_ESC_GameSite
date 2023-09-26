package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import dto.SkeletonSurvivorDTO;

public class SkeletonSurvivorDAO {
	private SkeletonSurvivorDAO() {}
	private static SkeletonSurvivorDAO instance;

	public static SkeletonSurvivorDAO getInstance() {
		if (instance == null) {
			instance = new SkeletonSurvivorDAO();
		}
		return instance;
	}

	private Connection getConnection() throws Exception {
		Context ctx = new InitialContext();
		DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/mysql");
		return ds.getConnection();
	}
	
	public int writeGameResult(String player, int score) throws Exception {
		String sql = "insert into skeletonsurvivor value(null,?,?,default)";
		try(Connection con = this.getConnection(); PreparedStatement pstat = con.prepareStatement(sql)){
			pstat.setString(1,player);
			pstat.setInt(2,score);
			return pstat.executeUpdate();
		}
	}
	
	public int deleteAll() throws Exception {
		String sql = "delete from skeletonsurvivor";
		try (Connection con = this.getConnection(); PreparedStatement pstat = con.prepareStatement(sql)){
			return pstat.executeUpdate();
		}
	}
	
	public void insertTop10Rank(List<SkeletonSurvivorDTO> top10List) throws Exception {
	    String insertSQL = "INSERT INTO skeletonsurvivorhistory (seq, player, score, played_time) VALUES (null, ?, ?, ?)";
	    
	    try (Connection con = this.getConnection(); 
	         PreparedStatement pstmt = con.prepareStatement(insertSQL)) {
	        
	        for (SkeletonSurvivorDTO dto : top10List) {
	            pstmt.setString(1, dto.getPlayer());
	            pstmt.setInt(2, dto.getScore());
	            pstmt.setTimestamp(3, dto.getPlayed_time());
	            
	            pstmt.executeUpdate(); 
	        }
	    }
	}
	
	
	public List<SkeletonSurvivorDTO> top10Rank() throws Exception{
		String sql = "SELECT ROW_NUMBER() OVER (ORDER BY score DESC) AS row_num, player, score, played_time "
				+ "FROM skeletonsurvivor "
				+ "GROUP BY player, score, played_time "
				+ "ORDER BY score DESC LIMIT 10";
		try (Connection con = this.getConnection(); PreparedStatement pstat = con.prepareStatement(sql)) {
			try (ResultSet rs = pstat.executeQuery()){
				List<SkeletonSurvivorDTO> list = new ArrayList<>();
				while(rs.next()) {
					int rownum = rs.getInt(1);
					String player = rs.getString(2);
					int score = rs.getInt(3);
					Timestamp played_time = rs.getTimestamp(4);
					list.add(new SkeletonSurvivorDTO(rownum,player,score,played_time)); 
				}
				return list;
			}
		}
	}
}