<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Quản lý Category</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body>
    <header class="admin-header">
        <div class="container header-content">
            <h1>Quản lý Category</h1>

            <nav class="header-navigation">
                <a href="${pageContext.request.contextPath}/home">
                    Trang chủ
                </a>
                <a href="${pageContext.request.contextPath}/admin/products">
                    Product
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
                Thêm Category thành công.
            </div>
        </c:if>

        <c:if test="${param.updated == 'success'}">
            <div class="message success">
                Cập nhật Category thành công.
            </div>
        </c:if>

        <c:if test="${param.deleted == 'success'}">
            <div class="message success">
                Xóa Category thành công.
            </div>
        </c:if>

        <c:if test="${param.deleteBlocked == 'products'}">
            <div class="message error">
                Không thể xóa Category đang có Product.
            </div>
        </c:if>

        <c:if test="${not empty alert}">
            <div class="message error">
                <c:out value="${alert}"/>
            </div>
        </c:if>

        <section class="toolbar">
            <c:url value="/admin/categories"
                   var="listUrl"/>

            <form action="${listUrl}"
                  method="get"
                  class="search-form">

                <input
                    type="search"
                    name="keyword"
                    value="${fn:escapeXml(keyword)}"
                    maxlength="100"
                    placeholder="Tìm theo tên Category">

                <button type="submit">
                    Tìm kiếm
                </button>

                <c:if test="${not empty keyword}">
                    <a href="${listUrl}"
                       class="button secondary">
                        Xóa bộ lọc
                    </a>
                </c:if>
            </form>

            <c:url value="/admin/categories/create"
                   var="createUrl"/>

            <a href="${createUrl}"
               class="button primary">
                Thêm Category
            </a>
        </section>

        <section class="table-card">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Tên Category</th>
                        <th>Icon</th>
                        <th>Thao tác</th>
                    </tr>
                </thead>

                <tbody>
                    <c:choose>
                        <c:when test="${empty categories}">
                            <tr>
                                <td colspan="4"
                                    class="empty-state">
                                    Không có Category phù hợp.
                                </td>
                            </tr>
                        </c:when>

                        <c:otherwise>
                            <c:forEach
                                items="${categories}"
                                var="category">

                                <tr>
                                    <td>
                                        <c:out
                                            value="${category.id}"/>
                                    </td>

                                    <td>
                                        <c:out
                                            value="${category.name}"/>
                                    </td>

                                    <td data-label="Icon">
                                        <c:choose>
                                            <c:when test="${not empty category.icon}">
                                                <c:url value="/admin/category-icons" var="iconUrl">
                                                    <c:param name="id" value="${category.id}" />
                                                    <c:param name="v" value="${category.updatedAt}" />
                                                </c:url>

                                                <img
                                                    class="category-icon-thumbnail"
                                                    src="${iconUrl}"
                                                    alt="Icon ${fn:escapeXml(category.name)}"
                                                    width="64"
                                                    height="64"
                                                    loading="lazy"
                                                >
                                            </c:when>

                                            <c:otherwise>
                                                <span class="no-icon">Chưa có</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td class="actions">
                                        <c:url
                                            value="/admin/categories/edit"
                                            var="editUrl">
                                            <c:param
                                                name="id"
                                                value="${category.id}"/>
                                        </c:url>

                                        <a href="${editUrl}"
                                           class="button secondary">
                                            Sửa
                                        </a>

                                        <c:url
                                            value="/admin/categories/delete"
                                            var="deleteUrl"/>

                                        <form
                                            action="${deleteUrl}"
                                            method="post"
                                            class="inline-form"
                                            onsubmit="return confirm('Bạn chắc chắn muốn xóa Category này?');">

                                            <input
                                                type="hidden"
                                                name="id"
                                                value="${category.id}">

                                            <button
                                                type="submit"
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
