<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%--

  Created by IntelliJ IDEA.
  User: codemind
  Date: 2021-06-03
  Time: 오전 11:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--<%--%>
<%--    List<String> log = new ArrayList<>();--%>
<%--    try {--%>
<%--        log = (List<String>) request.getAttribute("data");--%>
<%--        if (log == null)--%>
<%--            log.add("");--%>
<%--    } catch(Exception e){--%>
<%--        log.add("");--%>
<%--    }--%>
<%--%>--%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Title</title>
</head>
<body>
    <div>
        <form action="/commit.do" method="post">
            <input type="text" name="username" placeholder="아이디" value=""/>
            <input type="password" name="password" placeholder="비밀번호" value=""/>
            <input type="text" name="project" placeholder="프로젝트이름" value=""/>
            <input type="text" name="filepath" placeholder="파일경로" value=""/>
            <input type="submit" value="전송"/>
        </form>
    </div>
    <div>
        <form action="/result.do" method="get">

            <input type="submit" value="전송"/>
        </form>
    </div>


<%
    List<String> log = (List<String>) request.getAttribute("data");
    if(log != null)
    for(String s : log){
%>
       <p><%=s%>
<%
    }
%>

</body>
</html>
