<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> <%@
taglib prefix="c" uri="jakarta.tags.core" %> <%@ taglib prefix="fn"
uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />

    <title>Login với Cookie</title>

    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/auth.css"
    />
  </head>
  <body>
    <main class="auth-container">
      <section class="auth-card">
        <h1>Login với Cookie</h1>

        <p class="description">
          Đăng nhập và lưu username trong Cookie trong 30 phút.
        </p>

        <c:if test="${param.logout == 'success'}">
          <div class="message success">Đăng xuất và xóa Cookie thành công.</div>
        </c:if>

        <c:if test="${not empty alert}">
          <div class="message error">
            <c:out value="${alert}" />
          </div>
        </c:if>

        <c:url value="/cookie/login" var="loginUrl" />

        <form action="${loginUrl}" method="post">
          <div class="form-group">
            <label for="username"> Tài khoản </label>

            <input
              id="username"
              name="username"
              type="text"
              value="${fn:escapeXml(username)}"
              autocomplete="username"
              minlength="3"
              maxlength="50"
              pattern="[A-Za-z0-9._-]{3,50}"
              required
            />
          </div>

          <div class="form-group">
            <label for="password"> Mật khẩu </label>

            <input
              id="password"
              name="password"
              type="password"
              autocomplete="current-password"
              maxlength="72"
              required
            />
          </div>

          <button type="submit">Đăng nhập</button>
        </form>
      </section>
    </main>
  </body>
</html>
