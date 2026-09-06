<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Xác nhận OTP đặt lại mật khẩu</title>
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/auth.css"
    />
  </head>
  <body>
    <main class="auth-container">
      <section class="auth-card">
        <h1>Xác nhận OTP</h1>

        <p class="description">
          Nếu email phù hợp với một tài khoản đang hoạt động,
          hệ thống đã gửi mã OTP. Hãy kiểm tra hộp thư Mailtrap.
        </p>

        <c:if test="${param.requested == 'success'}">
          <div class="message success">
            Yêu cầu đã được tiếp nhận.
          </div>
        </c:if>

        <c:if test="${param.resent == 'success'}">
          <div class="message success">
            Yêu cầu gửi lại mã đã được tiếp nhận.
          </div>
        </c:if>

        <c:if test="${not empty alert}">
          <div class="message error">
            <c:out value="${alert}" />
          </div>
        </c:if>

        <c:url value="/reset-password/verify" var="verifyUrl" />

        <form action="${verifyUrl}" method="post">
          <div class="form-group">
            <label for="otp">Mã OTP gồm 6 chữ số</label>
            <input
              id="otp"
              name="otp"
              type="text"
              inputmode="numeric"
              autocomplete="one-time-code"
              minlength="6"
              maxlength="6"
              pattern="[0-9]{6}"
              required
            />
          </div>

          <button type="submit">Xác nhận mã</button>
        </form>

        <c:url
          value="/reset-password/verify/resend"
          var="resendUrl"
        />

        <form action="${resendUrl}" method="post" class="secondary-form">
          <button type="submit" class="secondary-button">
            Gửi lại mã OTP
          </button>
        </form>

        <div class="auth-links">
          <a href="${pageContext.request.contextPath}/forgot-password">
            Dùng email khác
          </a>
        </div>
      </section>
    </main>
  </body>
</html>
