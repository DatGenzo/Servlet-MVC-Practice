<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Kích hoạt tài khoản</title>
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/auth.css"
    />
  </head>
  <body>
    <main class="auth-container">
      <section class="auth-card">
        <h1>Kích hoạt tài khoản</h1>

        <p class="description">
          Nhập mã OTP 6 chữ số đã gửi tới
          <strong><c:out value="${pendingEmail}" /></strong>.
        </p>

        <c:if test="${param.sent == 'success'}">
          <div class="message success">
            Đăng ký thành công. Mã OTP đã được gửi.
          </div>
        </c:if>

        <c:if test="${param.resent == 'success'}">
          <div class="message success">
            Mã OTP mới đã được gửi.
          </div>
        </c:if>

        <c:if test="${param.mailError == 'true'}">
          <div class="message error">
            Tài khoản đã được tạo nhưng chưa gửi được email.
            Hãy thử nút gửi lại mã.
          </div>
        </c:if>

        <c:if test="${not empty alert}">
          <div class="message error">
            <c:out value="${alert}" />
          </div>
        </c:if>

        <c:url value="/account/activate" var="activateUrl" />
        <c:url
          value="/account/activate/resend"
          var="resendUrl"
        />

        <form action="${activateUrl}" method="post">
          <div class="form-group">
            <label for="otp">Mã OTP</label>
            <input
              id="otp"
              name="otp"
              type="text"
              inputmode="numeric"
              pattern="[0-9]{6}"
              minlength="6"
              maxlength="6"
              autocomplete="one-time-code"
              required
            />
          </div>

          <button type="submit">Kích hoạt tài khoản</button>
        </form>

        <form
          action="${resendUrl}"
          method="post"
          class="secondary-form"
        >
          <button type="submit" class="secondary-button">
            Gửi lại mã OTP
          </button>
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
