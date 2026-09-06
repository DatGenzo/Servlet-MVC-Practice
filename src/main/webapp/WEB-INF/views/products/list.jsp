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
    <title>Tất cả sản phẩm</title>
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
        <section class="page-heading">
            <div>
                <p class="eyebrow">Danh mục công khai</p>
                <h1>Tất cả sản phẩm</h1>
            </div>

            <p class="result-count">
                Tổng cộng
                <strong><c:out value="${pageResult.totalItems}"/></strong>
                sản phẩm
            </p>
        </section>

        <section class="product-grid">
            <c:choose>
                <c:when test="${empty pageResult.items}">
                    <div class="empty-state">
                        Chưa có sản phẩm để hiển thị.
                    </div>
                </c:when>

                <c:otherwise>
                    <c:forEach items="${pageResult.items}" var="product">
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

        <nav class="pagination" aria-label="Phân trang sản phẩm">
            <c:if test="${pageResult.hasPrevious}">
                <c:url value="/product" var="previousUrl">
                    <c:param name="page"
                             value="${pageResult.previousPage}"/>
                </c:url>
                <a href="${previousUrl}" class="page-link">
                    Trước
                </a>
            </c:if>

            <c:forEach begin="1"
                       end="${pageResult.totalPages}"
                       var="pageNumber">
                <c:url value="/product" var="pageUrl">
                    <c:param name="page" value="${pageNumber}"/>
                </c:url>

                <c:choose>
                    <c:when test="${pageNumber == pageResult.page}">
                        <span class="page-link active">
                            <c:out value="${pageNumber}"/>
                        </span>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageUrl}" class="page-link">
                            <c:out value="${pageNumber}"/>
                        </a>
                    </c:otherwise>
                </c:choose>
            </c:forEach>

            <c:if test="${pageResult.hasNext}">
                <c:url value="/product" var="nextUrl">
                    <c:param name="page"
                             value="${pageResult.nextPage}"/>
                </c:url>
                <a href="${nextUrl}" class="page-link">
                    Sau
                </a>
            </c:if>
        </nav>
    </main>
</body>
</html>
