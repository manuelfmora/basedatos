<%-- 
    Document   : consultas
    Created on : 14-jun-2016, 23:38:31
    Author     : jose
--%>

<% 
    // consuñta que devuelve el numero de veces
ResultSet registros = s.executeQuery ("SELECT count(*) as total FROM salud_centros;");//no

// Consulta que devuelve paginado los centros----------->>si
ResultSet listado = s.executeQuery ("SELECT c.NOMBRE as nombre,c.DIRECCION as direccion,"//si
        + "C.LOCALIDAD as localidad,p.NOMBRE as provincia FROM"
        + " salud_centros c join municipios m on c.CODMU=m.CODMU "
        + "join provincias p on m.CODPROV=p.IDPROV  limit "+pagina+", 20;");

// consulta que devulve todos los municipios que hay----------->>si
ResultSet registros = s.executeQuery ("SELECT count(distinct CODMU) as total FROM salud_centros;");

//COnsulta que devuelve el nombre y el codigo de las provincias para meterlos en el selec
// para seleccionar la provincia que quieres ver---------------------->>>no
ResultSet provin = s.executeQuery ("select NOMBRE,IDPROV from provincias;");

//Consulta que devuelve el numero total de centro que hay por provincias
// esta sirve para calcular despues el pocentaje
ResultSet provincias = s.executeQuery ("select count(*) as total from "
        + "salud_centros c join municipios m on c.CODMU=m.CODMU group by m.CODPROV;");

// COnsulta que devuelve todo lo que necesitas para la paginacion por municipios
ResultSet listado = s.executeQuery ("select MUNICIPIO,count(*) as total, "
        + "m.CODPROV as codigo from salud_centros c join municipios m on "
        + "c.CODMU=m.CODMU join provincias p on "
        + "m.CODPROV=p.IDPROV group by c.CODMU limit "+pagina+", 20;");

// consulta que devulve todos los municipios que hay en la privincia seleccionada en el selec
ResultSet registros = s.executeQuery ("SELECT count(distinct c.CODMU) as total "
        + "FROM salud_centros c join "
        + "municipios m on c.CODMU=m.CODMU where m.CODPROV='"+provincia+"';");

//Consulta que devuelve el numero total de centro que hay en la provincias del selec
ResultSet provincias = s.executeQuery ("select count(*) as total "
        + "from salud_centros c join municipios m "
        + "on c.CODMU=m.CODMU where m.CODPROV='"+provincia+"';");

// COnsulta que devuelve todo lo que necesitas para la paginacion por municipios de esa provincia
ResultSet listado = s.executeQuery ("select MUNICIPIO,count(*) as "
        + "total, m.CODPROV as codigo "
        + "from salud_centros c join municipios m "
        + "on c.CODMU=m.CODMU join provincias p "
        + "on m.CODPROV=p.IDPROV "
        + "where m.CODPROV='"+provincia+"' group by c.CODMU limit "+pagina+", 20;");

//PD si vas a utilizar estas consultas camvia los alias para que no se de cuenta
%>