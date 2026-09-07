<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Cập nhật Profile</title>
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/profile.css"
    />
  </head>
  <body>
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-12 col-lg-9 col-xl-8">
          <div class="card border-0 shadow-sm overflow-hidden">
            <div class="row g-0">
              <div class="col-md-4 profile-summary p-4 text-center">
                <c:choose>
                  <c:when test="${not empty user.image}">
                    <img
                      class="profile-avatar"
                      src="${pageContext.request.contextPath}/session/profile/image"
                      alt="Ảnh đại diện của ${fn:escapeXml(user.fullName)}"
                    />
                  </c:when>
                  <c:otherwise>
                    <div class="profile-avatar profile-avatar-placeholder" aria-label="Chưa có ảnh đại diện">
                      <span>${fn:escapeXml(fn:substring(user.fullName, 0, 1))}</span>
                    </div>
                  </c:otherwise>
                </c:choose>

                <h1 class="h5 mt-3 mb-1">
                  <c:out value="${user.fullName}" />
                </h1>
                <p class="text-secondary mb-1">
                  @<c:out value="${user.username}" />
                </p>
                <span class="badge text-bg-primary">
                  <c:out value="${user.role}" />
                </span>
              </div>

              <div class="col-md-8 p-4 p-lg-5">
                <h2 class="h4 mb-1">Thông tin cá nhân</h2>
                <p class="text-secondary mb-4">
                  Cập nhật họ tên, số điện thoại và ảnh đại diện.
                </p>

                <c:if test="${not empty success}">
                  <div class="alert alert-success" role="alert">
                    <c:out value="${success}" />
                  </div>
                </c:if>

                <c:if test="${not empty alert}">
                  <div class="alert alert-danger" role="alert">
                    <c:out value="${alert}" />
                  </div>
                </c:if>

                <form
                  action="${pageContext.request.contextPath}/session/profile"
                  method="post"
                  enctype="multipart/form-data"
                >
                  <div class="mb-3">
                    <label for="fullName" class="form-label">Họ và tên</label>
                    <input
                      id="fullName"
                      name="fullName"
                      type="text"
                      class="form-control"
                      value="${fn:escapeXml(not empty formFullName ? formFullName : user.fullName)}"
                      minlength="2"
                      maxlength="100"
                      required
                    />
                    <div class="form-text">Từ 2 đến 100 ký tự.</div>
                  </div>

                  <div class="mb-3">
                    <label for="phone" class="form-label">Số điện thoại</label>
                    <input
                      id="phone"
                      name="phone"
                      type="tel"
                      class="form-control"
                      value="${fn:escapeXml(not empty formPhone ? formPhone : user.phone)}"
                      maxlength="20"
                      pattern="\+?[0-9 .-]{8,20}"
                      inputmode="tel"
                      placeholder="Ví dụ: +84901234567"
                    />
                    <div class="form-text">
                      Không bắt buộc; 8-15 chữ số, có thể bắt đầu bằng dấu +.
                    </div>
                  </div>

                  <div class="mb-4">
                    <label for="image" class="form-label">Ảnh đại diện</label>
                    <input
                      id="image"
                      name="image"
                      type="file"
                      class="form-control"
                      accept="image/jpeg,image/png"
                    />
                    <div class="form-text">
                      JPG hoặc PNG thật, tối đa 2 MB. Bỏ trống để giữ ảnh hiện tại.
                    </div>
                  </div>

                  <dl class="row small text-secondary mb-4">
                    <dt class="col-sm-3">Email</dt>
                    <dd class="col-sm-9"><c:out value="${user.email}" /></dd>
                    <dt class="col-sm-3">Tài khoản</dt>
                    <dd class="col-sm-9"><c:out value="${user.username}" /></dd>
                  </dl>

                  <button type="submit" class="btn btn-primary">
                    Lưu thay đổi
                  </button>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>
