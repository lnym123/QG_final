package com.Controller.old;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/upload")
@MultipartConfig // 启用文件上传功能
public class FileUploadServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "C://Users//y//Desktop//untitled2"; // 替换为实际的上传目录路径
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 2; // 限制文件大小为2MB


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            // 处理非多部分请求或返回错误响应
            return;
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(MAX_FILE_SIZE); // 设置临时文件阈值
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        ServletFileUpload uploadHandler = new ServletFileUpload(factory);
        uploadHandler.setSizeMax(MAX_FILE_SIZE); // 设置最大允许上传文件大小
        try {
            List<FileItem> items = uploadHandler.parseRequest(request);
            for (FileItem item : items) {
                if (!item.isFormField() && item.getFieldName().equals("avatar")) { // 检查是否为期望的文件字段
                    InputStream inputStream = item.getInputStream();
                    String fileName = item.getName();
                    String contentType = item.getContentType();
                    long contentLength = item.getSize();

                    // 保存文件到服务器
                    File uploadedFile = new File(UPLOAD_DIR, fileName);
                    Files.copy(inputStream, uploadedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    // 将文件信息（如路径或ID）保存到数据库
                    saveFileToDatabase(uploadedFile.getAbsolutePath(), contentType, contentLength, request.getParameter("userId")); // 假设从请求中获取用户ID
                    break;
                }else if (item.isFormField() && item.getFieldName().equals("username")) { // 处理非文件字段（如username）
                    String username = item.getString(); // 获取username字段的值
                    System.out.println(username);
                }
            }

            response.getWriter().println("{ \"success\": true }");
        } catch (FileUploadException | SQLException e) {
            response.getWriter().println("{ \"success\": false, \"message\": \"" + e.getMessage() + "\" }");
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    private void saveFileToDatabase(String filePath, String contentType, long contentLength, String userId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // 替换为实际的数据库驱动类名
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            System.out.println(filePath);
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db1", "root", "1234"); // 替换为实际的数据库连接信息
            String sql = "UPDATE tb_user SET avatar_url = ? WHERE username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, filePath);
            stmt.setString(2, "zhangsan");
            stmt.executeUpdate();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
    
}