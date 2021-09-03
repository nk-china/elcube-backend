package cn.nkpro.ts5.security;

import org.springframework.http.MediaType;
 import org.springframework.security.core.AuthenticationException;
 import org.springframework.security.web.AuthenticationEntryPoint;
 
 import javax.servlet.ServletException;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import java.io.IOException;
 import java.io.PrintWriter;

/**
  * @author dax
  * @since 2019/11/6 22:11
  */
 public class NkAuthenticationEntryPoint implements AuthenticationEntryPoint {

     @Override
     public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {

         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
         response.setCharacterEncoding("utf-8");
         response.setContentType(MediaType.APPLICATION_JSON_VALUE);
         PrintWriter printWriter = response.getWriter();
         printWriter.print(e.getMessage());
         printWriter.flush();
         printWriter.close();
     }
 }