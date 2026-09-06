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
    <title>Servlet MVC Practice</title>
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
        <section class="hero">
            <p class="eyebrow">Bài tập 03</p>
            <h1>10 sản phẩm mới nhất</h1>
            <p>
                Danh sách được sắp xếp theo thời điểm tạo mới nhất.
            </p>
        </section>

        <section class="product-grid">
            <c:choose>
                <c:when test="${empty products}">
                    <div class="empty-state">
                        Chưa có sản phẩm để hiển thị.
                    </div>
                </c:when>

                <c:otherwise>
                    <c:forEach items="${products}" var="product">
                        <article class="product-card">
                            <div class="product-card-content">
                                <span class="category-badge">
                                    <c:out value="${product.category.name}"/>
                                </span>

                                <h2><c:out value="${product.name}"/></h2>

                                <p class="price">
                                    <fmt:formatNumber
                                        value="${product.price}"
                                        type="number"
                                        minFractionDigits="0"
                                        maxFractionDigits="2"/>
                                    đ
                                </p>

                                <p class="stock">
                                    Còn <c:out value="${product.quantity}"/>
                                    sản phẩm
                                </p>
                            </div>

                            <c:url value="/product/detail"
                                   var="detailUrl">
                                <c:param name="id"
                                         value="${product.id}"/>
                            </c:url>

                            <a href="${detailUrl}"
                               class="primary-link">
                                Xem chi tiết
                            </a>
                        </article>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </section>

        <div class="page-action">
            <a href="${pageContext.request.contextPath}/product"
               class="outline-link">
                Xem tất cả sản phẩm
            </a>
        </div>
    </main>
</body>
</html>
