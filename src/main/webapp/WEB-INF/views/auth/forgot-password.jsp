<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Quên mật khẩu</title>
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/auth.css"
    />
  </head>
  <body>
    <main class="auth-container">
      <section class="auth-card">
        <h1>Quên mật khẩu</h1>

        <p class="description">
          Nhập email đã đăng ký để nhận mã OTP đặt lại mật khẩu.
        </p>

        <c:if test="${param.authorization == 'expired'}">
          <div class="message error">
            Phiên đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.
          </div>
        </c:if>

        <c:if test="${not empty alert}">
          <div class="message error">
            <c:out value="${alert}" />
          </div>
        </c:if>

        <c:url value="/forgot-password" var="forgotPasswordUrl" />

        <form action="${forgotPasswordUrl}" method="post">
          <div class="form-group">
            <label for="email">Email</label>
            <input
              id="email"
              name="email"
              type="email"
              value="${fn:escapeXml(email)}"
              autocomplete="email"
              maxlength="100"
              required
            />
          </div>

          <button type="submit">Gửi mã OTP</button>
        </form>

        <div class="auth-links">
          <a href="${pageContext.request.contextPath}/session/login">
            Quay lại đăng nhập
          </a>
        </div>
      </section>
    </main>
  </body>
</html>
