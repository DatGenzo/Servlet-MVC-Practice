<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="requestUri" value="${pageContext.request.requestURI}" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title><sitemesh:write property="title" /></title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <style>
      .sitemesh-page > .site-header,
      .sitemesh-page > .admin-header {
        display: none !important;
      }
    </style>
    <sitemesh:write property="head" />
  </head>
  <body class="bg-body-tertiary d-flex flex-column min-vh-100">
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
      <div class="container-fluid px-3 px-lg-4">
        <a class="navbar-brand fw-semibold" href="${contextPath}/home">
          Servlet MVC Practice
        </a>
        <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#mainNavbar"
          aria-controls="mainNavbar"
          aria-expanded="false"
          aria-label="Mở menu"
        >
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="mainNavbar">
          <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            <li class="nav-item">
              <a
                class="nav-link ${fn:endsWith(requestUri, '/home') ? 'active' : ''}"
                href="${contextPath}/home"
              >Trang chủ</a>
            </li>
            <li class="nav-item">
              <a
                class="nav-link ${fn:contains(requestUri, '/product') && !fn:contains(requestUri, '/admin/') ? 'active' : ''}"
                href="${contextPath}/product"
              >Sản phẩm</a>
            </li>
            <c:if test="${not empty sessionScope.authenticatedUser}">
              <li class="nav-item">
                <a
                  class="nav-link ${fn:contains(requestUri, '/session/profile') ? 'active' : ''}"
                  href="${contextPath}/session/profile"
                >
                  Profile
                </a>
              </li>
            </c:if>
            <c:if test="${sessionScope.authenticatedUser.role == 'ADMIN'}">
              <li class="nav-item">
                <a
                  class="nav-link ${fn:contains(requestUri, '/admin/categories') ? 'active' : ''}"
                  href="${contextPath}/admin/categories"
                >
                  Categories
                </a>
              </li>
              <li class="nav-item">
                <a
                  class="nav-link ${fn:contains(requestUri, '/admin/products') ? 'active' : ''}"
                  href="${contextPath}/admin/products"
                >
                  Products
                </a>
              </li>
            </c:if>
          </ul>
          <c:if test="${not empty sessionScope.authenticatedUser}">
            <span class="navbar-text text-white me-3">
              <c:out value="${sessionScope.authenticatedUser.fullName}" />
            </span>
            <form action="${contextPath}/session/logout" method="post">
              <button class="btn btn-outline-light btn-sm" type="submit">Đăng xuất</button>
            </form>
          </c:if>
          <c:if test="${empty sessionScope.authenticatedUser}">
            <div class="d-flex gap-2">
              <a class="btn btn-outline-light btn-sm" href="${contextPath}/session/login">
                Đăng nhập
              </a>
              <a class="btn btn-light btn-sm text-primary" href="${contextPath}/register">
                Đăng ký
              </a>
            </div>
          </c:if>
        </div>
      </div>
    </nav>

    <div class="sitemesh-page flex-grow-1">
      <sitemesh:write property="body" />
    </div>

    <footer class="border-top bg-white py-3 mt-auto">
      <div class="container-fluid px-3 px-lg-4 text-center text-secondary small">
        Bài tập Servlet MVC — SiteMesh 3 và Bootstrap
      </div>
    </footer>

    <script
      src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
    ></script>
  </body>
</html>
