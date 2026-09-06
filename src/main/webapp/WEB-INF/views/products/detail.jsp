<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">
    <title><c:out value="${product.name}"/></title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/products.css">
</head>
<body>
    <header class="site-header">
        <div class="site-container header-content">
            <a class="brand"
               href="${pageContext.request.contextPath}/home">
                Servlet MVC Practice
            </a>

            <nav class="site-navigation">
                <a href="${pageContext.request.contextPath}/home">
                    Trang chủ
                </a>
                <a href="${pageContext.request.contextPath}/product">
                    Sản phẩm
                </a>
                <c:choose>
                    <c:when test="${not empty sessionScope.authenticatedUser}">
                        <a href="${pageContext.request.contextPath}/session/profile">
                            Tài khoản
                        </a>
                        <c:if test="${sessionScope.authenticatedUser.role == 'ADMIN'}">
                            <a href="${pageContext.request.contextPath}/admin/products">
                                Quản trị
                            </a>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/session/login">
                            Đăng nhập
                        </a>
                    </c:otherwise>
                </c:choose>
            </nav>
        </div>
    </header>

    <main class="site-container page-content">
        <article class="product-detail">
            <span class="category-badge">
                <c:out value="${product.category.name}"/>
            </span>

            <h1><c:out value="${product.name}"/></h1>

            <p class="detail-price">
                <fmt:formatNumber
                    value="${product.price}"
                    type="number"
                    minFractionDigits="0"
                    maxFractionDigits="2"/>
                đ
            </p>

            <dl class="product-metadata">
                <dt>Mã sản phẩm</dt>
                <dd><c:out value="${product.id}"/></dd>

                <dt>Số lượng</dt>
                <dd><c:out value="${product.quantity}"/></dd>

                <dt>Ngày tạo</dt>
                <dd><c:out value="${product.createdAt}"/></dd>
            </dl>

            <section class="description-block">
                <h2>Mô tả</h2>
                <c:choose>
                    <c:when test="${empty product.description}">
                        <p>Sản phẩm chưa có mô tả.</p>
                    </c:when>
                    <c:otherwise>
                        <p><c:out value="${product.description}"/></p>
                    </c:otherwise>
                </c:choose>
            </section>

            <a href="${pageContext.request.contextPath}/product"
               class="outline-link">
                Quay lại danh sách
            </a>
        </article>
    </main>
</body>
</html>
