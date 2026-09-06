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

    <title>Quản lý Product</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body>
    <header class="admin-header">
        <div class="container header-content">
            <h1>Quản lý Product</h1>

            <nav class="header-navigation">
                <a href="${pageContext.request.contextPath}/home">
                    Trang chủ
                </a>
                <a href="${pageContext.request.contextPath}/admin/categories">
                    Category
                </a>
                <a href="${pageContext.request.contextPath}/session/profile">
                    Tài khoản
                </a>
            </nav>
        </div>
    </header>

    <main class="container">
        <c:if test="${param.created == 'success'}">
            <div class="message success">
                Thêm Product thành công.
            </div>
        </c:if>

        <c:if test="${param.updated == 'success'}">
            <div class="message success">
                Cập nhật Product thành công.
            </div>
        </c:if>

        <c:if test="${param.deleted == 'success'}">
            <div class="message success">
                Xóa Product thành công.
            </div>
        </c:if>

        <section class="toolbar toolbar-end">
            <a href="${pageContext.request.contextPath}/admin/products/create"
               class="button primary">
                Thêm Product
            </a>
        </section>

        <section class="table-card">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Tên Product</th>
                        <th>Category</th>
                        <th>Giá</th>
                        <th>Số lượng</th>
                        <th>Thao tác</th>
                    </tr>
                </thead>

                <tbody>
                    <c:choose>
                        <c:when test="${empty products}">
                            <tr>
                                <td colspan="6" class="empty-state">
                                    Chưa có Product.
                                </td>
                            </tr>
                        </c:when>

                        <c:otherwise>
                            <c:forEach items="${products}" var="product">
                                <tr>
                                    <td><c:out value="${product.id}"/></td>
                                    <td><c:out value="${product.name}"/></td>
                                    <td><c:out value="${product.category.name}"/></td>
                                    <td>
                                        <fmt:formatNumber
                                            value="${product.price}"
                                            type="number"
                                            minFractionDigits="0"
                                            maxFractionDigits="2"/>
                                    </td>
                                    <td><c:out value="${product.quantity}"/></td>
                                    <td class="actions">
                                        <c:url value="/admin/products/edit"
                                               var="editUrl">
                                            <c:param name="id"
                                                     value="${product.id}"/>
                                        </c:url>

                                        <a href="${editUrl}"
                                           class="button secondary">
                                            Sửa
                                        </a>

                                        <c:url value="/admin/products/delete"
                                               var="deleteUrl"/>

                                        <form action="${deleteUrl}"
                                              method="post"
                                              class="inline-form"
                                              onsubmit="return confirm('Bạn chắc chắn muốn xóa Product này?');">
                                            <input type="hidden"
                                                   name="id"
                                                   value="${product.id}">
                                            <button type="submit"
                                                    class="button danger">
                                                Xóa
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </section>
    </main>
</body>
</html>
