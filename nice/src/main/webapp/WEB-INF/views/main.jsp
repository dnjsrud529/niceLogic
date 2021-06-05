<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Title</title>
    <script src="webjars/jquery/3.6.0/jquery.min.js"></script>
    <script>
        // $.ajax({
        //     url:"/data.do",
        //     dataType:"json",
        //     success:function(data){
        //         console.log(data);
        //     }
        // });
        $(function() {

            timer = setInterval( function () {
                $.ajax ({
                    url : "/data.do",
                    cache : false,
                    success : function (data) {
                        console.log(data);
                        $('.change-greeting').text(data);
                    }

                });

            }, 3000);

        });

    </script>
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
        <br/>
        <p class="change-greeting"></p>
        <br/>
    </div>
<div>
<%
    List<String> log = (List<String>) request.getAttribute("data");
    if(log != null)
    for(String s : log){
        if(s.contains("commit")){
%>
    <h1><%=s%></h1>
<%
        }else{
%>
    <h4><%=s%></h4>
<%
            }
    }
%>
</div>
    <br/>
    <br/>
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
    <br/><br/>
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
