package us.festivaltime.gametime.server;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by jbk on 8/31/14.
 */
public class TestJndiServlet extends HttpServlet implements Servlet {
    private static final long serialVersionUID = 1L;

    public TestJndiServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        performTask(request, response);
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        performTask(request, response);
    }

    private void performTask(HttpServletRequest request,
                             HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("TestJndiServlet says hi");
        out.println("<br/>");
        out.println(testJndiDataSource());
    }

    public String testJndiDataSource() {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer();
        try {
            InitialContext ctx = new InitialContext();
     //       DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/festival_master");

            // This works too
             Context envCtx = (Context) ctx.lookup("java:comp/env");
             DataSource ds = (DataSource) envCtx.lookup("jdbc/festival_master");

            conn = ds.getConnection();

            st = conn.createStatement();
            rs = st.executeQuery("SELECT * FROM festivals");
/*
            while (rs.next()) {
                String id = rs.getString("id");
                String firstName = rs.getString("username");
                String lastName = rs.getString("year");
                sb.append("ID: " + id + ", username: " + firstName
                        + ", email: " + lastName + "<br/>");
            }
            */
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (st != null) st.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return sb.toString();
    }

}
