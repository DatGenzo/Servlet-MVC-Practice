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

    <title><c:out value="${pageTitle}"/></title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body>
    <header class="admin-header">
        <div class="container header-content">
            <h1><c:out value="${pageTitle}"/></h1>
        </div>
    </header>

    <main class="container narrow-container">
        <section class="form-card">
            <c:if test="${not empty alert}">
                <div class="message error">
                    <c:out value="${alert}"/>
                </div>
            </c:if>

            <c:url value="${formAction}" var="productFormUrl"/>

            <form action="${productFormUrl}" method="post">
                <c:if test="${editMode}">
                    <input type="hidden"
                           name="id"
                           value="${product.id}">
                </c:if>

                <div class="form-group">
                    <label for="name">Tên Product</label>
                    <input id="name"
                           name="name"
                           type="text"
                           value="${fn:escapeXml(product.name)}"
                           maxlength="150"
                           required>
                </div>

                <div class="form-group">
                    <label for="categoryId">Category</label>
                    <select id="categoryId"
                            name="categoryId"
                            required>
                        <option value="">-- Chọn Category --</option>

                        <c:forEach items="${categories}" var="category">
                            <c:choose>
                                <c:when test="${not empty product.category and product.category.id == category.id}">
                                    <option value="${category.id}" selected>
                                        <c:out value="${category.name}"/>
                                    </option>
                                </c:when>
                                <c:otherwise>
                                    <option value="${category.id}">
                                        <c:out value="${category.name}"/>
                                    </option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="price">Giá</label>
                        <input id="price"
                               name="price"
                               type="number"
                               value="${product.price}"
                               min="0"
                               max="9999999999999.99"
                               step="0.01"
                               required>
                    </div>

                    <div class="form-group">
                        <label for="quantity">Số lượng</label>
                        <input id="quantity"
                               name="quantity"
                               type="number"
                               value="${product.quantity}"
                               min="0"
                               step="1"
                               required>
                    </div>
                </div>

                <div class="form-group">
                    <label for="description">Mô tả</label>
                    <textarea id="description"
                              name="description"
                              rows="6"
                              maxlength="2000"><c:out value="${product.description}"/></textarea>
                    <small class="file-hint">
                        Tối đa 2000 ký tự.
                    </small>
                </div>

                <div class="form-actions">
                    <button type="submit" class="button primary">
                        <c:out value="${submitLabel}"/>
                    </button>

                    <a href="${pageContext.request.contextPath}/admin/products"
                       class="button secondary">
                        Quay lại
                    </a>
                </div>
            </form>
        </section>
    </main>
</body>
</html>
