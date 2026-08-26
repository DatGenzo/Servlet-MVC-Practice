package com.thanhdat.servletmvc.controllers;

import java.io.IOException;
import java.util.List;

import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.models.Category;
import com.thanhdat.servletmvc.services.CategoryService;
import com.thanhdat.servletmvc.services.impl.CategoryServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(
        name = "categoryListController",
        urlPatterns = "/admin/categories"
)
public class CategoryListController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private transient CategoryService categoryService;

    @Override
    public void init() {
        categoryService = new CategoryServiceImpl();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String keyword = request.getParameter("keyword");

        try {
            List<Category> categories =
                    categoryService.search(keyword);

            request.setAttribute(
                    "categories",
                    categories
            );

            request.setAttribute(
                    "keyword",
                    keyword == null ? "" : keyword
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/admin/categories/list.jsp"
            ).forward(request, response);
        } catch (DataAccessException exception) {
            getServletContext().log(
                    "Không thể tải danh sách Category.",
                    exception
            );

            response.sendError(
                    HttpServletResponse
                            .SC_INTERNAL_SERVER_ERROR
            );
        }
    }
}