<%@ page import="us.festivaltime.gametime.server.GameTime" %>
<%--
  Created by IntelliJ IDEA.
  User: jbk
  Date: 8/30/14
  Time: 12:58 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/javascript;charset=UTF-8" language="java" %>
<%--
<link rel="stylesheet" type="text/css" href="styles.css"/>
<html>
<head>
    <title></title>
</head>
<body>
<h3 class="message"><%=GameTime.getMessage()%></h3>
</body>
</html>
--%>
<%=GameTime.parseRequest( request )%>
