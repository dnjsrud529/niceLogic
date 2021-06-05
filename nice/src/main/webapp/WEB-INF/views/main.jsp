<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>

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
                <input type="hidden" name="chk" value="a"/>
            <input type="submit" value="Commit A"/>
        </form>
    </div>
    <div>
        <form action="/commit.do" method="post">
            <input type="hidden" name="chk" value="b"/>
            <input type="submit" id ="btn" value="Commit B"/>
        </form>


    </div>
<div>
<%
    List<String> log = (List<String>) request.getAttribute("data");
    if(log != null)
    for(String s : log){
        if(s.contains("commit")){
%>
    <h2><%=s%></h2>
<%
        }else{
%>
    <h4><%=s%></h4>
<%
            }
    }
%>
</div>
<div>
    <h3>PassFiles</h3>
    <%
        List<String> pass = (List<String>) request.getAttribute("pass");
        if(pass != null)
            for(String s : pass){
    %>
    <p style="font-size: 15px"><%=s%>
            <%
    }
%>
</div>

    <div>
        <h3>FailFiles</h3>
        <%
            List<String> fail = (List<String>) request.getAttribute("fail");
            if(fail != null)
                for(String s : fail){
        %>
        <p style="font-size: 15px"><%=s%>
                <%
    }
%>
    </div>

</body>
</html>
