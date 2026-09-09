<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Đăng ký tài khoản</title>
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/auth.css"
    />
  </head>
  <body>
    <main class="auth-container">
      <section class="auth-card auth-card-wide">
        <h1>Đăng ký tài khoản</h1>

        <p class="description">
          Tài khoản cần được kích hoạt bằng mã OTP gửi qua email.
        </p>

        <c:if test="${not empty alert}">
          <div class="message error">
            <c:out value="${alert}" />
          </div>
        </c:if>

        <c:url value="/register" var="registerUrl" />

        <form action="${registerUrl}" method="post">
          <div class="form-group">
            <label for="username">Tên đăng nhập</label>
            <input
              id="username"
              name="username"
              type="text"
              value="${fn:escapeXml(username)}"
              minlength="3"
              maxlength="50"
              pattern="[A-Za-z0-9._-]{3,50}"
              title="Chỉ dùng chữ, số, dấu chấm, gạch dưới hoặc gạch ngang"
              autocomplete="username"
              required
            />
          </div>

          <div class="form-group">
            <label for="fullName">Họ tên</label>
            <input
              id="fullName"
              name="fullName"
              type="text"
              value="${fn:escapeXml(fullName)}"
              minlength="2"
              maxlength="100"
              autocomplete="name"
              required
            />
          </div>

          <div class="form-group">
            <label for="email">Email</label>
            <input
              id="email"
              name="email"
              type="email"
              value="${fn:escapeXml(email)}"
              maxlength="100"
              autocomplete="email"
              required
            />
          </div>

          <div class="form-group">
            <label for="password">Mật khẩu</label>
            <input
              id="password"
              name="password"
              type="password"
              minlength="8"
              maxlength="72"
              autocomplete="new-password"
              required
            />
          </div>

          <div class="form-group">
            <label for="confirmedPassword">Xác nhận mật khẩu</label>
            <input
              id="confirmedPassword"
              name="confirmedPassword"
              type="password"
              minlength="8"
              maxlength="72"
              autocomplete="new-password"
              required
            />
          </div>

          <button type="submit">Đăng ký</button>
        </form>

        <div class="auth-links">
          <a href="${pageContext.request.contextPath}/session/login">
            Đã có tài khoản? Đăng nhập
          </a>
        </div>
      </section>
    </main>
  </body>
</html>
