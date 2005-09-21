<%@ page import="java.util.Iterator"%>
<%@ page import="org.apache.axis2.Constants"%>
<%@ page import="org.apache.axis2.context.ConfigurationContext"%>
<%@ page import="org.apache.axis2.context.ServiceGroupContext"%>
<%@ page import="org.apache.axis2.context.ServiceContext"%>
<%@ page import="java.util.HashMap"%>
<%--
  Created by IntelliJ IDEA.
  User: Indika Deepal
  Date: Sep 20, 2005
  Time: 9:16:56 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>View All the Run time context hierachy</title></head>
<body>
<h1>Runing Context hierachy</h1>
<%
    ConfigurationContext configContext = (ConfigurationContext)request.getSession().getAttribute(
            Constants.CONFIG_CONTEXT);

    HashMap serviceGroupContextsMap = configContext.getServiceGroupContexts();
    Iterator serviceGroupContext = serviceGroupContextsMap.keySet().iterator();
    if(serviceGroupContextsMap.size() >0){
    %>
     <ul>
    <%
    while (serviceGroupContext.hasNext()) {
        String groupContextID = (String)serviceGroupContext.next();
        ServiceGroupContext groupContext = (ServiceGroupContext)serviceGroupContextsMap.get(groupContextID);
        %>
           <li><%=groupContextID%><font color="blue"><a href="viewServiceGroupContext.jsp?V=Y&ID=<%=groupContextID%>">
                    View</a></font>  <font color="red"><a href="viewServiceGroupContext.jsp?R=Y&ID=<%=groupContextID%>">
                    Remove</a> </font></li>
        <%
        Iterator serviceContextItr = groupContext.getServiceContexts();
            %><ul><%
        while (serviceContextItr.hasNext()) {
            ServiceContext serviceContext = (ServiceContext)serviceContextItr.next();
             String serviceConID = serviceContext.getServiceInstanceID();
        %>
            <li><%=serviceConID%><font color="blue"><a href="viewServiceContext.jsp?V=y&ID=<%=serviceConID%>&PID=<%=groupContextID%>">
                    View</a></font>  <font color="red"><a href="viewServiceContext.jsp?R=Y&ID=<%=serviceConID%>&PID=<%=groupContextID%>">
                    Reomove</a></font></li>
        <%
        }
                %></ul><hr><%
    }
    %>  </ul>
        <%
            }
%>
</body>
</html>