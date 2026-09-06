<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> <%@
taglib prefix="c" uri="jakarta.tags.core" %> <%@ taglib prefix="fn"
uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />

    <title>Login với Session</title>

    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/auth.css"
    />
  </head>
  <body>
    <main class="auth-container">
      <section class="auth-card">
        <h1>Login với Session</h1>

        <p class="description">
          Tài khoản sau khi xác thực sẽ được lưu trong HttpSession.
        </p>

        <c:if test="${param.logout == 'success'}">
          <div class="message success">
            Đăng xuất và hủy Session thành công.
          </div>
        </c:if>

        <c:if test="${param.activated == 'success'}">
          <div class="message success">
            Kích hoạt tài khoản thành công. Bạn có thể đăng nhập.
          </div>
        </c:if>

        <c:if test="${param.reset == 'success'}">
          <div class="message success">
            Đặt lại mật khẩu thành công. Bạn có thể đăng nhập.
          </div>
        </c:if>

        <c:if test="${not empty alert}">
          <div class="message error">
            <c:out value="${alert}" />
          </div>
        </c:if>

        <c:url value="/session/login" var="loginUrl" />

        <form action="${loginUrl}" method="post">
          <div class="form-group">
            <label for="username"> Tài khoản </label>

            <input
              id="username"
              name="username"
              type="text"
              value="${fn:escapeXml(username)}"
              autocomplete="username"
              maxlength="50"
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
              required
            />
          </div>

          <button type="submit">Đăng nhập</button>
        </form>

        <div class="auth-links">
          <a href="${pageContext.request.contextPath}/home">
            Trang chủ
          </a>

          <a href="${pageContext.request.contextPath}/product">
            Danh sách sản phẩm
          </a>

          <a href="${pageContext.request.contextPath}/forgot-password">
            Quên mật khẩu?
          </a>

          <a href="${pageContext.request.contextPath}/register">
            Chưa có tài khoản? Đăng ký
          </a>
        </div>
      </section>
    </main>
  </body>
</html>
