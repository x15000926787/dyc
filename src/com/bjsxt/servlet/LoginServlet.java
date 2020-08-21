package com.bjsxt.servlet;

import com.dl.impl.UserDAOImpl;
import com.dl.tool.DBConnection;
import com.dl.tool.Tool;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet   {
	@Autowired
	private UserDAOImpl user;
	private static final long serialVersionUID = 1L;
	public static final String CONTENT_TYPE="text/html;charset=utf-8";
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(LoginServlet.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		
		HttpSession session=request.getSession();
		response.setContentType(CONTENT_TYPE);
		request.setCharacterEncoding("utf-8");
		//System.out.println("LoginServlet");
		DBConnection dbcon=new DBConnection();
		PreparedStatement pstmt=null;
		ResultSet rs;
		String jsonString="{\"result\":0}";
		int t=0;
		//PrintWriter out = response.getWriter();
		String username= request.getParameter("username");
		String password= request.getParameter("password");
		//String rand=request.getParameter("rand");
		//String imagerand=session.getAttribute("Rand").toString();
		//System.out.println(username);
		//System.out.println(password);
		//System.out.println(rand);
		//System.out.println(imagerand);
		String sql="select a.name,a.passwd,a.id,a.gkey,a.leval,a.phone,a.email,b.domain,a.auth from msg_user a,prtu b where a.gkey=b.un_x and  a.name=? and a.passwd=?";
		//logger.warn(sql);
		if (username.matches("admin")) {
			sql = "select name,passwd,id,gkey,leval,phone,email,0 domain,auth from msg_user  where   name=? and passwd=?";

		}
			session.setAttribute("isTan", request.getParameter("isTan"));
		//if(rand.equalsIgnoreCase(imagerand)){
			try{
				pstmt=dbcon.setPreparedStatement(sql);
				pstmt.setString(1,username);
				pstmt.setString(2,password);
				rs=dbcon.getPrepatedResultSet();
				if(rs.next()){
					session.setAttribute("username", username);
					session.setAttribute("groupKey", rs.getString("gkey"));
					session.setAttribute("uleval", rs.getString("leval"));
					session.setAttribute("LOGIN_SUCCESS", rs.getString("id"));
					session.setAttribute("phone", rs.getString("phone"));
					session.setAttribute("email", rs.getString("email"));
					session.setAttribute("domain", rs.getString("domain"));
					session.setAttribute("auth", rs.getString("auth"));
					try {
						dbcon.setPreparedStatement("insert into loginred (uname,evt) values ('"+rs.getString("id")+"',1)");

						t=dbcon.getexecuteUpdate();
					} catch (Exception e) {
						logger.warn(e.toString());
						// TODO: handle exception
					}

					try {

						jsonString="{\"result\":1}";
					}catch (Exception e)
					{
						logger.warn(e.toString());
					}


				}else{
					//response.sendRedirect("login.html");
				}
				//System.out.println(jsonString);
				Tool.request( jsonString,response);
             pstmt.close();
             dbcon.getClose();
			}catch(SQLException e){
				System.out.println("出错了"+e.toString());
			}
		//}else{
		//	out.println("<script lanager=\"javascript\">javascript:alert(\"附加码错误，请重新输入\");window.location='login.html'</script>");
		//}
		
		
		
	}

}
