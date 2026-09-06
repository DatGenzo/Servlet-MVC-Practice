<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Đặt lại mật khẩu</title>
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/auth.css"
    />
  </head>
  <body>
    <main class="auth-container">
      <section class="auth-card">
        <h1>Đặt lại mật khẩu</h1>

        <p class="description">
          Mật khẩu mới phải dài từ 8 đến 72 ký tự.
        </p>

        <c:if test="${not empty alert}">
          <div class="message error">
            <c:out value="${alert}" />
          </div>
        </c:if>

        <c:url value="/reset-password" var="resetUrl" />

        <form action="${resetUrl}" method="post">
          <div class="form-group">
            <label for="password">Mật khẩu mới</label>
            <input
              id="password"
              name="password"
              type="password"
              autocomplete="new-password"
              minlength="8"
              maxlength="72"
              required
            />
          </div>

          <div class="form-group">
            <label for="confirmedPassword">Xác nhận mật khẩu mới</label>
            <input
              id="confirmedPassword"
              name="confirmedPassword"
              type="password"
              autocomplete="new-password"
              minlength="8"
              maxlength="72"
              required
            />
          </div>

          <button type="submit">Lưu mật khẩu mới</button>
        </form>
      </section>
    </main>
  </body>
</html>
