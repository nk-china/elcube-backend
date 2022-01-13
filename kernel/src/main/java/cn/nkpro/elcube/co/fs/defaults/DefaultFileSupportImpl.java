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
package cn.nkpro.elcube.co.fs.defaults;

import cn.nkpro.elcube.basic.NkProperties;
import cn.nkpro.elcube.co.fs.FSConfig;
import cn.nkpro.elcube.co.fs.FSSupport;
import cn.nkpro.elcube.co.fs.FileUploadStatus;
import cn.nkpro.elcube.security.UserAccountService;
import cn.nkpro.elcube.utils.UUIDHexGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultFileSupportImpl implements FSSupport<Map<String, Object>> {

    private Format format = new SimpleDateFormat(String.format("yyyyMMdd%sHHmmss", File.separator));

    @Autowired
    private NkProperties properties;

    @Autowired
    private UserAccountService accountService;

    @Override
    public ResponseEntity<Map<String, Object>> init(FSConfig file) {

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("NK-Token",accountService.createToken().get("accessToken"));
        header.put("key", "${filename}");


        Map<String, Object> response = new LinkedHashMap<>();
        response.put("host","/api/fs/upload");
        response.put("path","");
        response.put("filename","");
        response.put("header",header);

        return ResponseEntity.ok(response);
    }

    @Override
    public FileUploadStatus upload(MultipartFile file){

        FileUploadStatus status = new FileUploadStatus();
        OutputStream os = null;
        InputStream is = null;
        try {
            String url = String.join(
                    File.separator,
                    format.format(new Date()),
                    UUIDHexGenerator.generate(),
                    file.getOriginalFilename()
            ).replace("\\","/");
            String path = String.join(
                    File.separator,
                    properties.getFileRootPath(),
                    url
            ).replace("\\","/");
            File targetFile = new File(path);
            if(targetFile.getParentFile().mkdirs()){
                os=new FileOutputStream(targetFile);
                is=file.getInputStream();
                byte[] b=new byte[1024];
                //一个一个字节的读取并写入
                while(is.read(b) !=(-1)){
                    os.write(b);
                }
                status.setName(file.getOriginalFilename());
                status.setStatus("done");
                status.setUrl(url);
                status.setThumbUrl(url);
            }else{
                status.setStatus("error");
                status.setMessage("创建文件目录发生错误");
            }
        } catch (IOException e) {
            status.setStatus("error");
            status.setMessage(e.getMessage());
        }finally {
            if(os!=null){
                try {
                    os.flush();
                    os.close();
                } catch (IOException ignored) {}
            }
            if(is!=null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
        return status;
    }

    @Override
    public void moveTo(String source, String target) {

    }

    @Override
    public ResponseEntity download(String url) {
        return ResponseEntity.ok("/api/fs/d/"+url);
    }

    public ResponseEntity downloadFile(String url) {

        File file = new File(String.join(
                File.separator,
                properties.getFileRootPath(),
                url
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment",
                new String(file.getName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
        headers.setContentLength(file.length());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new FileSystemResource(file));
    }
}