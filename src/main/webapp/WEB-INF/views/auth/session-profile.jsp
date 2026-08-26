<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> <%@
taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />

    <title>Session Profile</title>

    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/auth.css"
    />
  </head>
  <body>
    <main class="auth-container">
      <section class="auth-card">
        <h1>Đăng nhập thành công</h1>

        <dl class="profile-information">
          <dt>Tài khoản</dt>
          <dd>
            <c:out value="${user.username}" />
          </dd>

          <dt>Họ tên</dt>
          <dd>
            <c:out value="${user.fullName}" />
          </dd>

          <dt>Email</dt>
          <dd>
            <c:out value="${user.email}" />
          </dd>

          <dt>Vai trò</dt>
          <dd>
            <c:out value="${user.role}" />
          </dd>
        </dl>

        <c:url value="/session/logout" var="logoutUrl" />

        <c:if test="${user.role == 'ADMIN'}">
          <c:url value="/admin/categories" var="categoryAdminUrl" />

          <p>
            <a href="${categoryAdminUrl}"> Đi đến quản lý Category </a>
          </p>
        </c:if>

        <form action="${logoutUrl}" method="post">
          <button type="submit" class="danger-button">
            Đăng xuất và hủy Session
          </button>
        </form>
      </section>
    </main>
  </body>
</html>
