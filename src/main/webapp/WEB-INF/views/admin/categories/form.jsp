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
            <h1>
                <c:out value="${pageTitle}"/>
            </h1>
        </div>
    </header>

    <main class="container narrow-container">
        <section class="form-card">
            <c:if test="${not empty alert}">
                <div class="message error">
                    <c:out value="${alert}"/>
                </div>
            </c:if>

            <c:url value="${formAction}"
                   var="categoryFormUrl"/>

            <form action="${categoryFormUrl}"
                  method="post" enctype="multipart/form-data">

                <c:if test="${editMode}">
                    <input
                        type="hidden"
                        name="id"
                        value="${category.id}">
                </c:if>

                <div class="form-group">
                    <label for="name">
                        Tên Category
                    </label>

                    <input
                        id="name"
                        name="name"
                        type="text"
                        value="${fn:escapeXml(category.name)}"
                        maxlength="100"
                        required>
                </div>

                <div class="form-group">
                    <label for="icon">Icon danh mục</label>

                    <c:if test="${editMode and not empty category.icon}">
                        <c:url value="/admin/category-icons" var="currentIconUrl">
                            <c:param name="id" value="${category.id}" />
                            <c:param name="v" value="${category.updatedAt}" />
                        </c:url>

                        <div class="current-icon">
                            <p class="current-icon-label">Icon hiện tại:</p>

                            <img
                                class="category-icon-preview"
                                src="${currentIconUrl}"
                                alt="Icon ${fn:escapeXml(category.name)}"
                                width="160"
                                height="160"
                            >

                            <p class="file-hint">
                                Chỉ chọn ảnh mới nếu bạn muốn thay thế icon hiện tại.
                            </p>
                        </div>
                    </c:if>

                    <input
                        class="file-input"
                        id="icon"
                        name="icon"
                        type="file"
                        accept=".jpg,.jpeg,.png,image/jpeg,image/png"
                        aria-describedby="iconHelp"
                    >

                    <small id="iconHelp" class="file-hint">
                        Chấp nhận ảnh JPG hoặc PNG, dung lượng tối đa 2 MB.
                    </small>
                </div>

                <div class="form-actions">
                    <button type="submit"
                            class="button primary">
                        <c:out value="${submitLabel}"/>
                    </button>

                    <c:url value="/admin/categories"
                           var="backUrl"/>

                    <a href="${backUrl}"
                       class="button secondary">
                        Quay lại
                    </a>
                </div>
            </form>
        </section>
    </main>
</body>
</html>