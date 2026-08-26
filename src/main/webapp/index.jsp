<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Servlet MVC Practice</title>
</head>
<body>
    <main>
        <h1>Servlet MVC Practice</h1>

        <p>Project đã được cấu hình thành công.</p>

        <ul>
            <li>Java: 17</li>
            <li>Jakarta Servlet: 6.1</li>
            <li>Jakarta JSP: 4.0</li>
            <li>Tomcat: 11</li>
            <li>Packaging: WAR</li>
        </ul>

        <p>
            Context path:
            <strong>${pageContext.request.contextPath}</strong>
        </p>
    </main>
</body>
</html>