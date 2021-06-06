<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Business Logic</title>
    <script src="webjars/jquery/3.6.0/jquery.min.js"></script>
    <script>
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

        function onSubmit(){
            $('#pass').remove();
            $('#fail').remove();
            $('#result').remove();
            // $("input[name=pass]").remove();
        }

    </script>
</head>
<body>
<input type="hidden" id="clear" value="false"/>
    <div>
        <form action="/commit.do" method="post" onsubmit="onSubmit()">
            <input type="hidden" name="chk" value="a"/>
            <input type="submit" value="Commit A"/>
        </form>
    </div>
    <div>
        <form action="/commit.do" method="post" onsubmit="onSubmit()">
            <input type="hidden" name="chk" value="b"/>
            <input type="submit" value="Commit B"/>
        </form>
    </div>
    <div>
        <p class="change-greeting"></p>
        <br/>
    </div>
<div id="result">
<%
    List<String> datalist = (List<String>) request.getAttribute("data");

    if(datalist != null)
    for(String s : datalist){
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

    <h3>PassFiles</h3>
<div id = "pass">
    <%
        List<String> passlist = (List<String>) request.getAttribute("pass");
        if(passlist != null)
            for(String s : passlist){
    %>
        <p style="font-size: 15px"><%=s%>
            <%
    }
%>
</div>
    <br/>

        <h3>FailFiles</h3>
<div id="fail">
        <%
            List<String> faillist = (List<String>) request.getAttribute("fail");
            if(faillist != null)
                for(String s : faillist){
        %>
        <p style="font-size: 15px"><%=s%>
                <%
    }
%>
    </div>

</body>
</html>
