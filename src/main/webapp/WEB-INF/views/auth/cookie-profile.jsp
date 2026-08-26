<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> <%@
taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />

    <title>Cookie Profile</title>

    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/auth.css"
    />
  </head>
  <body>
    <main class="auth-container">
      <section class="auth-card">
        <h1>Đăng nhập thành công</h1>

        <p>
          Xin chào,
          <strong>
            <c:out value="${username}" />
          </strong>
        </p>

        <p class="description">
          Username này được đọc từ Cookie của trình duyệt.
        </p>

        <c:url value="/cookie/logout" var="logoutUrl" />

        <form action="${logoutUrl}" method="post">
          <button type="submit" class="danger-button">
            Đăng xuất và xóa Cookie
          </button>
        </form>
      </section>
    </main>
  </body>
</html>
