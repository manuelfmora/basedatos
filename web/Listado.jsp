<%-- 
    Document   : Todos
    Created on : 12-jun-2016, 20:09:38
    Author     : Manuel Mora
--%>
<%! int inicio = 0;
    int numPaginas = 0;%>
<%
    if (request.getAttribute("inicio") != null) {
        inicio = (Integer) request.getAttribute("inicio");
    }

    int numPaginas = (Integer) request.getAttribute("numPaginas");
    int pagActual = inicio / 20;
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Listado Usuarios</title>
        <link rel="stylesheet" type="text/css" href="assets/style.css" media="screen" />
       
    </head>
    <body>
        <%@ include file="index.jsp"%><%--Incluimos menú --%>
    <center>
        <hr noshade>
        <h1><u>Listado de usuarios</u></h1>
        <h2>Número de usuarios: <% out.println(request.getAttribute("numCentros")); %></h2>

        <%
            if (request.getAttribute("tabla") != null) {
                out.println(request.getAttribute("tabla"));
            }
        %>


        <%-- PAGINADOR --%>                
        <p>      
            <% if (inicio != pagActual) {%>
                <a href="Listado?inicio=0" class="paginas" title="Inicio"><<</a>
                <a href="Listado?inicio=<%=(inicio - 20)%>" class="paginas" title="Anterior"><</a>
            <% } %>

            <% for (int i = 0; i < numPaginas; i++) {
                if ((i - 2) <= pagActual && (pagActual <= i + 2) && i != pagActual) {%> 
                        <a href="Listado?inicio=<%=(i * 20)%>" class="paginas"><%=i + 1%></a>
                <% } //Fin if%>
                
                <% if (i == pagActual) {%> 

                    <%="<span class='paginas paginaActual'>" + (i + 1) + "</span>"%>
                <% } //Fin if%>
            <%} //Fin for%>

            <% if (numPaginas != (pagActual+1)) { //pagActual empieza en 0, por eso sumamos 1%>
                <a href="Listado?inicio=<%=(inicio + 20)%>" class="paginas" title="Siguiente">></a>
                <a href="Listado?inicio=<%=((numPaginas - 1) * 20)%>" class="paginas" title="Final">>></a>
            <% }%>
        </p>
    </center>
    <script src="assets/jquery.min.js" type="text/javascript"></script>
    <script>
        $(document).ready(function () {
            $("tr:odd").addClass("impar"); // filas impares
            $("tr:even").addClass("par"); // filas pares
        });
    </script>
</body>
</html>

