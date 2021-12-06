/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.security;

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